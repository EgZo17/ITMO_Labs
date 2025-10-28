package myPokemons;

import ru.ifmo.se.pokemon.*;
import moves.*;

public class Bounsweet extends Pokemon {
    public Bounsweet(String name, int level) {
        super(name, level);
        super.setType(Type.GRASS);
        super.setStats(42, 30, 38, 30, 38, 32);

        Confide confide = new Confide();
        Facade facade = new Facade();

        super.addMove(confide);
        super.addMove(facade);
    }
}
