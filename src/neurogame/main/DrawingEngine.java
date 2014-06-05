/**
 * NeuroGame.
 * CS 351, Project 3
 * 
 * Team members:
 * Ramon A. Lovato
 * Danny Gomez
 * Marcos Lemus
 */

package neurogame.main;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;



import neurogame.gameplay.Player;
import neurogame.gameplay.PowerUp;
import neurogame.level.World;
import neurogame.library.Library;

/**
 * The drawing engine for NeuroGame. Each frame, the engine produces a buffered
 * image with all of the necessary graphics on it. NeuroFrame then takes the
 * image and draws it onto the screen.
 * 
 * @author Marcos Lemus
 * @author Ramon A. Lovato
 * @team Danny Gomez
 */
public class DrawingEngine {
	public static final double ZOOM_MIN = 0.5;
	public static final double ZOOM_MAX = 2.0;
	public static final double ZOOM_DEFAULT = 1.0;
	
	private BufferedImage image;
	private Graphics2D graphics;
	private World world;
	private Player player;
	private double screenWidth;
	private double screenHeight;
	private double clipWidth;
	private double clipHeight;
	private double worldWidth;
	private double worldHeight;
	// The x and y fields are used to specify where the focal point of the
	// subimage should be (in u). Typically, this will be the center of the
	// player, but it can be moved for camera effects as needed.
	private double clipX;
	private double clipY;
	// The zoom value is applied when retrieving the subimage. A value of 1
	// is no zoom. Fractional values between ZOOM_MIN and 1 zoom the camera in.
	// Values greater than 1 zoom the camera out.
	private double zoom;
	// If true, the screen tries to stay centered on the player (excepting when
	// at the edge of the raster. If false, the screen stays fixed and the
	// player can move freely within it.
	private boolean centered;
	
	/**
	 * Instantiates a new DrawingEngine with a reference to its containing
	 * frame.
	 * 
	 * @param frame NeuroFrame that will be displaying this engine.
	 * @param world World instance responsible for the game's level.
	 */
	public DrawingEngine(NeuroFrame frame, World world) {
		screenWidth = Library.HORIZONTAL_MAX;
		screenHeight = Library.VERTICAL_MAX;
		clipWidth = screenWidth;
		clipHeight = screenHeight;
		this.world = world;
		player = world.getPlayer();
		worldWidth = world.getWidth();
		worldHeight = world.getHeight();
		image = new BufferedImage(Library.worldToScreen(worldWidth),
				Library.worldToScreen(worldHeight),
				BufferedImage.TYPE_INT_ARGB);
		graphics = image.createGraphics();
		zoom = ZOOM_DEFAULT;
		centered = false;
		setClip(new Point2D.Double(screenWidth/2, screenHeight/2));
	}
	
	/**
	 * Draw a single Drawable element onto the master image.
	 * 
	 * @param d Drawable element to draw onto the master image.
	 */
	public void draw(Drawable d) {
		d.draw(graphics);
	}
	
	/**
	 * Sequentially draw a list of Drawable elements onto the master image.
	 * 
	 * @param l List of Drawable elements to draw onto the master image.
	 */
	public void drawAll(List<Drawable> l) {
		for (Drawable d : l) {
			d.draw(graphics);
		}
	}
	
	/**
	 * Draw the World to the image using an AffineTransform.
	 */
	public void drawWorld() {
		draw(world);
	}
	
	/**
	 * Draw the Player.
	 */
	public void drawPlayer() {
//		draw(player);
	}
	
	/**
	 * Draw the PowerUp.
	 */
	public void drawPowerUp() {
		PowerUp p = player.getPowerUp();
		if (p != null) {
//			draw(p);
		}
	}
	
	/**
	 * Clear the image by painting it black.
	 */
	public void clear() {
		graphics.clearRect(0, 0, image.getWidth(), image.getHeight());
	}
	
