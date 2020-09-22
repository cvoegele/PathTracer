import javafx.scene.paint.Color;
import util.Vec3;

public class Sphere implements SceneElement {

    private final Vec3 position;
    private final float radius;
    private final Vec3 color;

    public Sphere(Vec3 position, float radius, Vec3 color) {
        this.position = position;
        this.radius = radius;
        this.color = color;
    }

    public Vec3 getPosition() {
        return position;
    }

    public float getRadius() {
        return radius;
    }

    public Vec3 getColor() {
        return color;
    }
}
