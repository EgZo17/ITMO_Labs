package abstracts;

import java.util.Objects;
import data.LocalPosition;
import enums.AnimalType;

public abstract class Animal extends IslandEntity{
    protected AnimalType type;

    public Animal(String name, LocalPosition localPosition, AnimalType type) {
        super(name, localPosition);
        this.type = type;
    }

    public AnimalType getType() {
        return type;
    }

    @Override
    public String getDescription() {
        return type.getRussianName() + " по имени " + name;
    }

    @Override
    public String toString() {
        return getDescription() + " в позиции " + localPosition + " (" + (alive ? "жив" : "мертв") + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Animal that = (Animal) o;
        return this.type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type);
    }
}
