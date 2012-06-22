import java.awt.Image;
import java.io.File;
import java.util.HashMap;

import javax.swing.ImageIcon;


public class Player {

	private int x;
	private int y;
	private int facing; // N, E, S, W <==> 0, 1, 2, 3
	private HashMap playerImages;
	private int turn;
	
	public Player(int x, int y) {
		this.x = x;
		this.y = y;
		loadPlayerImages();
		turn = 0;
	}

	//*********** ACCESSORS ************//
	
	public int getFacing() {
		return facing;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Image getPlayerSprite() {
		return (Image)playerImages.get(facing);
	}
	
	public int getTurn() {
		return turn;
	}
	
	//*********** MUTATORS ************//
	
	public void movePlayer(int x, int y) {
		this.x += x;
		this.y += y;
		turn++;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setFacing(int facing) {
		this.facing = facing;
	}

	//*********** PRIVATE ************//
	
	public static Image loadImage(String name) {
		String filename = "images/" + name;
		return new ImageIcon(filename).getImage();
	}		

	public void loadPlayerImages() {
		// keep looking for tile A,B,C, etc. this makes it
		// easy to drop new tiles in the images/ directory
		playerImages = new HashMap();
		for (int x = 0; x <= 4; x++) {
			String name = "player" + x + ".png";
			File file = new File("images/" + name);
			if (file.exists()) {
				playerImages.put(x,loadImage(name));
			}
		}
	}

}