	/**
	 * Sets the clipping area on which the subimage should be focused, in u
	 * world coordinates, which get converted to pixel coordinates at clip
	 * time.
	 * 
	 * @param center Point2D position (in u world coordinates) on which
	 *            the subimage should be focused. 
	 */
	public void setClip(Point2D center) {
		if (zoom != ZOOM_DEFAULT || centered) {
			double newWorldWidth = world.getWidth();
			double newWorldHeight = world.getHeight();
			if (newWorldWidth != worldWidth || newWorldHeight != worldHeight && false) {
				worldWidth = newWorldWidth;
				worldHeight = newWorldHeight;
				image = new BufferedImage(Library.worldToScreen(worldWidth),
						Library.worldToScreen(worldHeight),
						BufferedImage.TYPE_INT_ARGB);
				graphics = image.createGraphics();
			}
			double deltaX = world.getDeltaX();
			double deltaY = 0;
			// Get where the center of the display should be with respect to
			// the world.
			double screenX = center.getX() - deltaX;
			double screenY = center.getY() - deltaY;
			// Apply zoom. If zoom is > 1, the size of the subimage increases,
			// capturing more of the level. When the subimage is then drawn to
			// the display, the larger image is scaled down to fit on the frame.
			// If zoom is < 1, the size of the subimage decreases, effectively
			// zooming in the same way.
			clipHeight = (screenHeight * zoom > worldHeight ?
					screenHeight : screenHeight * zoom);
//			clipWidth = clipHeight * Library.ASPECT_RATIO;
			// Figure out where the top-left corner of the screen should be.
			clipX = screenX - (clipWidth / 2);
			clipY = screenY - (clipHeight / 2);
			
			// If the frame would be outside the permissible area, lock it to
			// the nearest edge. When the centered flag is enabled, this tries
			// to keep the screen centered on the player (except at the edge of
			// the raster). When the flag is disabled, the player moves freely
			// within the frame, but the frame doesn't adjust to the player's
			// movement.
			double adjustedWidth = (centered ? worldWidth : screenWidth);
			double adjustedHeight = (centered ? worldHeight : screenHeight);
			if (clipX < 0 || clipX + clipWidth > adjustedWidth) {
				clipX = 0;
			} else if (clipX + clipWidth > adjustedWidth) {
				clipX = adjustedWidth - clipWidth;
			}
			if (clipY < 0 || clipY + clipHeight > adjustedHeight) {
				clipY = 0;
			} else if (clipY + clipHeight > adjustedHeight) {
				clipY = adjustedHeight - clipHeight;
			}
		}
	}
	
	/**
	 * Setter for centered, determining if the screen follows the player or
	 * moves independently.
	 * 
	 * @param centered If true, the frame tries to keep the player centered,
	 *            unless at the edge of the raster. If false, the screen
	 *            scrolls normally, and the player moves freely within it.
	 */
	public void setCentered(boolean centered) {
		this.centered = centered;
	}
	
	/**
	 * Toggle centered-screen mode.
	 */
	public void toggleCentered() {
		centered = !centered;
	}
	
	/**
	 * Setter for the zoom amount (default 1). Values < 1 zoom in; values > 1
	 * zoom out.
	 * 
	 * @param zoom Zoom as a double (default 1.0). Values not in the range
	 *            [ZOOM_MIN, ZOOM_MAX] are ignored.
	 */
	public void zoom(double zoom) {
		if (zoom >= ZOOM_MIN && zoom <= ZOOM_MAX &&
				zoom * screenWidth <= worldWidth &&
				zoom * screenHeight <= worldHeight) {
			this.zoom = zoom;
		}
	}
	
	/**
	 * Reset the zoom.
	 */
	public void resetZoom() {
		zoom(ZOOM_DEFAULT);
	}
	
	/**
	 * Getter for the current zoom amount.
	 * 
	 * @return The current zoom amount as a double.
	 */
	public double getZoom() {
		return zoom;
	}
	
	/**
	 * Get the graphics object, which can be used to draw directly to this
	 * engine's image. Drawing to the masterGraphics draws to the image
	 * for this engine, not the display image.
	 * 
	 * @return Graphics2D object used to draw onto this engine's image.
	 */
	public Graphics2D graphics() {
		return graphics;
	}
	
	/**
	 * Get the subimage suitable for drawing to the frame.
	 * 
	 * @return BufferedImage containing the image.
	 */
	public BufferedImage image() {
		return image.getSubimage(Library.worldToScreen(clipX),
				Library.worldToScreen(clipY), Library.worldToScreen(clipWidth),
				Library.worldToScreen(clipHeight));
	}
	
}
