package moves;

import java.util.List;
import ru.ifmo.se.pokemon.*;

public final class Facade extends PhysicalMove {
    public Facade() {
        super(Type.NORMAL, 70, 100);
    }

    @Override
    protected double calcBaseDamage(Pokemon att, Pokemon def) {
        if (List.of(Status.BURN, Status.PARALYZE, Status.POISON).contains(att.getCondition())) {
            return (0.4 * (double) att.getLevel() + 2.0) * this.power / 150.0 * 2;
        }
        return (0.4 * (double) att.getLevel() + 2.0) * this.power / 150.0;
    }

    @Override
    protected String describe() {
        return "буйствует";
    }
}
