import util.Vec3;

public class HitPoint {

    private final Vec3 position;
    private final SceneElement hitObject;
    private final double lambda;
    private final Vec3 emission;


    public HitPoint(Vec3 position, SceneElement hitObject, double lambda, Vec3 emission) {
        this.position = position;
        this.hitObject = hitObject;
        this.lambda = lambda;
        this.emission = emission;
    }

    public Vec3 getPosition() {
        return position;
    }

    public SceneElement getHitObject() {
        return hitObject;
    }

    public double getLambda() {
        return lambda;
    }

    public Vec3 getEmission() {
        return emission;
    }

    public Vec3 getNormal() {
        return getPosition().subtract(getHitObject().getPosition());
    }
}
