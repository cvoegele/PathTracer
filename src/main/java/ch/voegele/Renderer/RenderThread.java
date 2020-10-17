package ch.voegele.Renderer;

import ch.voegele.util.Vec2;
import ch.voegele.util.Vec3;
import ch.voegele.util.VectorHelpers;

import java.util.Random;

/***
 * Thread that computes a certain part of the image.
 * The thread writes its output into the passed sceneRenderer Object, which also created the thread.
 */
public class RenderThread extends Thread {

    /***
     * number of this thread
     */
    private final int threadNumber;

    /***
     * starting y coordinate of this thread's work
     */
    private final int start;

    /***
     * ending y coordinate fo this threads work
     */
    private final int end;

    /***
     * height of the image
     */
    private final int height;

    /***
     * width of the image
     */
    private final int width;

    /***
     * the amount of rays that the thread shoots at each pixel
     */
    private final int sampleRate;

    /***
     * whether gaussian Anti-Aliasing is turned on or off
     */
    private final boolean gaussianAAOn;

    /***
     * RenderEngine object to do the actual computation
     */
    private final RenderEngine renderer;

    /***
     * Object that created the thread. Used as a central storage for the output of the image
     */
    private final SceneRenderer sceneRenderer;

    /***
     * scene with all the geometric objects that has to be rendered
     */
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
        helpers = new VectorHelpers(2.2); //gamma is hardcoded for the rgb conversion
    }

    /***
     * run() method, that each thread executes upon t.start().
     * Shoots sampleRate amount of rays at each pixel between y = start and y = end (full rows)
     * and writes the output into sceneRenderer.observableImage, so that it can be retrieved upon finished or during the runtime of the thread
     */
    @Override
    public void run() {
        //new random instance for every thread
        Random r = new Random();

        //sigma for gaussianAA
        var sigmaX = 1d / width;
        var sigmaY = 1d / height;

        for (int v = start; v < end; v++) {
            for (int u = 0; u < width; u++) {
                double y = (((double) v / height) * 2) - 1;
                double x = (((double) u / width) * 2) - 1;

                //cast as many rays as sampleRate
                Vec3[] colors = new Vec3[sampleRate];
                for (int i = 0; i < sampleRate; i++) {

                    double newX = x, newY = y;

                    //nudge pixels when gaussian is on
                    if (gaussianAAOn) {
                        var xNudge = SceneRenderer.nextGaussianNormalDistribution(r, sigmaX);
                        var yNudge = SceneRenderer.nextGaussianNormalDistribution(r, sigmaY);
                        newX += xNudge;
                        newY += yNudge;
                    }

                    //cast first ray
                    Ray ray = renderer.CreateEyeRay(scene.getEye(), scene.getLookAt(), scene.getFOV(), new Vec2(newX, newY));
                    //compute color of this pixel
                    var color = renderer.ComputeColor(scene, ray);

                    colors[i] = color;
                }

                //sum and average the colors of each sample
                Vec3 sum = new Vec3(0, 0, 0);
                for (int i = 0; i < sampleRate; i++) {
                    sum = sum.add(colors[i]);
                }
                sum = sum.scale(1f / sampleRate);

                //gamma correction with gamma of 2.2 and clamping
                Vec3 finalColor = helpers.RGBto_sRGB(sum);

                sceneRenderer.observableImage.setPixel(u, v, finalColor);
            }
        }
        //print time used
        var endTime = System.currentTimeMillis() - startTime;
        System.out.println("Full time used in Thread " + threadNumber + " in s: " + endTime / 1000);
    }

}
