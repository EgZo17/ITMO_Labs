package abstracts;

import java.util.Objects;
import data.LocalPosition;

public abstract class IslandEntity {
    protected String name;
    protected LocalPosition localPosition;
    protected boolean alive;

    public IslandEntity(String name, LocalPosition localPosition) {
        this.name = name;
        this.localPosition = localPosition;
        this.alive = true;
    }

    public abstract String getDescription();

    public void moveTo(LocalPosition newPosition) {
        this.localPosition.setX(newPosition.getX());
        this.localPosition.setY(newPosition.getY());
    }

    public void moveBy(int dx, int dy) {
        this.localPosition.move(dx, dy);
    }

    public boolean isAlive() {
        return this.alive;
    }

    public void die() {
        if (this.alive) {this.alive = false;}
    }

    public String getName() {
        return this.name;
    }

    public LocalPosition getLocalPosition() {
        return this.localPosition;
    }

    public void setLocalPosition(LocalPosition position) {
        this.localPosition = position;
    }

    @Override
    public String toString() {
        return name + " в позиции " + localPosition + " (" + (alive ? "жив" : "мертв") + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        IslandEntity that = (IslandEntity) o;
        return alive == that.alive &&
               Objects.equals(name, that.name) &&
               Objects.equals(localPosition, that.localPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, localPosition, alive);
    }
}
