package ch.voegele.UI;

import ch.voegele.util.Vec3;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ObservableImage {

    //Observable Implementation
    List<PixelChangeListener> listeners = new CopyOnWriteArrayList<PixelChangeListener>();

    public void addListener(PixelChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeListeners(PixelChangeListener listener) {
        listeners.remove(listener);
    }

    private final Vec3[][] image;
    private final int height;
    private final int width;

    public ObservableImage(int height, int width) {
        this.height = height;
        this.width = width;
        image = new Vec3[height][width];
    }

    public void setPixel(int u, int v, Vec3 color) throws IndexOutOfBoundsException {
        if (u >= width || u < 0)
            throw new IndexOutOfBoundsException("Index " + u + " was out of bounds for Range " + width);
        if (v >= height || v < 0)
            throw new IndexOutOfBoundsException("Index " + v + " was out of bounds for Range " + height);

        image[v][u] = color;

        for (var listener : listeners) {
            listener.pixelChanged(u, v, color);
        }
    }

    public Vec3 getPixel(int u, int v){
        if (u >= width || u < 0)
            throw new IndexOutOfBoundsException("Index " + u + " was out of bounds for Range " + width);
        if (v >= height || v < 0)
            throw new IndexOutOfBoundsException("Index " + v + " was out of bounds for Range " + height);

        return image[v][u];
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
