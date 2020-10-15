package ch.voegele.Renderer;

import ch.voegele.util.Vec2;
import ch.voegele.util.Vec3;
import ch.voegele.util.VectorHelpers;

import java.util.Random;

public class RenderThread extends Thread {

    private final int threadNumber;
    private final int start;
    private final int end;
    private final int height;
    private final int width;
    private final int sampleRate;
    private final boolean gaussianAAOn;
    private final RenderEngine renderer;
    private final SceneRenderer sceneRenderer;
    private final Scene scene;

    private final long startTime;
    private final VectorHelpers helpers;

    public RenderThread(int threadNumber,
                        int start,
                        int end,
                        int height,
                        int width,
                        int sampleRate,
                        boolean gaussianAAOn,
                        RenderEngine renderer,
                        SceneRenderer sceneRenderer, Scene scene) {
        this.threadNumber = threadNumber;
        this.start = start;
        this.end = end;
        this.height = height;
        this.width = width;
        this.sampleRate = sampleRate;
        this.gaussianAAOn = gaussianAAOn;
        this.renderer = renderer;
        this.sceneRenderer = sceneRenderer;
        this.scene = scene;
        startTime = System.currentTimeMillis();
        helpers = new VectorHelpers(2.2);
    }

    @Override
    public void run() {
        //new random instance for every thread
        Random r = new Random();

        for (int v = start; v < end; v++) {
            for (int u = 0; u < width; u++) {
                double y = (((double) v / height) * 2) - 1;
                double x = (((double) u / width) * 2) - 1;

                var sigmaX = 1d / width;
                var sigmaY = 1d / height;

                Vec3[] colors = new Vec3[sampleRate];
                for (int i = 0; i < sampleRate; i++) {

                    double newX = x, newY = y;

                    if (gaussianAAOn) {
                        var xNudge = SceneRenderer.nextGaussianNormalDistribution(r, sigmaX);
                        var yNudge = SceneRenderer.nextGaussianNormalDistribution(r, sigmaY);
                        newX += xNudge;
                        newY += yNudge;
                    }

                    Ray ray = renderer.CreateEyeRay(scene.getEye(), scene.getLookAt(), scene.getFOV(), new Vec2(newX, newY));

                    var color = renderer.ComputeColor(scene, ray);

                    colors[i] = color;
                }

                Vec3 sum = new Vec3(0, 0, 0);
                for (int i = 0; i < sampleRate; i++) {
                    sum = sum.add(colors[i]);
                }

                sum = sum.scale(1f / sampleRate);

                //gamma correction with gamma of 2.2
                Vec3 finalColor = helpers.RGBto_sRGB(sum);

                sceneRenderer.observableImage.setPixel(u, v, finalColor);
            }
        }

        var endTime = System.currentTimeMillis() - startTime;
        System.out.println("Full time used in Thread " + threadNumber + " in s: " + endTime / 1000);
    }

}
