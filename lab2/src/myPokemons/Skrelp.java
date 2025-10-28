package myPokemons;

import ru.ifmo.se.pokemon.*;
import moves.*;

public class Skrelp extends Pokemon {
    public Skrelp(String name, int level) {
        super(name, level);
        super.setType(Type.POISON, Type.WATER);
        super.setStats(50, 60, 60, 60, 60, 30);

        SludgeWave sludgeWave = new SludgeWave();
        Smokescreen smokescreen = new Smokescreen();
        Facade facade = new Facade();

        super.addMove(sludgeWave);
        super.addMove(smokescreen);
        super.addMove(facade);
    }
}
