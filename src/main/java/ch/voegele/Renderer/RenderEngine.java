package ch.voegele.Renderer;

import ch.voegele.util.Vec2;
import ch.voegele.util.Vec3;

import java.util.Random;


public class RenderEngine {

    private Random random;

    public RenderEngine() {
        random = new Random();
    }

    /**
     * create ray that goes through the given pixel on the screen
     *
     * @param eye    Eye-Position in the World
     * @param lookAt Position the eye looks at in the World
     * @param FOV    field of View in Degrees
     * @param Pixel  on the Screen in -1 ... 1 for both x and y. (0,0) is the middle of the screen
     */
    Ray CreateEyeRay(Vec3 eye, Vec3 lookAt, double FOV, Vec2 Pixel) {
        Vec3 f = lookAt.subtract(eye);
        Vec3 r = (new Vec3(0, 1, 0)).cross(f);
        Vec3 u = f.cross(r);
        r = r.normalize();
        u = u.normalize();
        f = f.normalize();

        double FOVrad = FOV * (Math.PI / 180);
        double x = Pixel.x;
        double y = Pixel.y;
        Vec3 d = f.add(r.scale((float) (x * Math.tan(FOVrad / 2)))).add(u.scale((float) (y * Math.tan(FOVrad / 2))));
        return new Ray(eye, d.normalize());
    }

    /***
     *  find closest Hitpoint in a CornellBox.Scene for a ray
     *  SceneElements are currently only supported if they are Spheres
     *
     *  Primarily used for rays cast from the eye, but can also be used for other rays
     *
     * @param s
     * @param r
     * @return
     */
    HitPoint FindClosestHitPoint(Scene s, Ray r) {
        //standard values meaning no hitpoint, will only be overwritten when there is a hitpoint
        Vec3 closestPoint = Vec3.ZERO;
        ISceneElement hitObject = null;
        double lambda = 0d;
        Vec3 emission = Vec3.ZERO;

        for (ISceneElement element : s.get()) {
            if (element instanceof Sphere) {
                Sphere sphere = (Sphere) element;

                //detect Hit-Points between this sphere and the ray
                Vec3 CE = r.getPosition().subtract(sphere.getPosition());
                double a = r.getDirection().dot(r.getDirection());
                double b = CE.scale(2).dot(r.getDirection());
                double c = CE.length() * CE.length() - sphere.getRadius() * sphere.getRadius();

                double ac4 = 4 * a * c;
                double bb = b * b;

                //there are two hitpoints
                if (ac4 < bb) {

                    //solve with quadratic formula
                    double lambda1 = ((-b + Math.sqrt(bb - ac4)) / 2);
                    double lambda2 = ((-b - Math.sqrt(bb - ac4)) / 2);

                    double chosenLambda = SmallestPositiveValue(lambda1, lambda2);

                    /*
                    if the new lambda is not negative and it is smaller than one that might have been found before,
                    we have detected an intersection that is in front of the previous one.
                    */
                    if (chosenLambda != 0 && SmallestPositiveValue(chosenLambda, lambda) == chosenLambda) {
                        closestPoint = r.getPosition().add(r.getDirection().scale((float) chosenLambda));
                        hitObject = sphere;
                        lambda = chosenLambda;
                        emission = sphere.getEmission();
                    }
                    //there is only one hitpoint
                } else if (ac4 == bb) {
                    lambda = -b / 2;
                    hitObject = sphere;
                    closestPoint = r.getPosition().add(r.getDirection().scale((float) lambda));
                    emission = sphere.getEmission();
                }
                //there are no hitpoints e.g do nothing
            }
        }
        //nudge into opposite direction of ray a bit
        var nudge = r.getDirection().scale(-1f * 1f / 1000f);
        closestPoint = closestPoint.add(nudge);

        return new HitPoint(closestPoint, hitObject, emission, r);
    }

