package ch.voegele;

import javafx.application.Application;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Timer;

public class Main extends Application {

    private static CornellBox box;

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
                var arg = new Pair<String, String>(splits[i], splits[i + 1]);
                arguments[i] = arg;
            }

            numberOfThreads = readNumberOfThreads(arguments);
            sampleRate = readSampleRate(arguments);
            bounces = readBounces(arguments);
        }

        //create RenderBox
        box = new CornellBox(numberOfThreads, sampleRate, bounces);
        launch(args);
    }

    public void start(Stage primaryStage) throws InterruptedException {
        var scene = box.startRender();
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
            if ("-sampleRate".equals(argument.getKey()))
                return Integer.parseInt(argument.getValue());
        }
        return -1;
    }


}
