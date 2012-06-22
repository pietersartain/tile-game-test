import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;

public class TileUtils {
	
	
	private HashMap tiles;
	private HashMap objects;

	private TileMap tilemap;
	private Player player;
	private int turn;

	public TileUtils() {
		tiles = new HashMap();
		objects = new HashMap();
		loadImages("tile",tiles);
		loadImages("object",objects);
		try {
			tilemap = loadMap("maps/map.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failed to load the map.");
			e.printStackTrace();
		}
	}

/**************
 *   IMAGE 	  *
 **************/

	public static Image loadImage(String name) {
        String filename = "images/" + name;
        return new ImageIcon(filename).getImage();
    }		
	
	public void loadImages(String type, HashMap map) {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ directory
        char ch = 'A';
        while (ch != ('Z'+1)) {
            String name = type + "_" + ch + ".png";
            File file = new File("images/" + name);
            if (file.exists()) {
                map.put(ch,loadImage(name));
            }
            ch++;
        }
        
//        tiles.put("player",loadImage("player4.png"));
    }

	
/**************
 *  TILE MAP  *
 **************/
	
	public void setObject(int x, int y, char object, boolean passable) {
		//if (tilemap.getObject(x, y) != null) {
			tilemap.setObject(x, y, (Image)objects.get(object), passable);
		//}
	}
	
	public int getTurn() {
		return turn;
	}
	
	public TileMap getMap() {
		return tilemap;
	}
	
	public void movePlayer(int x,int y) {
		if (tilemap.getObstruction(player.getX()+x,player.getY()+y) == false) {
			player.movePlayer(x, y);
			turn++;
		}
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Image getPlayerSprite() {
		return player.getPlayerSprite();
	}
	
    private TileMap loadMap(String filename) throws IOException
    {
        ArrayList<String> lines = new ArrayList<String>();
        int width = 0;
        int height = 0;

        // read every line in the text file into the list
        BufferedReader reader = new BufferedReader(
            new FileReader(filename));
        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            // add every line except for comments
            if (!line.startsWith("#")) {
                lines.add(line);
                width = Math.max(width, line.length());
            }
        }

        // parse the lines to create a TileEngine
        height = lines.size();
        tilemap = new TileMap(width, height);
        for (int y=0; y<height; y++) {
            String line = (String)lines.get(y);
            for (int x=0; x<line.length(); x++) {
                char ch = line.charAt(x);

                if (ch == '*') {
            		player = new Player(x,y);
                	//tilemap.setTile(x, y, (Image)tiles.get("player"));
                } else {
                	if ((Image)tiles.get(ch) == null) {
                		tilemap.setTile(x, y, null);
                	} else {
                		tilemap.setTile(x, y, (Image)tiles.get(ch), true);
                	}
                }
            }
        }

        return tilemap;
    }

}
