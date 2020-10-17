package ch.voegele.Texture;

import ch.voegele.Renderer.Point;
import ch.voegele.util.MathUtilities;
import ch.voegele.util.Vec3;

import java.io.IOException;

/***
 * spherical mapping on sphere by normal vector
 */
public class SphereSphericalTextureMapping extends AbstractBitmapTexture implements ITextureMapper {

    public SphereSphericalTextureMapping(String path) throws IOException {
        setTexture(path);
    }

    @Override
    public int getColorByNormal(Vec3 normal) {
        normal = normal.normalize();

        //map outputs of functions atan2 and acos to 0 to 1
        double s = (Math.atan2(normal.x, -normal.z) + Math.PI) / (2*Math.PI);
        double t = (Math.acos(-normal.y)) / Math.PI; //account for upside down coordinate system on y-axis

        s = MathUtilities.clamp(s, 0, 1);
        t = MathUtilities.clamp(t, 0, 1);

        int w = texture.getWidth();
        int h = texture.getHeight();

        int v = (int) (h * t);
        int u = (int) (w * s);
        v = MathUtilities.clamp(v, 0, h - 1);
        u = MathUtilities.clamp(u, 0, w - 1);

        var point = new Point(u, v);

        return texture.getRGB(point.i1, point.i2);
    }
}
