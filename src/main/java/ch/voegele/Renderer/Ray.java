package ch.voegele.Renderer;

import ch.voegele.util.Vec3;

public class Ray {

    private final Vec3 position;
    private final Vec3 direction;

    public Ray(Vec3 position, Vec3 direction) {
        this.position = position;
        this.direction = direction;
    }

    public Vec3 getPosition() {
        return position;
    }

    public Vec3 getDirection() {
        return direction;
    }
}
