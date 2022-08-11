package nl.iobyte.framework.generic.service.objects;

import nl.iobyte.framework.generic.exceptional.ExceptionalTask;
import nl.iobyte.framework.generic.reflections.objects.ReflectedType;
import nl.iobyte.framework.generic.service.ServiceLoader;
import nl.iobyte.framework.generic.service.annotations.Inject;
import nl.iobyte.framework.generic.service.enums.ServiceState;
import nl.iobyte.framework.generic.service.interfaces.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ServiceContainer<T extends Service> {

    private final ServiceLoader serviceLoader;
    private final boolean abortOnException;

    private final ReflectedType<T> serviceType;
    private final AtomicReference<ServiceState> serviceState;
    private final AtomicReference<T> atomicService;
    private final List<Class<? extends Service>> dependencies;
    private final List<ExceptionalTask> preStart, postStart, preStop, postStop;

    public ServiceContainer(ServiceLoader serviceLoader, boolean abortOnException, ReflectedType<T> serviceType) {
        this.serviceLoader = serviceLoader;
        this.abortOnException = abortOnException;

        this.serviceType = serviceType;
        this.serviceState = new AtomicReference<>(ServiceState.NONE);
        this.atomicService = new AtomicReference<>(null);
        this.dependencies = new CopyOnWriteArrayList<>();

        this.preStart = new CopyOnWriteArrayList<>();
        this.postStart = new CopyOnWriteArrayList<>();
        this.preStop = new CopyOnWriteArrayList<>();
        this.postStop = new CopyOnWriteArrayList<>();
    }

    /**
     * Get type of service
     *
     * @return type of service
     */
    public Class<T> getServiceType() {
        return serviceType.getType();
    }

    /**
     * Get service instance
     *
     * @return service
     */
    public T getService() {
        return atomicService.get();
    }

    /**
     * Get state of service
     *
     * @return service state
     */
    public ServiceState getState() {
        return serviceState.get();
    }

    /**
     * Initialise service
     */
    public void init() {
        runState(
                ServiceState.NONE,
                ServiceState.INIT,
                () -> {
                    //Gather dependencies in service class
                    dependencies.addAll(getServiceDependencies(serviceType));
                    dependencies.forEach(type -> serviceLoader.addDependency(serviceType.getType(), type));

                    T service = serviceType.getConstructor().newInstance();
                    atomicService.set(service);
                }
        );
    }

    /**
     * Pre Start service
     */
    public void preStart() {
        runState(
                ServiceState.INIT,
                ServiceState.PRE_START,
                () -> runTaskSequence(preStart)
        );
    }

    /**
     * Start service
     */
    public void start() {
        runState(
                ServiceState.PRE_START,
                ServiceState.START,
                () -> {
                    T service = atomicService.get();
                    resolveServiceDependencies(service);
                    service.start();
                }
        );
    }

    /**
     * Post Start service
     */
    public void postStart() {
        runState(
                ServiceState.START,
                ServiceState.POST_START,
                () -> runTaskSequence(postStart)
        );
    }

    /**
     * Pre Stop service
     */
    public void preStop() {
        runState(
                ServiceState.POST_START,
                ServiceState.PRE_STOP,
                () -> runTaskSequence(preStop)
        );
    }

    /**
     * Stop service
     */
    public void stop() {
        runState(
                ServiceState.PRE_STOP,
                ServiceState.STOP,
                () -> atomicService.get().stop()
        );
    }

    /**
     * Post Stop service
     */
    public void postStop() {
        runState(
                ServiceState.STOP,
                ServiceState.POST_STOP,
                () -> runTaskSequence(postStop)
        );
    }

    /**
     * Add task to state
     *
     * @param state type
     * @param task  instance
     */
    public void addTask(ServiceState state, ExceptionalTask task) {
        switch(state) {
            case PRE_START -> preStart.add(task);
            case POST_START -> postStart.add(task);
            case PRE_STOP -> preStop.add(task);
            case POST_STOP -> postStop.add(task);
            default -> throw new IllegalArgumentException("cannot add task to state " + state);
        }
    }

    /**
     * Run task
     *
     * @param from previous service state
     * @param to   next service state
     * @param task that throws exception
     */
    private void runState(ServiceState from, ServiceState to, ExceptionalTask task) {
        if(!serviceState.compareAndSet(from, to))
            return;

        try {
            task.run();
        } catch(Exception e) {
            serviceState.set(from);
            if(abortOnException)
                throw new RuntimeException(e);

            //TODO Report what failed, e.printStackTrace();
        }
    }

    /**
     * Get service dependencies from type
     *
     * @param type reflected type instance
     * @return list of service types
     */
    private static List<Class<? extends Service>> getServiceDependencies(ReflectedType<?> type) {
        //noinspection unchecked
        return type.getFields()
                   .stream()
                   .filter(field -> field.hasAnnotation(Inject.class))
                   .map(field -> field.getField().getType())
                   .filter(Service.class::isAssignableFrom)
                   .map(fieldType -> (Class<? extends Service>) fieldType)
                   .distinct()
                   .collect(Collectors.toList());
    }

    /**
     * Resolve service fields for object
     *
     * @param obj Object instance
     */
    private void resolveServiceDependencies(Object obj) {
        ReflectedType.of(obj.getClass())
                     .getFields()
                     .stream()
                     .filter(field -> field.hasAnnotation(Inject.class))
                     .filter(field -> Service.class.isAssignableFrom(field.getRawType()))
                     .forEach(field -> {
                         //noinspection unchecked
                         Service service = serviceLoader.get((Class<? extends Service>) field.getRawType());
                         try {
                             field.setRawValue(obj, service);
                         } catch(IllegalAccessException e) {
                             if(abortOnException)
                                 throw new RuntimeException(e);

                             e.printStackTrace();
                         }
                     });
    }

    /**
     * Run sequence for tasks
     *
     * @param tasks list of exceptional tasks
     */
    private void runTaskSequence(List<ExceptionalTask> tasks) {
        for(ExceptionalTask task : tasks) {
            resolveServiceDependencies(task);

            try {
                task.run();
            } catch(Exception e) {
                if(abortOnException)
                    throw new RuntimeException(e);

                e.printStackTrace();
            }
        }
    }

    /**
     * Get service container instance
     *
     * @param serviceLoader    instance of service loader
     * @param abortOnException whether to abort when an exception occurs
     * @param type             of service
     * @param <T>              type of service
     * @return service container instance
     */
    public static <T extends Service> ServiceContainer<T> of(
            ServiceLoader serviceLoader,
            boolean abortOnException,
            ReflectedType<T> type
    ) {
        return new ServiceContainer<>(
                serviceLoader,
                abortOnException,
                type
        );
    }

}
