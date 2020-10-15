package ch.voegele.UI;

import ch.voegele.Renderer.Scene;
import ch.voegele.Renderer.SceneRenderer;
import ch.voegele.Texture.SphereSphericalTextureMapping;
import ch.voegele.util.Vec3;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import java.io.IOException;

public class RenderView implements PixelChangeListener {

    WritableImage writableImage;
    ImageView view;

    public RenderView(int width, int height, int numberOfThreads, int sampleRate, boolean gaussianAA) {

        var observableImage = new ObservableImage(height, width);
        observableImage.addListener(this);
        SceneRenderer toRender = new SceneRenderer(width, height, numberOfThreads, sampleRate, gaussianAA, observableImage);

        //set scene to render
        //toRender.setScene(setupSkyBoxScene());
        toRender.setScene(setupCornellBox());
        //toRender.setScene(setupGaussScene());

        writableImage = new WritableImage(width, height);
        view = new ImageView(writableImage);
        toRender.startRender();
    }

    public ImageView getView() {
        return view;
    }

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
        scene.addSphereShiny(new Vec3(0, 1001, 0), 1000, Vec3.ZERO, Vec3.ONE); //ground

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

    @Override
    public void pixelChanged(int u, int v, Vec3 color) {
        Platform.runLater(()-> {
            var writer = writableImage.getPixelWriter();
            writer.setColor(u, v, color.toColor());
        });
    }
}
