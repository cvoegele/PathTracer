package ch.voegele;

import ch.voegele.Renderer.Scene;
import ch.voegele.Renderer.SceneRenderer;
import ch.voegele.Texture.SphereSphericalTextureMapping;
import ch.voegele.UI.ObservableImage;
import ch.voegele.UI.PixelChangeListener;
import ch.voegele.util.Vec3;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.io.IOException;

/***
 * RenderView that creates a SceneRenderer, that then renders the Image with a given RenderEngine
 *
 */
public class RenderView implements PixelChangeListener {

    /***
     * Pixel writer to save write pixels to ImageView
     */
    private final PixelWriter writer;
    /***
     * javafx.ImageView to show image in gui
     */
    private final ImageView view;

    /***
     * Instantiate RenderView with parameters and start render process
     *
     * @param width of rendered image
     * @param height of rendered image
     * @param numberOfThreads number of threads to calculate the image
     * @param sampleRate number of rays to shoot at every pixel
     * @param gaussianAA whether gaussian Anti-Aliasing is turned on or off
     */
    public RenderView(int width, int height, int numberOfThreads, int sampleRate, boolean gaussianAA) {

        var observableImage = new ObservableImage(height, width);
        observableImage.addListener(this);
        SceneRenderer toRender = new SceneRenderer(width, height, numberOfThreads, sampleRate, gaussianAA, observableImage);

        //set scene to render
        toRender.setScene(setupSkyBoxScene());
        //toRender.setScene(setupCornellBox());
        //toRender.setScene(setupGaussScene());
        //toRender.setScene(setupUniverse());

        WritableImage writableImage = new WritableImage(width, height);
        view = new ImageView(writableImage);
        writer = writableImage.getPixelWriter();
        toRender.startRender();
    }

    /***
     * get view to present as ImageView in any javafx application
     * @return ImageView with rendered/currently rendering image
     */
    public ImageView getView() {
        return view;
    }

    /*
    the coming methods all just setup different scenes, with the Scene Builder class. On could put this somewhere else, but for now its here.
     */

    private static Scene setupGaussScene() {
        var scene = new Scene(new Vec3(0, -3, -3), new Vec3(0, 0, 0), 36);
        scene.addSphereDiffuse(new Vec3(0, 1001, 0), 1000, new Vec3(0.1, 0.1, 0.1)); //ground
        scene.addSphereEmmissive(new Vec3(0, 0, 0), 1000, Vec3.ONE, new Vec3(0.01, 0.01, 0.01));
        scene.addSphereDiffuse(new Vec3(0, 0, 0), 1, new Vec3(0, 0, 0.3));
        return scene;
    }

    private static Scene setupCornellBox() {
        //create a scene with an eye and lookAt Vector and FOV in degrees
        var scene = new Scene(new Vec3(0, 0, -4), new Vec3(0, 0, 6), 36);
        //add shiny spheres
        scene.addSphereShiny(new Vec3(-1001, 0, 0), 1000, new Vec3(0.3, 0, 0), Vec3.ONE);
        scene.addSphereShiny(new Vec3(1001, 0, 0), 1000, new Vec3(0, 0, 0.3), Vec3.ONE);
        scene.addSphereShiny(new Vec3(0, 0, 1001), 1000, new Vec3(0.1, 0.1, 0.1), Vec3.ONE);
        scene.addSphereShiny(new Vec3(0, 1001, 0), 1000, new Vec3(0.1, 0.1, 0.1), Vec3.ONE);
        //add emissive spheres
        scene.addSphereEmmissive(new Vec3(0, -1001, 0), 1000, new Vec3(0.8, 0.8, 0.8), Vec3.ONE.scale(4f));

        scene.addSphereShiny(new Vec3(-0.6, 0.7, -0.6), 0.3f, new Vec3(0.42, 0.42, 0), Vec3.ONE);
        try {
            //create a sphere with a texture
            var fireTexure = new SphereSphericalTextureMapping("earth.tif");
            scene.addSphereTextureShiny(new Vec3(0.3, 0.3, -0.3), 0.6f, fireTexure, Vec3.ONE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scene;
    }

    private static Scene setupSkyBoxScene() {
        var scene = new Scene(new Vec3(0, -3, -3), new Vec3(0, 0, 0), 36);
        //scene.addSphereShiny(new Vec3(0, 1001, 0), 1000, Vec3.ZERO, Vec3.ONE); //ground

        //add skybox
        try {
            var skyTexture = new SphereSphericalTextureMapping("small_cathedral_02.jpg");
            scene.addSphereTextureEmissive(new Vec3(0, 0, 0), 1000, skyTexture, new Vec3(0.01, 0.01, 0.01));
        } catch (IOException e) {
            e.printStackTrace();
        }

        scene.addSphereShiny(new Vec3(0, 0, 0), 1, Vec3.ZERO, Vec3.ONE);
        return scene;
    }

    private static Scene setupUniverse() {
        var scene = new Scene(new Vec3(0, -3, -3), new Vec3(0, 0, 0), 36);

        try {

            var nightSkyTexture = new SphereSphericalTextureMapping("nightsky.jpg");
            scene.addSphereTextureDiffuse(new Vec3(0, 0, 0), 10, nightSkyTexture);

            scene.addSphereEmmissive(new Vec3(0.4, -2, 10), 0.7f, Vec3.ONE, Vec3.ONE.scale(50));

            var earthTexture = new SphereSphericalTextureMapping("earth.tif");
            scene.addSphereTextureDiffuse(new Vec3(0.3, 0.3, -0.3), 0.6f, earthTexture);

            var moonTexture = new SphereSphericalTextureMapping("moon.tif");
            scene.addSphereTextureShiny(new Vec3(0.855, -0.5, -0.855), 0.1f, moonTexture, Vec3.ONE);

            var jupiterTexture = new SphereSphericalTextureMapping("jupiter.tif");
            scene.addSphereTextureDiffuse(new Vec3(-1, -1, -0.5), 1f, jupiterTexture);


            scene.addSphereEmmissive(new Vec3(3, 0, -3), 0.7f, Vec3.ONE, Vec3.ONE.scale(50));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return scene;
    }

    /***
     * Implementation of listener
     * @param u horizontal coordinate on image
     * @param v vertical coordinate on image
     * @param color color to set in image (gamma corrected sRGB)
     */
    @Override
    public void pixelChanged(int u, int v, Vec3 color) {
        Platform.runLater(() -> writer.setColor(u, v, color.toColor()));
    }
}
