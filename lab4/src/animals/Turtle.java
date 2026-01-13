package animals;

import java.util.Objects;
import abstracts.Animal;
import data.LocalPosition;
import enums.AnimalType;
import interfaces.Huntable;

public class Turtle extends Animal implements Huntable {
    private int shellHardness;
    private static int meatNutrition = 30;

    public Turtle(String name, LocalPosition localPosition, int shellHardness) {
        super(name, localPosition, AnimalType.TURTLE);
        this.shellHardness = Math.max(1, Math.min(10, shellHardness));
    }

    @Override
    public String getMeatName() {
        return "Черепашье мясо";
    }

    @Override
    public int getMeatNutrition() {
        return meatNutrition;
    }

    public int getShellHardness() {
        return shellHardness;
    }

    @Override
    public String toString() {
        return super.toString() + " (твердость панциря: " + shellHardness + "/10)";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Turtle that = (Turtle) o;
        return this.shellHardness == that.shellHardness;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), shellHardness);
    }
}
