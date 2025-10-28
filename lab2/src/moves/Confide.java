package moves;

import ru.ifmo.se.pokemon.*;

public final class Confide extends StatusMove {
    public Confide() {
        super(Type.NORMAL, 0, 100);
    }

    @Override
    protected void applyOppEffects(Pokemon pok) {
        Effect eff = new Effect().stat(Stat.SPECIAL_ATTACK, -1);
        pok.addEffect(eff);
    }

    @Override
    protected String describe() {
        return "подрывает уверенность соперника";
    }
}
