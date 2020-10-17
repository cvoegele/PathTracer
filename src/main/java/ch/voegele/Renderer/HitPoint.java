package ch.voegele.Renderer;

import ch.voegele.util.Vec3;

/***
 * hit point class
 */
public class HitPoint {

    /***
     * position of the hit point in world coordinates
     */
    private final Vec3 position;

    /***
     * reference to the element hit in the scene
     */
    private final ISceneElement hitObject;


    /***
     * emission of hit object
     */
    private final Vec3 emission;

    /***
     * Ray with which the intersection was calculated
     */
    private final Ray ray;


    public HitPoint(Vec3 position, ISceneElement hitObject, Vec3 emission, Ray ray) {
        this.position = position;
        this.hitObject = hitObject;
        this.emission = emission;
        this.ray = ray;
    }

    public Vec3 getPosition() {
        return position;
    }

    public ISceneElement getHitObject() {
        return hitObject;
    }


    public Vec3 getEmission() {
        return emission;
    }

    public Vec3 getNormal() {
        return getPosition().subtract(getHitObject().getPosition());
    }

    public Ray getRay() {
        return ray;
    }
}
