package nl.iobyte.framework.data.database.enums;

public enum Where {

    EQUALS("="),
    GREATER(">"),
    GREATER_OR_EQUAL(">="),
    LOWER("<"),
    LOWER_OR_EQUAL("<="),
    NOT_EQUALS("<>"),
    BETWEEN("BETWEEN"),
    NOT_BETWEEN("NOT BETWEEN"),
    IN("IN"),
    LIKE("LIKE"),
    NOT_IN("NOT IN");

    private final String operator;

    Where(String operator) {
        this.operator = operator;
    }

    /**
     * Return operator representation of enum
     *
     * @return operator
     */
    public String toString() {
        return operator;
    }

}
