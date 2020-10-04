package ch.voegele;

public class Scene {

    private SceneElement[] objects;

    public Scene(SceneElement[] objects) {
        this.objects = objects;
    }

    public SceneElement[] getObjects() {
        return objects;
    }
}
