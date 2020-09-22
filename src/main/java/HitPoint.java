import util.Vec3;

public class HitPoint {

    private final Vec3 position;
    private final SceneElement hitObject;
    private final double lambda;

    public HitPoint(Vec3 position, SceneElement hitObject, double lambda) {
        this.position = position;
        this.hitObject = hitObject;
        this.lambda = lambda;
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
}
