
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
 */
public class TileGame extends JFrame
		implements KeyListener
{
	private 				Images          imageManager;
	private static  MainCanvas      mainCanvas;
	private static  BufferStrategy  bufstrat;
	private static 	TileUtils       tileUtils;
	
	public TileGame() {
		imageManager = new Images();
		mainCanvas = new MainCanvas(imageManager);
		mainCanvas.addKeyListener(this);
		tileUtils = new TileUtils();

		// Forces the canvas to have focus onload, which means you can get stuck
		// in right away with the arrow keys
		addWindowListener( new WindowAdapter() {
			public void windowOpened( WindowEvent e ){
				mainCanvas.requestFocus();
			}
		} );

	}

	/**
	 *
	 */	
	public MainCanvas getCanvas(){
		return mainCanvas;
	}

	/**
	 * This is the canvas on which the image is drawn.
	 */
	public static class MainCanvas 
		extends Canvas
	{
		private Images imageManager;
		
		/**
		 * Creates a new test canvas.
		 */
		public MainCanvas(Images imageManager)
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
			Graphics2D g2d = (Graphics2D)bufstrat.getDrawGraphics();
			g2d.clearRect(0,0,getWidth(),getHeight()); // Clear the canvas from previous image

			// Draw something
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

			/*************************************************
			 * Ideally I want to use this method to handle
			 * keypresses, but it doesn't work on OSX!
			 * So, we fall back to getKeyCode().

			String cmd = KeyEvent.getKeyText(e.getKeyCode());
			if ( cmd.equals("Up") ) {}

			 ************************************************/

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

		/* This is a rewrite of the renderMap method which considers only whole squares.
		 * @author Owan
		 */
		protected static void renderMap(){
			renderMap(32);	
		}

		protected static void renderMap(int tileSize){
		
			TileMap tmap = tileUtils.getMap();
			// The player character always appears in the middle of the canvas and his position never varies.
			// If we declare the top left corner (0,0) to be the origin of a square, the player character's
			// square must be determined.
			
			// Note that if there size of the canvas is not an exact multiple of tileSize, the part visible
			// squares around the right and/or bottom edges of the canvas are counted too.
			int totalSquaresX = (int)Math.ceil(mainCanvas.getWidth()/tileSize);
			int totalSquaresY = (int)Math.ceil(mainCanvas.getHeight()/tileSize);

			// A decision must now be made about which square the player appears in.  If the total is an odd
			// number, the decision is simple as there's an actual centre.  If the total is an even number
			// the decision is more complicated. I have chosen here to always round down as it will improve
			// the visual effect of resizing the screen due to the possibility of partial squares on the right
			// and/or buttom edges of the canvas.
			int playerX = (int)Math.floor(totalSquaresX/2);
			int playerY = (int)Math.floor(totalSquaresY/2);

			// Determine the tile map's offset.  This again is measured in squares.
			int mapOriginX = tileUtils.getPlayer().getX() - playerX;
			int mapOriginY = tileUtils.getPlayer().getY() - playerY;

			// Clear the map and then draw the gridlines onto the screen.
			Graphics2D g2d = (Graphics2D)bufstrat.getDrawGraphics();
			g2d.clearRect(0,0,mainCanvas.getWidth(),mainCanvas.getHeight());
			g2d.setColor(new Color(32,32,32));
			for (int x=0;x<=totalSquaresX;x++){
				for (int y=0;y<=totalSquaresY;y++){
				  g2d.drawRect(x*tileSize,y*tileSize,tileSize,tileSize);
				}
			}
			
			// Draw the map itself.
			for (int x=0;x<=totalSquaresX;x++){
				for (int y=0;y<=totalSquaresY;y++){
					g2d.drawImage(tmap.getTile(mapOriginX+x,mapOriginY+y),x*tileSize,y*tileSize,tileSize,tileSize,null,null);
				}	
			}					
			
			// Draw the items which appear on the map.
			for (int x=0;x<=totalSquaresX;x++){
				for (int y=0;y<=totalSquaresY;y++){
					g2d.drawImage(tmap.getObject(mapOriginX+x,mapOriginY+y),x*tileSize,y*tileSize,tileSize,tileSize,null,null);
				}	
			}					
		
			// Draw the player character.
			g2d.drawImage(tileUtils.getPlayerSprite(),playerX*tileSize,playerY*tileSize,tileSize,tileSize,null,null);		
			
			// Display the turn information.
			g2d.setColor(new Color(0,0,0));
			g2d.fillRect(2,2,110,35);
			g2d.setColor(new Color(255,255,255));
			g2d.drawString("World turn:  "+tileUtils.getTurn(),5,15);
			g2d.drawString("Player turn: "+tileUtils.getPlayer().getTurn(),5,30);
			
			//Now we display the image and flush the buffer
			g2d.dispose();
			bufstrat.show();		
		}

	public static void createAndShowGUI() {
		final TileGame tileGame = new TileGame();
		
		mainCanvas = tileGame.getCanvas();
		tileGame.setTitle("Image Test");
		tileGame.setSize(new Dimension(640,480));
		tileGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add canvas
		tileGame.add(mainCanvas,BorderLayout.CENTER); // Canvas will fill AS MUCH OF THE WINDOW as it can from the center, according to this layout.
		
		// Show window
		tileGame.pack(); // Makes the window as small as possible
		tileGame.setLocationRelativeTo(null); // Best if used AFTER pack for a centered window
		tileGame.setVisible(true);
		
		// Double buffering stops the flickering
		mainCanvas.createBufferStrategy(2);
		bufstrat = mainCanvas.getBufferStrategy();		
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
				
								/* Game loop goes here. */			
            }
        });
	}
}
