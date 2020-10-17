package ch.voegele.Renderer;

import ch.voegele.Texture.ITextureMapper;
import ch.voegele.util.Vec3;
import ch.voegele.util.VectorHelpers;

/***
 * Sphere container class. Is used to create scenes for the renderer
 */
public class Sphere implements ISceneElement {

    /***
     * position of sphere in world coordinates
     */
    private final Vec3 position;

    /***
     * radius of sphere
     */
    private final float radius;

    /***
     * diffuse color of sphere
     */
    private final Vec3 diffuse;

    /***
     * emissive color of sphere
     */
    private final Vec3 Emission;

    /***
     * specular color of sphere
     */
    private final Vec3 specularColor;

    /**
     * TextureMapper containing the texture and mapping strategy of any texture
     */
    private ITextureMapper textureMapper;

    private VectorHelpers helper = new VectorHelpers(2.2);

    public Sphere(Vec3 position, float radius, Vec3 diffuse, Vec3 Emission, Vec3 specularColor) {
        this.position = position;
        this.radius = radius;
        this.diffuse = diffuse;
        this.Emission = Emission;
        this.specularColor = specularColor;
    }


    /***
     * returns the diffuse color or the color on the bitmap texture.
     * Uses a bitmap texture only if there has been a ITextureMapper set with setTextureMapper().
     *
     * @param normal normal at hitpoint
     * @return diffuse color
     */
    @Override
    public Vec3 getColor(Vec3 normal) {
        if (textureMapper == null) return diffuse;

        int value = textureMapper.getColorByNormal(normal);

        int red = (value & 0xFF0000) >> 16;
        int green = (value & 0xFF00) >> 8;
        int blue = (value & 0xFF);

        var sRGB = new Vec3(red, green, blue);

        //TODO: Pre do gamma correction and conversion to Vec3 on all pixels to reduce runtime
        //gamma correct texture value and clamp
        return helper.sRGBtoRGB(sRGB);
    }

    @Override
    public void setTextureMapper(ITextureMapper mapper) {
        textureMapper = mapper;
    }

    @Override
    public Vec3 getPosition() {
        return position;
    }

    public float getRadius() {
        return radius;
    }

    public Vec3 getEmission() {
        return Emission;
    }

    public Vec3 getSpecularColor() {
        return specularColor;
    }
}
