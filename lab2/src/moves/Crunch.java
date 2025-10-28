package moves;

import ru.ifmo.se.pokemon.*;

public final class Crunch extends PhysicalMove {
    public Crunch() {
        super(Type.DARK, 80, 100);
    }

    @Override
    protected void applyOppEffects(Pokemon pok) {
        Effect eff = new Effect().chance(0.2).stat(Stat.DEFENSE, -1);
        pok.addEffect(eff);
    }

    @Override
    protected String describe() {
        return "грызёт соперника";
    }
}
