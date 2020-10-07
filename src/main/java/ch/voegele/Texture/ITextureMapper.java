package ch.voegele.Texture;

import ch.voegele.Point;
import ch.voegele.util.Vec3;

public interface ITextureMapper {
    public int getColorByNormal(Vec3 normal);
}
