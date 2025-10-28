import myPokemons.*;
import ru.ifmo.se.pokemon.*;
import java.util.List;

public class PokemonFight {
    public static void main(String[] args) {
        Battle b = new Battle();
        Bounsweet p1 = new Bounsweet("Berrie", 14);
        Dragalge p2 = new Dragalge("Gloomie", 52);
        Tsareena p3 = new Tsareena("Blossom", 43);
        Skrelp p4 = new Skrelp("Seahorse", 29);
        Steenee p5 = new Steenee("Stemmie", 27);
        Carnivine p6 = new Carnivine("Grassie", 50);
        for (Pokemon pok : List.of(p1, p2, p3)) {
            b.addAlly(pok);
        }
        for (Pokemon pok : List.of(p4, p5, p6)) {
            b.addFoe(pok);
        }
        b.go();
    }
}
