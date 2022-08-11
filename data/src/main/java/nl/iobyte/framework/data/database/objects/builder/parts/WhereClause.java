package nl.iobyte.framework.data.database.objects.builder.parts;

import nl.iobyte.framework.data.database.enums.Where;

import java.util.List;

public record WhereClause(String column, Where operator, List<Object> values) {

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(commentName(column)).append(" ").append(operator).append(" ");
        switch(operator) {
            case BETWEEN, NOT_BETWEEN -> builder.append("? AND ?");
            case IN, NOT_IN -> {
                builder.append("(");

                boolean first = true;
                for(Object ignored : values) {
                    if(!first) {
                        builder.append(",");
                    }

                    first = false;
                    builder.append("?");
                }
            }
            default -> builder.append("?");
        }

        return builder.toString();
    }

    /**
     * Comment column name in string
     *
     * @param name column name
     * @return commented column name
     */
    private static String commentName(String name) {
        return "`" + name.replace(".", "`.`") + "`";
    }

}
