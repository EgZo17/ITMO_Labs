package myPokemons;

import ru.ifmo.se.pokemon.*;
import moves.*;

public final class Dragalge extends Skrelp {
    public Dragalge(String name, int level) {
        super(name, level);
        super.setType(Type.POISON, Type.DRAGON);
        super.setStats(65, 75, 90, 97, 123, 44);

        Twister twister = new Twister();

        super.addMove(twister);
    }
}
