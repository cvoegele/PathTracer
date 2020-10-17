package ch.voegele.Renderer;

import ch.voegele.util.Vec3;

/***
 * Ray class that represents any ray inside of the world
 */
public class Ray {

    /***
     * where the ray starts
     */
    private final Vec3 position;

    /***
     * in what direction the ray goes
     */
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
