package com.labwork.data;

import java.util.Objects;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Location {

    @XmlElement(required = true)
    private Integer x; //Поле не может быть null

    @XmlElement(required = true)
    private double y;

    @XmlElement(required = true)
    private Double z; //Поле не может быть null

    public Location(Integer x, double y, Double z) {
        setX(x);
        setY(y);
        setZ(z);
    }

    // Конструктор без параметров для JAXB
    public Location() {}

    public Integer getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Double getZ() {
        return z;
    }

    public void setX(Integer x) {
        if (!(x == null)) {
            this.x = x;
            return;
        }
        throw new IllegalArgumentException("Illegal Location: x can not be null");
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(Double z) {
        if (!(z == null)) {
            this.z = z;
            return;
        }
        throw new IllegalArgumentException("Illegal Location: z can not be null");
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj.getClass() != getClass()) {return false;}
        Location that = (Location) obj;
        return Objects.equals(that.x, x) && Objects.equals(that.y, y) && Objects.equals(that.z, z);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", x, y, z);
    }
}
