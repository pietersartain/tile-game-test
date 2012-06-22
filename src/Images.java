import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Here is a nice self contained image class to make image handling for standard disk reading easy!
 * You should put this in it's own file and remove the STATIC for optimal use.
 */
public class Images {

	private BufferedImage bufferedImage; // Image itself
	private File imageFile; // File for image
	
	private ArrayList<Image> tiles;
	

	/**
	 * Creates a new image for you from the filename string given.
	 *
	 * @param fileName  The file name for the image.
	 */
	public Images() {
		loadTileImages();
	}
	

	public void create(String fileName) {
		// Create file
		imageFile = new File(fileName);
		try
		{
			// Read the file...
			bufferedImage = ImageIO.read(imageFile);
		}
		catch(IOException e)
		{
			// Something went wrong!
			System.out.println("Error loading "+fileName);
			System.out.println(e.toString());
		}
	}
	/**
	 * Draws the image on a Graphics2D context.
	 *
	 * @param g  The Graphics2D object to draw this image to.
	 */
	public void draw(Graphics2D g,double x,double y)
	{
		// Draw (we just want to place it somewhere, so AffineTransform has this method)
		g.drawRenderedImage(bufferedImage,AffineTransform.getTranslateInstance(x,y));
	}
	
	public Image loadImage(String name) {
        String filename = "images/" + name;
        return new ImageIcon(filename).getImage();
    }		
	
	public void loadTileImages() {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ directory
        tiles = new ArrayList<Image>();
        char ch = 'A';
        while (true) {
            String name = "tile_" + ch + ".png";
            File file = new File("images/" + name);
            if (!file.exists()) {
                break;
            }
            tiles.add(loadImage(name));
            ch++;
        }
    }
	
	public ArrayList<Image> getTiles() {
		return tiles;
	}
	
	
}
