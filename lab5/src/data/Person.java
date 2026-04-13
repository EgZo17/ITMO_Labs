package com.labwork.data;

import java.util.Comparator;
import java.util.Objects;
import com.labwork.enums.Color;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Person implements Comparable<Person> {

    @XmlElement(required = true)
    private String name; //Поле не может быть null, Строка не может быть пустой

    @XmlElement(required = true)
    private Double height; //Поле не может быть null, Значение поля должно быть больше 0

    @XmlElement(required = true)
    private Color eyeColor; //Поле не может быть null

    @XmlElement(required = true)
    private Location location; //Поле не может быть null

    public Person(String name, Double height, Color eyeColor, Location location) {
        setName(name);
        setHeight(height);
        setEyeColor(eyeColor);
        setLocation(location);
    }

    // Конструктор без параметров для JAXB
    public Person() {}

    public String getName() {
        return name;
    }

    public Double getHeight() {
        return height;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public Location getLocation() {
        return location;
    }

    public void setName(String name) {
        if (!(name == null) && !(name.isEmpty())) {
            this.name = name;
            return;
        }
        throw new IllegalArgumentException("Illegal Person: name can not be null or empty");
    }

    public void setHeight(Double height) {
        if (!(height == null) && !(height <= 0)) {
            this.height = height;
            return;
        }
        throw new IllegalArgumentException("Illegal Person: height can not be null or non-positive");
    }

    public void setEyeColor(Color eyeColor) {
        if (!(eyeColor == null)) {
            this.eyeColor = eyeColor;
            return;
        }
        throw new IllegalArgumentException("Illegal Person: eyeColor can not be null");
    }

    public void setLocation(Location location) {
        if (!(location == null)) {
            this.location = location;
            return;
        }
        throw new IllegalArgumentException("Illegal Person: location can not be null");
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, height, eyeColor, location);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj.getClass() != getClass()) {return false;}
        Person that = (Person) obj;
        return Objects.equals(that.name, name) && Objects.equals(that.height, height)
            && Objects.equals(that.eyeColor, eyeColor) && Objects.equals(that.location, location);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nPerson:\n");
        sb.append(String.format("\tName: %s\n", name));
        sb.append(String.format("\tHeight: %s\n", height));
        sb.append(String.format("\tEye Color: %s\n", eyeColor));
        sb.append(String.format("\tLocation: %s\n", location));
        return sb.toString();
    }

    @Override
    public int compareTo(Person other) {
        return Comparator.comparing(Person::getName).thenComparing(Person::getHeight).compare(this, other);
    }
}
