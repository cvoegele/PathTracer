package ch.voegele.Texture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/***
 * Abstract Class Implementing storage option for a bitmap texture
 */
public abstract class AbstractBitmapTexture {

    protected BufferedImage texture;

    public void setTexture(String path) throws IOException {
        texture = ImageIO.read(getClass().getResource("/" + path));
    }
}
