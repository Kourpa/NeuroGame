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

/**
 * An interface for drawable elements.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 */
public interface Drawable {
	/**
	 * The draw method guarantees that an object is capable of drawing itself.
	 * This allows DrawingEngine to call a Drawable's draw method with its
	 * own Graphics object and have the Drawable object draw itself onto the
	 * engine's image.
	 * 
	 * Important note: because all internal coordinates are measured in terms
	 * of u (double), all coordinates and dimensions must be converted to
	 * screen pixel coordinates by multiplying by Library.uValue(). A
	 * convenience method, Library.worldToScreen(float) is provided for this
	 * purpose. More complex calculations will likely want to retrieve 
	 * uValue() directly.
	 * 
	 * @param g Graphics2D object to use for drawing.
	 */
	public void draw(Graphics2D g);
	
}
