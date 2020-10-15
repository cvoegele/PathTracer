package ch.voegele.Renderer;

import ch.voegele.Texture.ITextureMapper;
import ch.voegele.util.Vec3;

import java.io.IOException;

public interface ISceneElement {
    public Vec3 getColor(Vec3 normal);

    public Vec3 getPosition();

    public Vec3 getSpecularColor();

    public void setTextureMapper(ITextureMapper mapper);
}
