package data;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;
import enums.Difficulty;

public class LabWork implements Comparable<LabWork> {
    private final int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private final LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Float minimalPoint; //Поле не может быть null, Значение поля должно быть больше 0
    private Difficulty difficulty; //Поле может быть null
    private Person author; //Поле не может быть null

    public LabWork(int id, String name, Coordinates coordinates, Float minimalPoint, Difficulty difficulty, Person author) {
        this.id = id;
        setName(name);
        setCoordinates(coordinates);
        this.creationDate = LocalDate.now();
        setMinimalPoint(minimalPoint);
        setDifficulty(difficulty);
        setAuthor(author);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public Float getMinimalPoint() {
        return minimalPoint;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Person getAuthor() {
        return author;
    }

    public void setName(String name) {
        if (!(name == null) && !(name.isEmpty())) {
            this.name = name;
            return;
        }
        throw new IllegalArgumentException("Illegal LabWork: name can not be null or empty");
    }

    public void setCoordinates(Coordinates coordinates) {
        if (!(coordinates == null)) {
            this.coordinates = coordinates;
            return;
        }
        throw new IllegalArgumentException("Illegal LabWork: coordinates can not be null");
    }

    public void setMinimalPoint(Float minimalPoint) {
        if (!(minimalPoint == null) && !(minimalPoint <= 0)) {
            this.minimalPoint = minimalPoint;
            return;
        }
        throw new IllegalArgumentException("Illegal LabWork: minimalPoint can not be null or non-positive");
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setAuthor(Person author) {
        if (!(author == null)) {
            this.author = author;
            return;
        }
        throw new IllegalArgumentException("Illegal LabWork: author can not be null");
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, minimalPoint, difficulty, author);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj.getClass() != getClass()) {return false;}
        LabWork that = (LabWork) obj;
        return Objects.equals(that.id, id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nLabWork:\n");
        sb.append(String.format("\tID: %s\n", id));
        sb.append(String.format("\tName: %s\n", name));
        sb.append(String.format("\tCoordinates: %s\n", coordinates));
        sb.append(String.format("\tCreation Date: %s\n", creationDate));
        sb.append(String.format("\tMinimal Point: %s\n", minimalPoint));
        sb.append(String.format("\tDifficulty: %s\n", difficulty));
        sb.append(String.format("\tAuthor: %s\n", author.getName()));
        return sb.toString();
    }

    @Override
    public int compareTo(LabWork other) {
        return Comparator.comparing(LabWork::getName).thenComparing(LabWork::getAuthor).compare(this, other);
    }
}
