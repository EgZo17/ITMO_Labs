package moves;

import ru.ifmo.se.pokemon.*;

public final class PlayNice extends StatusMove {
    public PlayNice() {
        super(Type.NORMAL, 0, 100);
    }

    @Override
    protected void applyOppEffects(Pokemon pok) {
        Effect eff = new Effect().stat(Stat.ATTACK, -1);
        pok.addEffect(eff);
    }

    @Override
    protected String describe() {
        return "ослабляет соперника";
    }
}
