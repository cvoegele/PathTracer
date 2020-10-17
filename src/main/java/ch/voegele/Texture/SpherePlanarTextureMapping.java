package ch.voegele.Texture;

import ch.voegele.Renderer.Point;
import ch.voegele.util.MathUtilities;
import ch.voegele.util.Vec3;

import java.io.IOException;

/***
 * Planar mapping on sphere by normal vector
 */
public class SpherePlanarTextureMapping extends AbstractBitmapTexture implements ITextureMapper {

    public SpherePlanarTextureMapping(String path) throws IOException {
        setTexture(path);
    }

    @Override
    public int getColorByNormal(Vec3 normal) {
        normal = normal.normalize();

        double s = normal.x / 2d + 0.5d;
        double t = normal.y / 2d + 0.5d;

        s = MathUtilities.clamp(s, 0, 1);
        t = MathUtilities.clamp(t, 0, 1);

        int w = texture.getWidth();
        int h = texture.getHeight();

        int v = (int) (h * t);
        int u = (int) (w * s);
        v = MathUtilities.clamp(v, 0, h-1);
        u = MathUtilities.clamp(u, 0, w-1);

        var point = new Point(u, v);

        return texture.getRGB(point.i1, point.i2);
    }


}
