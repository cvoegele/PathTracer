package ch.voegele.Texture;

import ch.voegele.util.Vec3;

/***
 * Interface defining texture mappers.
 * Any Texture mapper needs to implement this ITextureMapper interface
 * Any texture mapping calculates position on texture via normal vector
 */
public interface ITextureMapper {

    /***
     * calculate position on texture via normal
     * @param normal
     * @return color in sRGB as an integer with empty alpha channel and 8 bits per color channel
     */
    int getColorByNormal(Vec3 normal);
}