    /***
     * Computes the Color of a Pixel inside of the CornellBox.Scene.
     * This method finds the closest hit point by calling FindClosestHitPoint
     * Then bouncing in the screen until it terminates with a probability.
     * The number of bounces is not deterministic.
     *
     * @param s CornellBox.Scene with the SceneElments inside
     * @param r CornellBox.Ray that is cast from the Eye
     * @return Vec3 representing Color in LinearRGB (0 ... 1)
     */
    Vec3 ComputeColor(Scene s, Ray r) {
        HitPoint point = FindClosestHitPoint(s, r);


        if (point.getHitObject() == null) {
            return new Vec3(0, 0, 0);
        }

        double p = 0.1;
        if (random.nextDouble() < p) {
            return point.getEmission();
        } else {
            var w = Vec3.ONE.scale(2);
            while (w.length() > 1) {
                var x = (random.nextDouble() * 2) - 1;
                var y = (random.nextDouble() * 2) - 1;
                var z = (random.nextDouble() * 2) - 1;
                w = new Vec3(x, y, z);
            }
            w = w.normalize();

            var normal = point.getNormal().normalize();
            var dotWnormal = w.dot(normal);
            //return normal;
            if (dotWnormal < 0) w = w.scale(-1);

            var middle = BRDF(point, r, w).scale((float) ((w.dot(normal) * (Math.PI / (1f - p)))));
            var recursion = ComputeColor(s, new Ray(point.getPosition(), w));
            var x = point.getEmission().x + middle.x * recursion.x;
            var y = point.getEmission().y + middle.y * recursion.y;
            var z = point.getEmission().z + middle.z * recursion.z;
            return new Vec3(x, y, z);

        }
    }

    /***
     * Computes the Color of a Pixel inside of the CornellBox.Scene.
     * This method finds the closest hit point by calling FindClosestHitPoint
     * Then bouncing in the scene in a random direction @bounces times
     * The number of bounces is deterministic.
     *
     * @param s CornellBox.Scene with the SceneElments inside
     * @param r CornellBox.Ray that is cast from the Eye
     * @param bounces the maximum bounces the render does before it returns the color
     * @param bounce the number of bounce it is on right now. To start render pass 0.
     * @return Vec3 representing Color in LinearRGB (0 ... 1)
     */
    @Deprecated
    Vec3 ComputeColor(Scene s, Ray r, int bounces, int bounce) {
        HitPoint point = FindClosestHitPoint(s, r);


        if (point.getHitObject() == null) {
            return new Vec3(0, 0, 0);
        }

        double p = 0.1;
        if (bounces == bounce) {
            return point.getEmission();
        } else {
            var w = Vec3.ONE.scale(2);
            while (w.length() > 1) {
                var x = (random.nextDouble() * 2) - 1;
                var y = (random.nextDouble() * 2) - 1;
                var z = (random.nextDouble() * 2) - 1;
                w = new Vec3(x, y, z);
            }
            w = w.normalize();

            var normal = point.getNormal().normalize();
            var dotWnormal = w.dot(normal);
            //return normal;
            if (dotWnormal < 0) w = w.scale(-1);

            var middle = BRDF(point, r, w).scale((float) ((w.dot(normal) * (Math.PI / (1f - p)))));
            var recursion = ComputeColor(s, new Ray(point.getPosition(), w), bounces, bounce + 1);
            var x = point.getEmission().x + middle.x * recursion.x;
            var y = point.getEmission().y + middle.y * recursion.y;
            var z = point.getEmission().z + middle.z * recursion.z;
            return new Vec3(x, y, z);
        }
    }


    Vec3 BRDF(HitPoint point, Ray r, Vec3 w) {
        var epsilon = 0.01f;
        var mu = 2f;

        var normal = point.getNormal().normalize();
        var d = r.getDirection();
        var reflection = d.subtract(normal.scale(normal.dot(d)).scale(2));
        var wDotReflection = w.dot(reflection);
        if (wDotReflection > 1 - epsilon) {
            return point.getHitObject().getColor(point.getNormal()).add(point.getHitObject().getSpecularColor().scale(mu));
        } else {
            return point.getHitObject().getColor(point.getNormal());
        }
    }

    private double SmallestPositiveValue(double val1, double val2) {
        if (val1 <= 0 && val2 <= 0) return 0;
        if (val1 <= 0) return val2;
        if (val2 <= 0) return val1;
        return Math.min(val1, val2);
    }
}
