//Changes by Owan - Thursday 1st October 2008
// - Key listener now checks for key codes rather than strings.
// - renderMap method rewritten from scratch. Old version still exists and is
//   called renderMapOLD. Outputs are nearly identical. New version avoids
//   the strange rounding artefact which was cropping up in renderMapOLD.
// - Added zoom functionality to renderMap.  renderMap now has an optional
//   int parameter which determines the size of a square.  The default size
//   is 32 pixels if no parameter is passed.


// JAVA-AWT
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
/**
 * Demonstrates the use of images in GUI windows.
 *
 * @author WolfCoder
 */
public class ImageTest extends JFrame
		implements KeyListener
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6543859117232249348L;
	private Images imageManager;
	private static TestCanvas testCanvas;
	private static String argList;
	
	private static BufferStrategy bufstrat;
	
	private static TileUtils tileUtils;
	
	public ImageTest() {
		imageManager = new Images();
		testCanvas = new TestCanvas(imageManager);
		testCanvas.addKeyListener(this);
		tileUtils = new TileUtils();

		// Forces the canvas to have focus onload, which means you can get stuck
		// in right away with the arrow keys
		addWindowListener( new WindowAdapter() {
			public void windowOpened( WindowEvent e ){
				testCanvas.requestFocus();
			}
		} );

	}

	/*
	public TestCanvas init() {
		TestCanvas testCanvas = new TestCanvas(imageManager);
		testCanvas.addKeyListener(this);
		return testCanvas;
	}
	*/
	
	public TestCanvas getCanvas(){
		return testCanvas;
	}


	/**
	 * This is the test canvas on which the image is drawn.
	 */
	public static class TestCanvas 
		extends Canvas
	{
	
		/**
		 * 
		 */
		private static final long serialVersionUID = -2154141738405682542L;
		private Images imageManager;
		
		/**
		 * Creates a new test canvas.
		 */
		public TestCanvas(Images imageManager)
		{
			this.imageManager = imageManager;
			// Set properties
			setSize(new Dimension(800,600));
			setBackground(new Color(100,100,100));			
		}
		
		/**
		 * Paints this canvas.
		 */
		
		// THIS HAPPENS WHEN YOU RESIZE THE WINDOW!
		public void paint(Graphics g)
		{
			// Use graphics 2D
			//Graphics2D g2d = (Graphics2D)g;
			Graphics2D g2d = (Graphics2D)bufstrat.getDrawGraphics();
			g2d.clearRect(0,0,getWidth(),getHeight()); // Clear the canvas from previous image
			// Draw something
			/*
			g2d.setColor(new Color(128,128,128)); // Use grey color
			g2d.drawRect(2,2,128-5,128-5); // Draw a border
			// Now draw the image (lol it might not fit!)
			imageManager.draw(g2d,0,0);
			*/
			renderMap();
			g2d.dispose();
			bufstrat.show();
		}
		
	}
	
		/*****************
		 * KEY LISTENERS *
		 *****************/
		public void keyPressed(KeyEvent e)
		{
			//cmd takes the value of the keycode associated with the keypress.
			int cmd = e.getKeyCode();
			int facing = tileUtils.getPlayer().getFacing();
			//37 repreesnts the left arrow key.
			if (cmd == 37) {
				if (facing == 3) {
					tileUtils.movePlayer(-1,0);
				} else {
					tileUtils.getPlayer().setFacing(3);
				}
			}
			else 
			//39 represents the right arrow key.
			if (cmd == 39) {
				if (facing == 1) {
					tileUtils.movePlayer(1,0);
				} else {
					tileUtils.getPlayer().setFacing(1);
				}				
			}
			else
			//38 represents the up arrow key.
			if (cmd == 38) {
				if (facing == 0) {
					tileUtils.movePlayer(0,-1);
				} else {
					tileUtils.getPlayer().setFacing(0);
				}
			}
			else
			//40 represents the down arrow key.
			if (cmd == 40) {
				if (facing == 2) {
					tileUtils.movePlayer(0,1);
				} else {
					tileUtils.getPlayer().setFacing(2);
				}
			}
			else
			//32 is the space bar.
			if (cmd == 32) {
				int px = tileUtils.getPlayer().getX();
				int py = tileUtils.getPlayer().getY();
				if (facing == 0) {
					tileUtils.setObject(px, py-1, 'A', false);
				} else if (facing == 1) {
					tileUtils.setObject(px+1, py, 'A', false);
				} else if (facing == 2) {
					tileUtils.setObject(px, py+1, 'A', false);
				} else if (facing == 3) {
					tileUtils.setObject(px-1, py, 'A', false);
				}
			}
	
			// Rerender map
			renderMap();
		}
		
		public void keyReleased(KeyEvent e){}
		public void keyTyped(KeyEvent e){}	

		//This is a rewrite of the renderMap method which considers only whole squares. The original version
		//has been renamed renderMapOLD.
		protected static void renderMap(){
			renderMap(32);	
		}
		protected static void renderMap(int tileSize){
		
			TileMap tmap = tileUtils.getMap();
			//The player character always appears in the middle of the canvas and his position never varies.
			//If we declare the top left corner (0,0) to be the origin of a square, the player character's
			//square must be determined.
			
			//Note that if there size of the canvas is not an exact multiple of tileSize, the part visible
			//squares around the right and/or bottom edges of the canvas are counted too.
			
			int totalSquaresX = (int)Math.ceil(testCanvas.getWidth()/tileSize);
			int totalSquaresY = (int)Math.ceil(testCanvas.getHeight()/tileSize);

			//A decision must now be made about which square the player appears in.  If the total is an odd
			//number, the decision is simple as there's an actual centre.  If the total is an even number
			//the decision is more complicated. I have chosen here to always round down as it will improve
			//the visual effect of resizing the screen due to the possibility of partial squares on the right
			//and/or buttom edges of the canvas.

			int playerX = (int)Math.floor(totalSquaresX/2);
			int playerY = (int)Math.floor(totalSquaresY/2);

			//Now we must determine the tile map's offset.  This again is measured in squares.
			
			int mapOriginX = tileUtils.getPlayer().getX() - playerX;
			int mapOriginY = tileUtils.getPlayer().getY() - playerY;

			//Now we clear the map and then draw the gridlines onto the screen.
			Graphics2D g2d = (Graphics2D)bufstrat.getDrawGraphics();
			g2d.clearRect(0,0,testCanvas.getWidth(),testCanvas.getHeight());
			g2d.setColor(new Color(32,32,32));
			for (int x=0;x<=totalSquaresX;x++){
				for (int y=0;y<=totalSquaresY;y++){
				  g2d.drawRect(x*tileSize,y*tileSize,tileSize,tileSize);
				}
			}
			
			//Now we draw the map itself.
			for (int x=0;x<=totalSquaresX;x++){
				for (int y=0;y<=totalSquaresY;y++){
					g2d.drawImage(tmap.getTile(mapOriginX+x,mapOriginY+y),x*tileSize,y*tileSize,tileSize,tileSize,null,null);
				}	
			}					
			
			//Now we draw the items which appear on the map.
			for (int x=0;x<=totalSquaresX;x++){
				for (int y=0;y<=totalSquaresY;y++){
					g2d.drawImage(tmap.getObject(mapOriginX+x,mapOriginY+y),x*tileSize,y*tileSize,tileSize,tileSize,null,null);
				}	
			}					
		
			//Now we draw the player character.
			g2d.drawImage(tileUtils.getPlayerSprite(),playerX*tileSize,playerY*tileSize,tileSize,tileSize,null,null);		
			
			//Now we display the turn information.
			g2d.setColor(new Color(0,0,0));
			g2d.fillRect(2,2,110,35);
			g2d.setColor(new Color(255,255,255));
			g2d.drawString("World turn:  "+tileUtils.getTurn(),5,15);
			g2d.drawString("Player turn: "+tileUtils.getPlayer().getTurn(),5,30);
			
			//Now we display the image and flush the buffer...or something like that.
			g2d.dispose();
			bufstrat.show();		
		}
		

		private static final int TILE_SIZE = 32;	
		protected static void renderMapOLD() {
			
			TileMap tmap = tileUtils.getMap();
			
			// The top left of the map should be at:
			int mapX0 = (testCanvas.getWidth()/2) - (tileUtils.getPlayer().getX()*TILE_SIZE);
			int mapY0 = (testCanvas.getHeight()/2) - (tileUtils.getPlayer().getY()*TILE_SIZE);
			
			//System.out.println("X0 "+mapX0);
			//System.out.println("Y0 "+mapY0);
			
			double offsetX, offsetY;
			
			// 1st tile offset X
			double tilesShown = (double)testCanvas.getWidth()/(double)TILE_SIZE/2;
			double tilesX = Math.ceil(tilesShown);
			offsetX = 0-Math.ceil((tilesX - tilesShown)*(double)TILE_SIZE);  
			//System.out.println("[W] "+testCanvas.getWidth()+" : "+tilesShown+" : "+tilesX+" : "+offsetX);
			
			// tile index x
			double tileMinX = (double)tileUtils.getPlayer().getX() - tilesX;
			if (tileMinX < 0) {
				tileMinX = 0;
			}

			// 1st tile offset Y
			tilesShown = (double)testCanvas.getHeight()/(double)TILE_SIZE/2;
			double tilesY = Math.ceil(tilesShown);
			double tileOffsetY = (tilesY - tilesShown);				// in tiles
			offsetY = 0-Math.ceil(tileOffsetY*(double)TILE_SIZE);	// in pixels 
			//System.out.println("[H] "+testCanvas.getHeight()+" : "+tilesShown+" : "+tilesY+" : "+offsetY);
 
			// tile index y
			double tileMinY = (double)tileUtils.getPlayer().getY() - tilesY;
			if (tileMinY < 0) {
				tileMinY = 0;
			}
			
			// if the player is at screenH/2 * screenW/2 then the map needs to be drawn from a point:
			//offsetX = tileUtils.getPlayerX()*TILE_SIZE;
			//offsetY = tileUtils.getPlayerY()*TILE_SIZE;
			
			// Use graphics 2D
			//Graphics2D g2d = (Graphics2D)testCanvas.getGraphics();
			Graphics2D g2d = (Graphics2D)bufstrat.getDrawGraphics();
			g2d.clearRect(0,0,testCanvas.getWidth(),testCanvas.getHeight()); // Clear the canvas from previous image
			
			// Draw map
			g2d.setColor(new Color(32,32,32)); // Use grey color
			
			for (int x=0; x < tilesX*2; x++) {
				for (int y=0; y < tilesY*2; y++) {
					// Draw boxes; this makes a nice TILE_SIZE*TILE_SIZE grid
					g2d.drawRect((int)offsetX+(x*TILE_SIZE),(int)offsetY+(y*TILE_SIZE),TILE_SIZE,TILE_SIZE);
					g2d.drawImage(
							tmap.getTile((int)tileMinX+x, (int)tileMinY+y),
							(int)mapX0+(x*TILE_SIZE),
							(int)mapY0+(y*TILE_SIZE),
							null);
					
					g2d.drawImage(
							tmap.getObject((int)tileMinX+x, (int)tileMinY+y),
							(int)mapX0+(x*TILE_SIZE),
							(int)mapY0+(y*TILE_SIZE),
							null);					
					
					/* Obstruction test *
					if (tmap.getObstruction(x, y)) {
						g2d.setColor(new Color(255,128,128));
						g2d.fillRect((int)mapX0+(x*TILE_SIZE),(int)mapY0+(y*TILE_SIZE),TILE_SIZE,TILE_SIZE);
					}
					//*/
					
				}
			}
			
			// Render the player
			g2d.drawImage(
					tileUtils.getPlayerSprite(),
					(int)Math.ceil(testCanvas.getWidth()/2),
					(int)Math.ceil(testCanvas.getHeight()/2),
					null);
			
			// Render the turn
			g2d.setColor(new Color(0,0,0));
			g2d.fillRect(2, 2, 110, 35);
			g2d.setColor(new Color(255,255,255));
			g2d.drawString("World turn:  "+tileUtils.getTurn(), 5, 15);
			g2d.drawString("Player turn: "+tileUtils.getPlayer().getTurn(), 5, 30);
			
			g2d.dispose();
			bufstrat.show();

		}

	public static void createAndShowGUI() {
		final ImageTest imageTest = new ImageTest();
		
		//TestCanvas testCanvas = imageTest.init();
		testCanvas = imageTest.getCanvas();
		imageTest.setTitle("Image Test"); // Set Title
		imageTest.setSize(new Dimension(640,480)); // Make a generic window
		imageTest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Program will stop if this window is closed
		// Create the image here (could be nearly anywhere you know)	
		//imageTest.imageManager.create(argList); // KAWAII! But... You can't see it. It's the chibi character around the globe on a orange world map background, I use this wallpaper for my laptop.
		// Add canvas
		imageTest.add(testCanvas,BorderLayout.CENTER); // Canvas will fill AS MUCH OF THE WINDOW as it can from the center, according to this layout.
		// Show window
		imageTest.pack(); // Makes the window as small as possible
		imageTest.setLocationRelativeTo(null); // Best if used AFTER pack for a centered window
		imageTest.setVisible(true);
		
		// Supposed to eliminate the flicker, but blatantly doesn't.
		testCanvas.createBufferStrategy(2);
		bufstrat = testCanvas.getBufferStrategy();
		//testCanvas.setIgnoreRepaint(true);
		
	}

	/**
	 * The program begins here.
	 *
	 * @param args  This is unused.
	 */
	public static void main(String[] args)
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
				
				/* Game loop ? */			
            }
        });
	}
}
