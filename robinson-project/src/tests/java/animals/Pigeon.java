package animals;

import java.util.Objects;
import abstracts.Animal;
import data.LocalPosition;
import enums.AnimalType;
import interfaces.Huntable;

public class Pigeon extends Animal implements Huntable {
    private boolean canFlyLongDistance;
    private static int meatNutrition = 15;

    public Pigeon(String name, LocalPosition localPosition, boolean canFlyLongDistance) {
        super(name, localPosition, AnimalType.PIGEON);
        this.canFlyLongDistance = canFlyLongDistance;
    }

    @Override
    public String getMeatName() {
        return "Голубиное мясо";
    }

    @Override
    public int getMeatNutrition() {
        return meatNutrition;
    }

    public boolean canFlyLongDistance() {
        return canFlyLongDistance;
    }

    @Override
    public String toString() {
        return super.toString() + " (" + (canFlyLongDistance ? "" : "не ") + "может летать на дальние расстояния)";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Pigeon that = (Pigeon) o;
        return this.canFlyLongDistance == that.canFlyLongDistance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), canFlyLongDistance);
    }
}
