package enums;

public enum AnimalType {
    PARROT("Попугай"),
    FOX("Лисица"),
    GOAT("Коза"),
    TURTLE("Черепаха"),
    PIGEON("Голубь");

    private final String russianName;

    AnimalType(String russianName) {
        this.russianName = russianName;
    }

    public String getRussianName() {
        return russianName;
    }

    @Override
    public String toString() {
        return russianName;
    }
}
