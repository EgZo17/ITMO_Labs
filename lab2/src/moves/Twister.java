package moves;

import ru.ifmo.se.pokemon.*;
import java.lang.Math;

public final class Twister extends SpecialMove {
    public Twister() {
        super(Type.DRAGON, 40, 100);
    }

    @Override
    protected void applyOppEffects(Pokemon pok) {
        double reqEffChance = 0.3;
        double effChance = Math.random();
        if (effChance <= reqEffChance) {
            Effect.flinch(pok);
        }
    }

    @Override
    protected String describe() {
        return "закручивает соперника в вихре";
    }
}
