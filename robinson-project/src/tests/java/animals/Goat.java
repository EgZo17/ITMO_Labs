package animals;

import abstracts.Animal;
import data.LocalPosition;
import enums.AnimalType;
import interfaces.Huntable;

public class Goat extends Animal implements Huntable {
    private static int meatNutrition = 50;

    public Goat(String name, LocalPosition localPosition) {
        super(name, localPosition, AnimalType.GOAT);
    }

    @Override
    public String getMeatName() {
        return "Козлятина";
    }

    @Override
    public int getMeatNutrition() {
        return meatNutrition;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
