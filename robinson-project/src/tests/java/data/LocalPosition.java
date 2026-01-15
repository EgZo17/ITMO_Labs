package data;

import java.util.Objects;
import java.lang.Math;

public class LocalPosition {
    private int x;
    private int y;

    public LocalPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public double distanceTo(LocalPosition other) {
        int dx = this.x - other.x;
        int dy = this.y = other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public boolean isWithinBounds(int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean moveBounded(int dx, int dy, int width, int height) {
        int new_x = x + dx;
        int new_y = y + dy;
        return new_x >= 0 && new_x < width && new_y >= 0 && new_y < height;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalPosition that = (LocalPosition) o;
        return this.x == that.x && this.y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
