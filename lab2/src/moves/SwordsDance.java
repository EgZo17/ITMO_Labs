package moves;

import ru.ifmo.se.pokemon.*;

public final class SwordsDance extends StatusMove {
    public SwordsDance() {
        super(Type.NORMAL, 0, 100);
    }

    @Override
    protected void applySelfEffects(Pokemon pok) {
        Effect eff = new Effect().stat(Stat.ATTACK, +2);
        pok.addEffect(eff);
    }

    @Override
    protected String describe() {
        return "вызывает танец клинков";
    }
}
