package ch.voegele;

import javafx.application.Application;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Timer;

public class Main extends Application {

    private static CornellBox box;


    public static void main(String[] args) {
        var numberOfThreads = 1;
        var sampleRate = 4;
        var bounces = 10;

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
        return 1;
    }

    private static int readBounces(Pair<String, String>[] arguments) {
        for (Pair<String, String> argument : arguments) {
            if ("-sampleRate".equals(argument.getKey()))
                return Integer.parseInt(argument.getValue());
        }
        return 1;
    }


}
