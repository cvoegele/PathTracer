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

    private int width = 1024;
    private int height = 1024;

    Vec3 eye = new Vec3(0, 0, 4);
    Vec3 lookAt = new Vec3(0, 0, 6);
    double FOV = 36;

    Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        initScene();
        ImageView view = RenderCornellBox();
        javafx.scene.Scene scene = new javafx.scene.Scene(new VBox(view), width, height);
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    private void initScene() {
        Sphere left = new Sphere(new Vec3(-1001, 0, 0), 1000, new Vec3(0.9, 0, 0));
        Sphere right = new Sphere(new Vec3(1001, 0, 0), 1000, new Vec3(0, 0, 0.9));
        Sphere back = new Sphere(new Vec3(0, 0, -1001), 1000, new Vec3(0.5, 0.5, 0.5));
        Sphere bot = new Sphere(new Vec3(0, 1001, 0), 1000, new Vec3(0.5, 0.5, 0.5));
        Sphere top = new Sphere(new Vec3(0, -1001, 0), 1000, new Vec3(0.8, 0.8, 0.8));
        Sphere yellowBall = new Sphere(new Vec3(-0.6, 0.7, 0.6), 0.3f, new Vec3(0.9, 0.9, 0));
        Sphere lightBlueBall = new Sphere(new Vec3(0.3, 0.4, -0.3), 0.6f, new Vec3(0, 0.7, 0.7));

        scene = new Scene(new SceneElement[]{left, right, back, bot, top, yellowBall, lightBlueBall});
    }

    private ImageView RenderCornellBox() {
        WritableImage image = new WritableImage(width, height);
        PixelWriter writer = image.getPixelWriter();

        MyRenderer renderer = new MyRenderer(eye, lookAt, FOV);

        //go through all pixels
        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {
                double y = (((double) v / height) * 2) - 1;
                double x = (((double) u / width) * 2) - 1;
                Ray ray = renderer.CreateEyeRay(eye, lookAt, FOV, new Vec2(x, y));
                Vec3 color = renderer.ComputeColor(scene, ray);
                writer.setColor(u, v, Color.color(color.x, color.y, color.z));
            }
        }


        ImageView view = new ImageView();
        view.setImage(image);
        return view;
    }


}
