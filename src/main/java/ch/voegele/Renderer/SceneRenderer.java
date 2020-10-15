package ch.voegele.Renderer;

import ch.voegele.util.Vec3;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SceneRenderer {

    private final int width;
    private final int height;

    private int numberOfThreads;
    private final int sampleRate;
    boolean gaussianAA;
    Scene scene;

    WritableImage image;
    private PixelWriter writer;
    private Vec3[][] imageArray;


    public SceneRenderer(int width, int height, int numberOfThreads, int sampleRate, boolean gaussianAA) {
        this.width = width;
        this.height = height;
        this.numberOfThreads = numberOfThreads;
        this.sampleRate = sampleRate;
        this.gaussianAA = gaussianAA;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public javafx.scene.Scene startRender() {
        if (scene == null) throw new NullPointerException("Scene was not set!");

        var image = new WritableImage(width, height);
        imageArray = new Vec3[width][height];
        ImageView view = new ImageView(image);
        writer = image.getPixelWriter();

        Timer timer = new Timer();
        new Thread(() -> {
            try {
                renderScene();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            saveRenderAsImage();
            timer.cancel(); // cancel timer
            new UpdateWindow().run(); //update one last time
        }).start();

        timer.schedule(new UpdateWindow(), 1000, 2000);

        return new javafx.scene.Scene(new VBox(view), width, height);
    }

    private void renderScene() throws InterruptedException {


        RenderEngine renderer = new RenderEngine(scene.getEye(), scene.getLookAt(), scene.getFOV());
        imageArray = new Vec3[height][width];

        //go through all pixels
        var parts = height / numberOfThreads;

        //enable correction thread if division in threads leaves empty rows
        var lostRows = 0;
        if (parts * numberOfThreads < height) {
            lostRows = height - parts * numberOfThreads;
            numberOfThreads++;
        }
        Thread[] threads = new Thread[numberOfThreads];
        //st
        if (lostRows != 0) {
            var start = parts * (numberOfThreads - 1);
            var end = start + lostRows - 1;
            threads[numberOfThreads - 1] = new RenderThread(numberOfThreads - 1,
                    start,
                    end,
                    height,
                    width,
                    sampleRate,
                    gaussianAA,
                    renderer,
                    scene,
                    imageArray);
            threads[numberOfThreads - 1].start();
        }

        //start normal threads
        for (int threadNumber = 0; threadNumber < numberOfThreads - 1; threadNumber++) {
            var start = parts * threadNumber;
            var end = parts * threadNumber + parts;

            threads[threadNumber] = new RenderThread(threadNumber,
                    start,
                    end,
                    height,
                    width,
                    sampleRate,
                    gaussianAA,
                    renderer,
                    scene,
                    imageArray);

            threads[threadNumber].start();
        }


        for (Thread thread : threads) {
            thread.join();
        }
    }

    private class UpdateWindow extends TimerTask {
        @Override
        public void run() {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    var finalColor = imageArray[j][i];
                    if (finalColor != null)
                        writer.setColor(j, i, finalColor.toColor());
                }
            }
        }
    }

    private void saveRenderAsImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                var finalColor = imageArray[j][i];
                if (finalColor != null)
                    image.setRGB(j, i, finalColor.toRGB());
            }
        }
        try {
            ImageIO.write(image, "bmp", new FileOutputStream("output/render_H" + LocalDateTime.now().getHour() + "_M" + LocalDateTime.now().getMinute() + ".bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static double nextGaussianNormalDistribution(Random r, double sigma) {
        var normalGaussian = r.nextGaussian();
        return normalGaussian * sigma;
    }

}
