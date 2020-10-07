package ch.voegele;

import ch.voegele.Texture.ITextureMapper;
import ch.voegele.util.Vec3;
import ch.voegele.util.VectorHelpers;

public class Sphere implements ISceneElement {

    private final Vec3 position;
    private final float radius;
    private final Vec3 color;
    private final Vec3 Emission;
    private final Vec3 specularColor;
    private ITextureMapper textureMapper;
    private VectorHelpers helper = new VectorHelpers(2.2);

    public Sphere(Vec3 position, float radius, Vec3 color, Vec3 Emission, Vec3 specularColor) {
        this.position = position;
        this.radius = radius;
        this.color = color;
        this.Emission = Emission;
        this.specularColor = specularColor;
    }

    @Override
    public Vec3 getPosition() {
        return position;
    }

    public float getRadius() {
        return radius;
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
        if (textureMapper == null) return color;

        int value = textureMapper.getColorByNormal(normal);

        int red = (value & 0xFF0000) >> 16;
        int green = (value & 0xFF00) >> 8;
        int blue = (value & 0xFF);

        var sRGB = new Vec3(red, green, blue);
        var RGB = helper.sRGBtoRGB(sRGB);

        return RGB;
    }

    public Vec3 getEmission() {
        return Emission;
    }

    public Vec3 getSpecularColor() {
        return specularColor;
    }

    @Override
    public void setTextureMapper(ITextureMapper mapper) {
        textureMapper = mapper;
    }

}
