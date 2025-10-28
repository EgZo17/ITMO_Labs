package myPokemons;

import ru.ifmo.se.pokemon.*;
import moves.*;

public final class Carnivine extends Pokemon {
    public Carnivine(String name, int level) {
        super(name, level);
        super.setType(Type.GRASS);
        super.setStats(74, 100, 72, 90, 72, 46);

        Crunch crunch = new Crunch();
        Swagger swagger = new Swagger();
        SwordsDance swordsDance = new SwordsDance();
        Facade facade = new Facade();

        super.addMove(crunch);
        super.addMove(swagger);
        super.addMove(swordsDance);
        super.addMove(facade);
    }
}
