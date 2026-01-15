package animals;

import java.util.Objects;
import abstracts.Animal;
import data.LocalPosition;
import enums.AnimalType;
import interfaces.Speakable;
import interfaces.Tameable;

public class Parrot extends Animal implements Speakable, Tameable {
    private boolean tamed;
    private String learnedWord;

    public Parrot(String name, LocalPosition localPosition) {
        super(name, localPosition, AnimalType.PARROT);
        this.tamed = false;
        this.learnedWord = "";
    }

    public boolean learnWord(String word) {
        if (tamed && learnedWord.isEmpty()) {
            learnedWord = word;
            System.out.println("Попугай " + name + " изучил новое слово: " + word);
            return true;
        }
        return false;
    }

    @Override
    public void speak() {
        if (!learnedWord.isEmpty()) {
            System.out.println(name + " говорит: \"" + learnedWord + "\"");
        } else {
            System.out.println(name + " чирикает");
        }
    }

    @Override
    public void tame() {
        tamed = true;
    }

    @Override
    public String toString() {
        return super.toString() + " (" + (tamed ? "" : "не ") + "приручен, выученное слово: " +
                (learnedWord.isEmpty() ? "нет" : "\"" + learnedWord + "\"") + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Parrot that = (Parrot) o;
        return this.tamed == that.tamed && Objects.equals(this.learnedWord, that.learnedWord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tamed, learnedWord);
    }
}
