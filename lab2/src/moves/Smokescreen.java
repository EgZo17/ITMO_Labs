package moves;

import ru.ifmo.se.pokemon.*;

public final class Smokescreen extends StatusMove {
    public Smokescreen() {
        super(Type.NORMAL, 0, 100);
    }

    @Override
    protected void applyOppEffects(Pokemon pok) {
        Effect eff = new Effect().stat(Stat.ACCURACY, -1);
        pok.addEffect(eff);
    }

    @Override
    protected String describe() {
        return "устраивает дымовую завесу";
    }
}
