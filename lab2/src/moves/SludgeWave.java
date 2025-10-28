package moves;

import ru.ifmo.se.pokemon.*;

public final class SludgeWave extends SpecialMove {
    public SludgeWave() {
        super(Type.POISON, 95, 100);
    }

    @Override
    protected void applyOppEffects(Pokemon pok) {
        double poisonChance = 0.1;
        if (pok.hasType(Type.POISON) | pok.hasType(Type.STEEL)) {
            poisonChance = 0;
        }
        Effect eff = new Effect().chance(poisonChance).condition(Status.POISON);
        pok.addEffect(eff);
    }

    @Override
    protected String describe() {
        return "пытается отравить соперника ударной волной";
    }
}
