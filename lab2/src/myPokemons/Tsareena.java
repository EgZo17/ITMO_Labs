package myPokemons;

import moves.*;

public final class Tsareena extends Steenee {
    public Tsareena(String name, int level) {
        super(name, level);
        super.setStats(72, 120, 98, 50, 98, 72);

        Swagger swagger = new Swagger();

        super.addMove(swagger);
    }
}
