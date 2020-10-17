package ch.voegele.Renderer;

import ch.voegele.Texture.ITextureMapper;
import ch.voegele.util.Vec3;

import java.util.ArrayList;
import java.util.List;

/***
 * Builder class for every scene.
 * Create an empty instance with the eye, lookAt and fieldOfView (FOV)
 * Then add all elements with the built in methods
 */
public class Scene {

    private final List<ISceneElement> sceneElements = new ArrayList<>();

    private final Vec3 eye;
    private final Vec3 lookAt;
    private final float FOV;

    public Scene(Vec3 eye, Vec3 lookAt, float FOV) {
        this.eye = eye;
        this.lookAt = lookAt;
        this.FOV = FOV;
    }


    /**
     * add sphere with plain color diffuse
     *
     * @param position
     * @param radius
     * @param color
     */
    public void addSphereDiffuse(Vec3 position, float radius, Vec3 color) {
        var sphere = new Sphere(position, radius, color, Vec3.ZERO, Vec3.ZERO);
        addSceneElement(sphere);
    }

    /**
     * add sphere with plain color emissive
     *
     * @param position
     * @param radius
     * @param color
     * @param emission
     */
    public void addSphereEmmissive(Vec3 position, float radius, Vec3 color, Vec3 emission) {
        var sphere = new Sphere(position, radius, color, emission, Vec3.ZERO);
        addSceneElement(sphere);
    }

    /**
     * add sphere with plain color shiny
     *
     * @param position
     * @param radius
     * @param color
     * @param specularColor
     */
    public void addSphereShiny(Vec3 position, float radius, Vec3 color, Vec3 specularColor) {
        var sphere = new Sphere(position, radius, color, Vec3.ZERO, specularColor);
        addSceneElement(sphere);
    }

    /**
     * add sphere with texture diffuse
     * @param position
     * @param radius
     * @param textureMapper
     */
    public void addSphereTextureDiffuse(Vec3 position, float radius, ITextureMapper textureMapper) {
        var sphere = new Sphere(position, radius, Vec3.ZERO, Vec3.ZERO, Vec3.ZERO);
        sphere.setTextureMapper(textureMapper);
        addSceneElement(sphere);
    }

    /**
     * add sphere with texture and emission
     * @param position
     * @param radius
     * @param textureMapper
     * @param emission
     */
    public void addSphereTextureEmissive(Vec3 position, float radius, ITextureMapper textureMapper, Vec3 emission) {
        var sphere = new Sphere(position, radius, Vec3.ZERO, emission, Vec3.ZERO);
        sphere.setTextureMapper(textureMapper);
        addSceneElement(sphere);
    }

    /**
     * add sphere with texture and shiny
     * @param position
     * @param radius
     * @param textureMapper
     * @param specularColor
     */
    public void addSphereTextureShiny(Vec3 position, float radius, ITextureMapper textureMapper, Vec3 specularColor){
        var sphere = new Sphere(position, radius, Vec3.ZERO, Vec3.ZERO ,specularColor);
        sphere.setTextureMapper(textureMapper);
        addSceneElement(sphere);
    }

    /**
     * add any arbitrary SceneElement not supported by direct builder method
     *
     * @param element
     */
    public void addAnySceneElement(ISceneElement element) {
        addSceneElement(element);
    }

    private void addSceneElement(ISceneElement sceneElement) {
        sceneElements.add(sceneElement);
    }

    public List<ISceneElement> get(){
        return sceneElements;
    }

    public Vec3 getEye() {
        return eye;
    }

    public Vec3 getLookAt() {
        return lookAt;
    }

    public float getFOV() {
        return FOV;
    }
}
