package nl.iobyte.framework.generic.service;

import nl.iobyte.framework.generic.exceptional.ExceptionalTask;
import nl.iobyte.framework.generic.reflections.objects.ReflectedType;
import nl.iobyte.framework.generic.service.enums.ServiceState;
import nl.iobyte.framework.generic.service.interfaces.Service;
import nl.iobyte.framework.generic.service.objects.ServiceContainer;
import nl.iobyte.framework.structures.dtree.DependencyTree;
import nl.iobyte.framework.structures.reflected.ReflectedMap;
import nl.iobyte.framework.structures.suppliers.IListSupplier;
import nl.iobyte.framework.structures.suppliers.IMapSupplier;
import nl.iobyte.framework.structures.suppliers.ISetSupplier;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class ServiceLoader {

    private final boolean abortOnException;
    private final AtomicReference<ServiceState> serviceState;
    private final Map<Class<? extends Service>, ServiceContainer<?>> serviceContainers;
    private final DependencyTree<Class<? extends Service>, ServiceContainer<?>> dependencyTree;

    public ServiceLoader(boolean abortOnException) {
        this(
                abortOnException,
                ReflectedMap.getMapSupplier(ConcurrentHashMap.class),
                ConcurrentHashMap::newKeySet,
                CopyOnWriteArrayList::new
        );
    }

    public ServiceLoader(
            boolean abortOnException,
            IMapSupplier mapSupplier,
            ISetSupplier setSupplier,
            IListSupplier listSupplier
    ) {
        this.abortOnException = abortOnException;
        this.serviceState = new AtomicReference<>(ServiceState.NONE);
        this.serviceContainers = mapSupplier.get();
        this.dependencyTree = new DependencyTree<>(this::getContainer, mapSupplier, setSupplier, listSupplier);
    }

    public <T extends Service> ServiceContainer<T> register(Class<T> service) {
        //noinspection unchecked
        return (ServiceContainer<T>) serviceContainers.computeIfAbsent(service, key -> {
            ServiceContainer<T> container = ServiceContainer.of(
                    this,
                    abortOnException,
                    ReflectedType.of(service)
            );

            dependencyTree.add(service);
            ServiceState state = serviceState.get();
            if(state.canInit())
                container.init();

            if(state.canStart())
                container.start();

            return container;
        });
    }

    /**
     * Register services
     *
     * @param services array of service classes
     */
    @SafeVarargs
    public final void registerBulk(Class<? extends Service>... services) {
        for(Class<? extends Service> service : services)
            register(service);
    }

    /**
     * Get service container of service type
     *
     * @param service type
     * @param <T>     type of service
     * @return service container instance
     */
    public <T extends Service> ServiceContainer<T> getContainer(Class<T> service) {
        //noinspection unchecked
        return (ServiceContainer<T>) serviceContainers.get(service);
    }

    /**
     * Get service instance
     *
     * @param service type
     * @param <T>     type of service
     * @return service instance
     */
    public <T extends Service> T get(Class<T> service) {
        return Optional.ofNullable(getContainer(service))
                       .map(ServiceContainer::getService)
                       .orElse(null);
    }

    /**
     * Add task to state for service
     *
     * @param service type
     * @param state   type
     * @param task    instance
     */
    public void addTask(Class<? extends Service> service, ServiceState state, ExceptionalTask task) {
        Optional.ofNullable(
                getContainer(service)
        ).ifPresent(
                serviceContainer -> serviceContainer.addTask(state, task)
        );
    }

    /**
     * Add dependency to service
     *
     * @param service    type
     * @param dependency service type
     * @param <T>        type of service
     * @param <R>        type of dependency
     */
    public <T extends Service, R extends Service> void addDependency(Class<T> service, Class<R> dependency) {
        ServiceContainer<T> serviceContainer = register(service);
        ServiceContainer<R> dependencyContainer = register(dependency);

        //Add dependency
        dependencyTree.addDependency(
                serviceContainer.getServiceType(),
                dependencyContainer.getServiceType()
        );
    }

    /**
     * Initialise services
     */
    public void init() {
        serviceContainers.values().forEach(ServiceContainer::init);
    }

    /**
     * Start services
     */
    public void start() {
        dependencyTree.traverseReverse(ServiceContainer::preStart);
        dependencyTree.traverseReverse(ServiceContainer::start);
        dependencyTree.traverseReverse(ServiceContainer::postStart);
    }

    /**
     * Stop services
     */
    public void stop() {
        dependencyTree.traverse(ServiceContainer::preStop);
        dependencyTree.traverse(ServiceContainer::stop);
        dependencyTree.traverse(ServiceContainer::postStop);
    }

}
