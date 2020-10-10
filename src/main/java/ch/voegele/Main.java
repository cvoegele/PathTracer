package ch.voegele;

import ch.voegele.Texture.SphereSphericalTextureMapping;
import ch.voegele.util.Vec3;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;

public class Main extends Application {

    private static SceneRenderer toRender;

    /***
     * Possible Commandline Arguments:
     * -threads     number of Threads the program should create to render the image
     * -sampleRate  the number of Rays that are being shot per pixel
     * -bounces     the number of bounces per single ray
     *
     * All Arguments are optional. If left blank these Standard Values are taken
     * threads = 1
     * sampleRate = 32
     * bounces = -1 means probability termination on bounces is used
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        var numberOfThreads = 0;
        var sampleRate = 0;
        var bounces = 0;

        //read arguments
        if (args.length > 0) {
            var splits = new String[args.length * 2];
            for (int i = 0; i < args.length; i++) {
                var argSplit = args[i].split("[=]");
                splits[i * 2] = argSplit[0];
                splits[i * 2 + 1] = argSplit[1];
            }


            Pair<String, String>[] arguments = new Pair[splits.length / 2];
            for (int i = 0; i < splits.length / 2; i++) {
                var arg = new Pair<String, String>(splits[i * 2], splits[i * 2 + 1]);
                arguments[i] = arg;
            }

            numberOfThreads = readNumberOfThreads(arguments);
            sampleRate = readSampleRate(arguments);
            bounces = readBounces(arguments);
        }

        //create Render
        toRender = new SceneRenderer(500,500, numberOfThreads, sampleRate, bounces);
        //set scene to render
        toRender.setScene(setupSkyBoxScene());
        launch(args);
    }

    private static Scene setupCornellBox() {
        var scene = new Scene(new Vec3(0,0,-4), new Vec3(0,0,6), 36);
        scene.addSphereShiny(new Vec3(-1001, 0, 0), 1000, new Vec3(0.3, 0, 0), Vec3.ONE);
        scene.addSphereShiny(new Vec3(1001, 0, 0), 1000, new Vec3(0, 0, 0.3), Vec3.ONE);
        scene.addSphereShiny(new Vec3(0, 0, 1001), 1000, new Vec3(0.1, 0.1, 0.1), Vec3.ONE);
        scene.addSphereShiny(new Vec3(0, 1001, 0), 1000, new Vec3(0.1, 0.1, 0.1), Vec3.ONE);
        scene.addSphereEmmissive(new Vec3(0, -1001, 0), 1000, new Vec3(0.8, 0.8, 0.8), Vec3.ONE.scale(4f));

        scene.addSphereShiny(new Vec3(-0.6, 0.7, -0.6), 0.3f, new Vec3(0.42, 0.42, 0), Vec3.ONE);
        try {
            var fireTexure = new SphereSphericalTextureMapping("earth.tif");
            scene.addSphereTextureShiny(new Vec3(0.3, 0.3, -0.3), 0.6f, fireTexure, Vec3.ONE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scene;
    }

    private static Scene setupSkyBoxScene(){
        var scene = new Scene(new Vec3(0,-3,-3), new Vec3(0,0,0), 36);
        scene.addSphereShiny(new Vec3(0,1001,0), 1000, Vec3.ZERO, Vec3.ONE); //ground

        //add skybox
        try {
            var skyTexture = new SphereSphericalTextureMapping("small_cathedral_02.jpg");
            scene.addSphereTextureEmissive(new Vec3(0,0,0), 1000, skyTexture, new Vec3(0.01,0.01,0.01));
        } catch (IOException e) {
            e.printStackTrace();
        }

        scene.addSphereShiny(new Vec3(0,0,0), 1, Vec3.ZERO, Vec3.ONE);
        return scene;
    }


    public void start(Stage primaryStage) throws InterruptedException {
        var scene = toRender.startRender();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static int readNumberOfThreads(Pair<String, String>[] arguments) {
        for (Pair<String, String> argument : arguments) {
            if ("-threads".equals(argument.getKey()))
                return Integer.parseInt(argument.getValue());
        }
        return 1;
    }

    private static int readSampleRate(Pair<String, String>[] arguments) {
        for (Pair<String, String> argument : arguments) {
            if ("-sampleRate".equals(argument.getKey()))
                return Integer.parseInt(argument.getValue());
        }
        return 32;
    }

    private static int readBounces(Pair<String, String>[] arguments) {
        for (Pair<String, String> argument : arguments) {
            if ("-bounces".equals(argument.getKey()))
                return Integer.parseInt(argument.getValue());
        }
        return -1;
    }
}
