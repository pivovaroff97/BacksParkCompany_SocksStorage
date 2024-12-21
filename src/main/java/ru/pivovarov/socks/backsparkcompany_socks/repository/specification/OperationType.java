package ru.pivovarov.socks.backsparkcompany_socks.repository.specification;

public enum OperationType {
    MORE_THAN("moreThan"),
    LESS_THAN("lessThan"),
    EQUAL("equal"),
    BETWEEN("between");

    private final String value;

    OperationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
