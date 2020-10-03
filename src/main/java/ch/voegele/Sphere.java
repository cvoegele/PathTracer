package ch.voegele;

import ch.voegele.util.Vec3;

public class Sphere implements SceneElement {

    private final Vec3 position;
    private final float radius;
    private final Vec3 color;
    private final Vec3 Emission;

    public Sphere(Vec3 position, float radius, Vec3 color, Vec3 Emission) {
        this.position = position;
        this.radius = radius;
        this.color = color;
        this.Emission = Emission;
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

    public Vec3 getEmission() {
        return Emission;
    }
}