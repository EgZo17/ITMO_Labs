package animals;

import java.util.Objects;
import abstracts.Animal;
import data.LocalPosition;
import enums.AnimalType;
import interfaces.Huntable;

public class Fox extends Animal implements Huntable {
    private boolean specialBreed;
    private int meatNutrition = (specialBreed ? 15 : 20);

    public Fox(String name, LocalPosition localPosition, boolean specialBreed) {
        super(name, localPosition, AnimalType.FOX);
        this.specialBreed = specialBreed;
    }

    @Override
    public String getMeatName() {
        return "Лисье мясо" + (specialBreed ? " (особая порода)" : "");
    }

    @Override
    public int getMeatNutrition() {
        return meatNutrition;
    }

    public boolean isSpecialBreed() {
        return specialBreed;
    }

    @Override
    public String toString() {
        return super.toString() + (specialBreed ? " (особой породы)" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Fox that = (Fox) o;
        return this.specialBreed == that.specialBreed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), specialBreed);
    }
}
