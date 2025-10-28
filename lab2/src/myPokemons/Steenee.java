package myPokemons;

import moves.*;

public class Steenee extends Bounsweet {
    public Steenee(String name, int level) {
        super(name, level);
        super.setStats(52, 40, 48, 40, 48, 62);

        PlayNice playNice = new PlayNice();

        super.addMove(playNice);
    }
}
