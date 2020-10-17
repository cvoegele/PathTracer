package ch.voegele.Renderer;

import ch.voegele.Texture.ITextureMapper;
import ch.voegele.util.Vec3;

/***
 * Scene Element interface. All objects in a scene must implement ISceneElement.
 */
public interface ISceneElement {
    public Vec3 getColor(Vec3 normal);

    public Vec3 getPosition();

    public Vec3 getSpecularColor();

    public void setTextureMapper(ITextureMapper mapper);
}
