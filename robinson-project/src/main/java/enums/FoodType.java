package enums;

public enum FoodType {
    MEAT("Мясо"),
    FRUIT("Фрукт");

    private final String russianName;

    FoodType(String russianName) {
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
