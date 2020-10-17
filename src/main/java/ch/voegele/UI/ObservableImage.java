package ch.voegele.UI;

import ch.voegele.util.Vec3;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/***
 * Observable Image container
 * To listens to changes. Register any PixelChangeListener on the image
 */
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

    /**
     * internal storage of pixels
     */
    private final Vec3[][] image;
    /***
     * height of image
     */
    private final int height;

    /***
     * with of image
     */
    private final int width;

    public ObservableImage(int height, int width) {
        this.height = height;
        this.width = width;
        image = new Vec3[height][width];
    }

    /***
     * set pixel in image
     * @param u horizontal coordinate u = 0 @ left
     * @param v vertical coordinate v = 0 @ top
     * @param color color to set as a Vec3
     * @throws IndexOutOfBoundsException
     */
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

    /***
     * get pixel on image
     * @param u horizontal coordinate u = 0 @ left
     * @param v vertical coordinate v = 0 @ top
     * @return color of pixel as Vec3
     */
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
