/**
 * Full-Screen-Exclusive Java Helper library.
 * 
 * Author: Ramon A. Lovato, ramonalovato.com
 */

package neurogame.library;

import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

/**
 * A static full-screen-exclusive mode helper library.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group James Green
 * @group Marcos Lemus
 * @group Mario LoPrinzi
 */
public class FullScreenHelper {
	/**
	 * Enter full-screen mode. If full-screen exclusive mode isn't supported,
	 * uses a maximized window instead.
	 * 
	 * @param frame JFrame to go full-screen.
	 * @param exclusive If true, use full-screen exclusive; if false, use a
	 *                           maximized window.
	 */
	public static void enterFullScreen(JFrame frame, boolean exclusive) {
        // Get the graphics device information.
        GraphicsEnvironment environment = 
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphics = environment.getDefaultScreenDevice();
        
        if (frame.isDisplayable()) {
        	frame.setVisible(false);
			frame.dispose();
        }
        frame.setUndecorated(true);
        
        // Go full screen, if supported and selected.
        if (graphics.isFullScreenSupported() && exclusive) {
            try {
                graphics.setFullScreenWindow(frame);
                
                // This double-switching of setVisible is to fix a bug with 
                // full-screen-exclusive mode on OS X. Versions 10.8 and later
                // don't send keyboard events properly without it.
                if (System.getProperty("os.name").contains("OS X")) {
                	frame.setVisible(false);
                }
            } catch (HeadlessException ex) {
                System.err.println("Error: primary display not set or found. "
                    + "Your experience of life may be suboptimal.");
                ex.printStackTrace();
            }
        } else {
            // If full-screen-exclusive mode isn't supported, switch to
            // maximized window mode.
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
        
        if (!frame.isVisible()) {
        	frame.setVisible(true);
        }
	}
	
	/**
	 * Leave full-screen mode.
	 * 
	 * @param frame JFrame to modify for displaying non-full-screen.
	 */
	public static void exitFullScreen(JFrame frame) {
        // Get the graphics device information.
        GraphicsEnvironment environment = 
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphics = environment.getDefaultScreenDevice();
        
        if (graphics.isFullScreenSupported()) {
        	try {
        		graphics.setFullScreenWindow(null);
        	} catch (HeadlessException ex) {
        		// Do nothing.
        	}
        } else {
        	frame.setExtendedState(Frame.NORMAL);
        }
        
        if (frame.isDisplayable()) {
			frame.setVisible(false);
			frame.dispose();
		}
        frame.setUndecorated(false);
        
        if (!frame.isVisible()) {
        	frame.setVisible(true);
        }
	}
	
	/**
     * Hide the mouse cursor by changing the image to an empty, transparent
     * icon.
     * 
     * @param frame JFrame whose mouse cursor should be modified.
     */
    public static void hideCursor(JFrame frame) {
    	frame.getContentPane().setCursor(
    			Toolkit.getDefaultToolkit().createCustomCursor(
    					new BufferedImage(
    							16, 16, BufferedImage.TYPE_INT_ARGB),
    					new Point(0, 0), "empty cursor"
		));
    }
	
}

