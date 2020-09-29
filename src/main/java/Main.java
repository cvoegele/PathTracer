import javafx.application.Application;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import util.Vec2;
import util.Vec3;

public class Main extends Application {

    private int width = 480;
    private int height = 480;

    Vec3 eye = new Vec3(0, 0, -4);
    Vec3 lookAt = new Vec3(0, 0, 6);
    double FOV = 36;

    Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        initScene();
        ImageView view = RenderCornellBox(1024);
        javafx.scene.Scene scene = new javafx.scene.Scene(new VBox(view), width, height);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initScene() {
        Sphere left = new Sphere(new Vec3(-1001, 0, 0), 1000, new Vec3(0.9, 0, 0), new Vec3(0, 0, 0));
        Sphere right = new Sphere(new Vec3(1001, 0, 0), 1000, new Vec3(0, 0, 0.9), new Vec3(0, 0, 0));
        Sphere back = new Sphere(new Vec3(0, 0, 1001), 1000, new Vec3(0.5, 0.5, 0.5), new Vec3(0, 0, 0));
        Sphere bot = new Sphere(new Vec3(0, 1001, 0), 1000, new Vec3(0.5, 0.5, 0.5), new Vec3(0, 0, 0));
        Sphere top = new Sphere(new Vec3(0, -1001, 0), 1000, new Vec3(0.8, 0.8, 0.8), new Vec3(1, 1, 1).scale(10));
        Sphere yellowBall = new Sphere(new Vec3(-0.6, 0.7, -0.6), 0.3f, new Vec3(0.9, 0.9, 0), new Vec3(0, 0, 0));
//        Sphere yellowBall2 = new Sphere(new Vec3(-0.6, 0.7, 0.5), 0.3f, new Vec3(0, 0.9, 0.9));
        Sphere lightBlueBall = new Sphere(new Vec3(0.3, 0.4, 0.3), 0.6f, new Vec3(0, 0.7, 0.7), new Vec3(0, 0, 0));
//        Sphere lightBlueBall = new Sphere(new Vec3(0, 0, 0.3), 0.6f, new Vec3(0, 0.7, 0.7),new Vec3(0,0,0));

        scene = new Scene(new SceneElement[]{left, right, back, bot, top, yellowBall, lightBlueBall});
//        scene = new Scene(new SceneElement[]{ lightBlueBall});
    }

    private ImageView RenderCornellBox(int sampleRate) {
        WritableImage image = new WritableImage(width, height);
        PixelWriter writer = image.getPixelWriter();

        MyRenderer renderer = new MyRenderer(eye, lookAt, FOV);

        //go through all pixels
        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {
                double y = (((double) v / height) * 2) - 1;
                double x = (((double) u / width) * 2) - 1;

                Vec3[] colors = new Vec3[sampleRate];
                for (int i = 0; i < sampleRate; i++) {
                    Ray ray = renderer.CreateEyeRay(eye, lookAt, FOV, new Vec2(x, y));
                    Vec3 color = renderer.ComputeColor(scene, ray);
                    colors[i] = color;
                }
                Vec3 sum = new Vec3(0,0,0);
                for (int i = 0; i < sampleRate; i++) {
                    sum = sum.add(colors[i]);
                }
                Vec3 finalColor = new Vec3(sum.x/sampleRate, sum.y/sampleRate, sum.z/sampleRate);
                
                writer.setColor(u, v, Color.rgb(clamp(finalColor.x), clamp(finalColor.y), clamp(finalColor.z)));
            }
        }


        ImageView view = new ImageView();
        view.setImage(image);
        return view;
    }

    private int clamp(float a) {
        if (a > 255) return 255;
        return (int) Math.max(a, 0);
    }

}
