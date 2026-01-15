package generators;

import data.LocalPosition;
import resources.Location;
import java.util.Random;

public class LocalPositionGenerator {
    private static final Random random = new Random();

    public static LocalPosition getRandomPosition(Location location) {
        int locationWidth = location.getInfo().width();
        int locationHeight = location.getInfo().height();
        LocalPosition randomPosition = new LocalPosition(random.nextInt(locationWidth), random.nextInt(locationHeight));
        return randomPosition;
    }
}
