package ch.voegele.UI;

import ch.voegele.util.Vec3;

/***
 * Listener Interface for ObservableImage
 */
public interface PixelChangeListener {
     void pixelChanged(int u, int v, Vec3 color);
}
