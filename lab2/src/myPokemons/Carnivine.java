package myPokemons;
import ru.ifmo.se.pokemon.*;

public class Carnivine extends Pokemon{
    public Carnivine (String name, int level) {
        super(name, level);
        super.setType(Type.GRASS);
    }
}