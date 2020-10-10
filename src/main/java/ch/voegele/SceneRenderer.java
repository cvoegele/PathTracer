package ch.voegele;

import ch.voegele.Texture.SphereSphericalTextureMapping;
import ch.voegele.util.Vec2;
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
import java.util.Timer;
import java.util.TimerTask;

public class SceneRenderer {

    private int width;
    private int height;

    int numberOfThreads;
    int sampleRate;
    int bounces;

    private PixelWriter writer;
    private Vec3[][] imageArray;

    Scene scene;
    private Thread[] threads;
    private long startTime;
    WritableImage image;


    public SceneRenderer(int width , int height ,int numberOfThreads, int sampleRate, int bounces) {
        this.width = width;
        this.height = height;
        this.numberOfThreads = numberOfThreads;
        this.sampleRate = sampleRate;
        this.bounces = bounces;
    }

    public void setScene(Scene scene){
        this.scene = scene;
    }

    public javafx.scene.Scene startRender() {
        if (scene == null) throw new NullPointerException("Scene was not set!");

        startTime = System.currentTimeMillis();

        image = new WritableImage(width, height);
        imageArray = new Vec3[width][height];
        ImageView view = new ImageView(image);

        new Thread(() -> {
            try {
                renderScene();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            saveRenderAsImage();
        }).start();

        Timer timer = new Timer();
        timer.schedule(new UpdateWindow(), 1000, 2000);

        return new javafx.scene.Scene(new VBox(view), width, height);
    }

    private void renderScene() throws InterruptedException {
        //WritableImage image = new WritableImage(width, height);
        writer = image.getPixelWriter();

        RenderEngine renderer = new RenderEngine(scene.getEye(), scene.getLookAt(), scene.getFOV());
        imageArray = new Vec3[height][width];

        //go through all pixels
        var parts = height / numberOfThreads;
        threads = new Thread[numberOfThreads];
        for (int partIndex = 0; partIndex < numberOfThreads; partIndex++) {
            var start = parts * partIndex;
            var end = parts * partIndex + parts;

            int finalPartIndex = partIndex;
            threads[partIndex] = new Thread(() -> {
                for (int v = start; v < end; v++) {
                    for (int u = 0; u < width; u++) {
                        double y = (((double) v / height) * 2) - 1;
                        double x = (((double) u / width) * 2) - 1;

                        Vec3[] colors = new Vec3[sampleRate];
                        for (int i = 0; i < sampleRate; i++) {
                            Ray ray = renderer.CreateEyeRay(scene.getEye(), scene.getLookAt(), scene.getFOV(), new Vec2(x, y));

                            Vec3 color;
                            if (bounces != -1)
                                //call with limited bounces
                                color = renderer.ComputeColor(scene, ray, bounces, 0);
                            else
                                //call with probability termination of bounces
                                color = renderer.ComputeColor(scene, ray);

                            colors[i] = color;
                        }
                        Vec3 sum = new Vec3(0, 0, 0);
                        for (int i = 0; i < sampleRate; i++) {
                            sum = sum.add(colors[i]);
                        }

                        var red = sum.x / (double) sampleRate;
                        var blue = sum.z / (double) sampleRate;
                        var green = sum.y / (double) sampleRate;

                        red = Math.pow(red, 1 / 2.2d);
                        blue = Math.pow(blue, 1 / 2.2d);
                        green = Math.pow(green, 1 / 2.2d);

                        Vec3 finalColor = new Vec3(red * 255, green * 255, blue * 255);
                        imageArray[u][v] = finalColor;
                        //writer.setColor(u, v, finalColor.toColor());
                    }
                }

                var endTime = System.currentTimeMillis() - startTime;
                System.out.println("Full time used in Thread " + finalPartIndex + " in s: " + endTime / 1000);
            });
            threads[partIndex].start();
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

}
