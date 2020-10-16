package ch.voegele.Renderer;

import ch.voegele.UI.ObservableImage;
import ch.voegele.util.Vec3;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

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

    private PixelWriter writer;
    public final ObservableImage observableImage;


    public SceneRenderer(int width, int height, int numberOfThreads, int sampleRate, boolean gaussianAA, ObservableImage observableImage) {
        this.width = width;
        this.height = height;
        this.numberOfThreads = numberOfThreads;
        this.sampleRate = sampleRate;
        this.gaussianAA = gaussianAA;
        this.observableImage = observableImage;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public boolean startRender() {
        if (scene == null) throw new NullPointerException("Scene was not set!");

        var image = new WritableImage(width, height);
        ImageView view = new ImageView(image);
        writer = image.getPixelWriter();

        new Thread(() -> {
            try {
                renderScene();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            saveRenderAsImage();
        }).start();

        return true;
    }

    private void renderScene() throws InterruptedException {
        RenderEngine renderer = new RenderEngine(scene.getEye(), scene.getLookAt(), scene.getFOV());

        //go through all pixels
        var parts = height / numberOfThreads;

        //enable correction thread if division in threads leaves empty rows
        var lostRows = 0;
        var extraThread =0;
        if (parts * numberOfThreads < height) {
            lostRows = height - parts * numberOfThreads;
            extraThread++;
        }
        Thread[] threads = new Thread[numberOfThreads + extraThread];
        //st
        if (lostRows != 0) {
            var start = parts * (numberOfThreads);
            var end = start + lostRows - 1;
            threads[numberOfThreads] = new RenderThread(numberOfThreads,
                    start,
                    end,
                    height,
                    width,
                    sampleRate,
                    gaussianAA,
                    renderer,
                    this,
                    scene);
            threads[numberOfThreads].start();
        }

        //start normal threads
        for (int threadNumber = 0; threadNumber < numberOfThreads; threadNumber++) {
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
                    this,
                    scene);

            threads[threadNumber].start();
        }


        for (Thread thread : threads) {
            thread.join();
        }
    }


    private void saveRenderAsImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {
                var finalColor = observableImage.getPixel(u, v);
                if (finalColor != null)
                    image.setRGB(u, v, finalColor.toRGB());
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
