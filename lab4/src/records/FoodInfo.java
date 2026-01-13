package records;

import enums.FoodType;

public record FoodInfo(String name, FoodType type, int nutritionalValue) {

    @Override
    public String toString() {
        return name + " [" + type.getRussianName() + ", питательность: " + nutritionalValue + "]";
    }
}
