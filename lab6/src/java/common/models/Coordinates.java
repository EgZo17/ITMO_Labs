package com.labwork.common.models;

import java.io.Serializable;
import java.util.Objects;
import javax.xml.bind.annotation.*;

/**
 * Класс, представляющий координаты (X, Y) для лабораторной работы.
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class Coordinates implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    private double x;

    @XmlElement(required = true)
    private int y;

    public Coordinates(double x, int y) {
        this.x = x;
        this.y = y;
    }

    // Конструктор без параметров для JAXB
    public Coordinates() {}

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
