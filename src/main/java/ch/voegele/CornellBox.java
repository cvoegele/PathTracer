package ch.voegele;

import ch.voegele.Texture.SpherePlanarTextureMapping;
import ch.voegele.util.MathUtilities;
import ch.voegele.util.Vec2;
import ch.voegele.util.Vec3;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.TimerTask;

public class CornellBox {

    private int width = 480;
    private int height = 480;

    Vec3 eye = new Vec3(0, 0, -4);
    Vec3 lookAt = new Vec3(0, 0, 6);
    double FOV = 36;
    int numberOfThreads;
    int sampleRate;
    int bounces;

    private PixelWriter writer;
    private Vec3[][] imageArray;

    Scene scene;
    private Thread[] threads;
    private long startTime;


    public CornellBox(int numberOfThreads, int sampleRate, int bounces) {
        this.numberOfThreads = numberOfThreads;
        this.sampleRate = sampleRate;
        this.bounces = bounces;
    }

    public javafx.scene.Scene startRender() throws InterruptedException {
        startTime = System.currentTimeMillis();

        try {
            initScene();
        } catch (IOException e) {
            e.printStackTrace();
        }

        var view = RenderCornellBox(sampleRate, numberOfThreads);
        saveRenderAsImage();
        return new javafx.scene.Scene(new VBox(view), width, height);
    }


    private void initScene() throws IOException {
        Sphere left = new Sphere(new Vec3(-1001, 0, 0), 1000, new Vec3(0.3, 0, 0), new Vec3(0, 0, 0), Vec3.ZERO);
        Sphere right = new Sphere(new Vec3(1001, 0, 0), 1000, new Vec3(0, 0, 0.3), new Vec3(0, 0, 0), Vec3.ZERO);
        Sphere back = new Sphere(new Vec3(0, 0, 1001), 1000, new Vec3(0.1, 0.1, 0.1), new Vec3(0, 0, 0), Vec3.ZERO);
        Sphere bot = new Sphere(new Vec3(0, 1001, 0), 1000, new Vec3(0.3, 0.3, 0.3), new Vec3(0, 0, 0), Vec3.ZERO);
        Sphere top = new Sphere(new Vec3(0, -1001, 0), 1000, new Vec3(0.3, 0.3, 0.3), new Vec3(1, 1, 1).scale(5f), Vec3.ZERO);
        Sphere yellowBall = new Sphere(new Vec3(-0.6, 0.7, -0.6), 0.3f, new Vec3(0.42, 0.42, 0), new Vec3(0, 0, 0), new Vec3(0.1, 0.1, 0.1));
//        CornellBox.Sphere yellowBall2 = new CornellBox.Sphere(new Vec3(-0.6, 0.7, 0.5), 0.3f, new Vec3(0, 0.9, 0.9));
        Sphere lightBlueBall = new Sphere(new Vec3(0.3, 0.4, 0.3), 0.6f, Vec3.ZERO, Vec3.ZERO, new Vec3(0.1, 0.1, 0.1));
        SpherePlanarTextureMapping textureMapping = new SpherePlanarTextureMapping("fire.jpg");
        lightBlueBall.setTextureMapper(textureMapping);
//        CornellBox.Sphere lightBlueBall = new CornellBox.Sphere(new Vec3(0, 0, 0.3), 0.6f, new Vec3(0, 0.7, 0.7),new Vec3(0,0,0));

        scene = new Scene(new ISceneElement[]{left, right, back, bot, top, yellowBall, lightBlueBall});
//        scene = new CornellBox.Scene(new CornellBox.SceneElement[]{ lightBlueBall});
    }

    private ImageView RenderCornellBox(int sampleRate, int numberOfThreads) throws InterruptedException {
        WritableImage image = new WritableImage(width, height);
        writer = image.getPixelWriter();

        MyRenderer renderer = new MyRenderer(eye, lookAt, FOV);
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
                            Ray ray = renderer.CreateEyeRay(eye, lookAt, FOV, new Vec2(x, y));

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
                        writer.setColor(u, v, finalColor.toColor());
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


        ImageView view = new ImageView();
        view.setImage(image);
        return view;
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
