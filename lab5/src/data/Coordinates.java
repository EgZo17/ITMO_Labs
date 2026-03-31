package com.labwork.data;

import java.util.Objects;

public class Coordinates {
    private double x;
    private int y;

    public Coordinates(double x, int y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj.getClass() != getClass()) {return false;}
        Coordinates that = (Coordinates) obj;
        return Objects.equals(that.x, x) && Objects.equals(that.y, y);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", x, y);
    }
}
