package records;

import data.LocalPosition;

public record LocationInfo(String name, String description, int width, int height) {

    public boolean isPositionValid(LocalPosition position) {
        return position.getX() >= 0 && position.getX() < width &&
               position.getY() >= 0 && position.getY() < height;
    }

    @Override
    public String toString() {
        return name + ": " + description + " (" + width + "x" + height + ")";
    }
}
