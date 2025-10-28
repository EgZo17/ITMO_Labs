package moves;

import ru.ifmo.se.pokemon.*;

public final class Swagger extends StatusMove {
    public Swagger() {
        super(Type.NORMAL, 0, 85);
    }

    @Override
    protected void applyOppEffects(Pokemon pok) {
        Effect.confuse(pok);
        Effect eff = new Effect().stat(Stat.ATTACK, +2);
        pok.addEffect(eff);
    }

    @Override
    protected String describe() {
        return "вводит соперника в кураж";
    }
}
