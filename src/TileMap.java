import java.awt.Dimension;
import java.awt.Image;

public class TileMap {

	private Image[][] tiles;
	private boolean[][] obstructions;
	private Image[][] objects;

	public TileMap(int h, int w) {
		tiles = new Image[h][w];
		objects = new Image[h][w];
		obstructions = new boolean[h][w];
	}
	
	//********** ACCESSORS **********//
	public Image getTile(int x, int y) {
		try {
			return tiles[x][y];
		} catch(ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
		
	public boolean getObstruction(int x, int y) {
		try {
			return obstructions[x][y];
		} catch(ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	public Image getObject(int x, int y) {
		try {
			return objects[x][y];
		} catch(ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	public Dimension size() {
		return new Dimension(tiles.length,tiles[1].length);
	}
	
	public int getHeight() {
		return tiles.length;
	}
	
	public int getWidth() {
		return tiles[1].length;
	}
	
	//********** MUTATORS **********//
	public void setTile(int x, int y, Image tile) {
		setTile(x,y,tile,false);
	}
	
	public void setTile(int x, int y, Image tile, boolean passable) {
		tiles[x][y] = tile;
		obstructions[x][y] = passable;
	}
	
	public void setObject(int x, int y, Image object, boolean passable) {
		objects[x][y] = object;
		obstructions[x][y] = passable;
	}
	
}
