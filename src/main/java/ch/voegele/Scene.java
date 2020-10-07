package ch.voegele;

public class Scene {

    private ISceneElement[] objects;

    public Scene(ISceneElement[] objects) {
        this.objects = objects;
    }

    public ISceneElement[] getObjects() {
        return objects;
    }
}
