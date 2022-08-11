package nl.iobyte.framework.generic.reflections.objects;

import nl.iobyte.framework.generic.reflections.interfaces.IReflected;

public abstract class AbstractReflected implements IReflected {

    private String snakeCaseName;

    @Override
    public String getSnakeCase() {
        if(snakeCaseName != null)
            return snakeCaseName;

        StringBuilder builder = new StringBuilder();
        char c;
        for(int i = 0; i < getName().length(); i++) {
            c = getName().charAt(i);
            if(i > 0 && Character.isUpperCase(c))
                if(!Character.isUpperCase(getName().charAt(i - 1)))
                    builder.append('_');

            builder.append(Character.toLowerCase(c));
        }

        return snakeCaseName = builder.toString();
    }

}
