package neurogame.main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

import neurogame.gameplay.PowerUp;
import neurogame.library.Library;
import neurogame.library.SpriteMap;

public class PlayerHud {
	private int windowWidth;
	private int windowHeight;	

	private SpriteMap sprites;
	private GradientPaint healthPaintFull = new GradientPaint(0, 0,
			Color.GREEN, 0, 20, Color.BLACK, true);
	private GradientPaint healthPaintDamaged = new GradientPaint(0, 0,
			Color.ORANGE, 0, 20, Color.BLACK, true);
	private GradientPaint healthPaintNearDeath = new GradientPaint(0, 0,
			Color.RED, 0, 20, Color.BLACK, true);

	public PlayerHud(NeuroFrame frame) {
		windowWidth = frame.getWidth();
		windowHeight = frame.getHeight();
		
		String path = System.getProperty("user.dir");
		path += "/resources/fonts/";

		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(path+"KarmaticArcade.ttf")));
			System.out.println("Registered Font");
		} catch (IOException | FontFormatException e) {
			System.out.println("Error Loading Font - PlayerHUD.java");
		}
	}

	public void updateHUD(Graphics2D canvasObjectLayer, NeuroFrame frame) {
		canvasObjectLayer.setFont(new Font("Karmatic Arcade", Font.PLAIN, 30));
		canvasObjectLayer.setColor(Color.WHITE);
		canvasObjectLayer.drawString("Score: ",
				(int) (windowWidth * 0.5 - 100), (int) (windowHeight * 0.05));

		canvasObjectLayer.setColor(new Color(100, 151, 255));
		canvasObjectLayer.drawString("" + frame.getScore(),
				(int) (windowWidth * 0.5 + 100), (int) (windowHeight * 0.05));
		
		//
		drawHealth(canvasObjectLayer, frame);
	}

	/**
	 * Draw the health display.
	 */
	public void drawHealth(Graphics2D canvasObjectLayer, NeuroFrame frame) {
		int health = frame.getHealth();
		Color outline = Color.GREEN;
		if (health >= 0.9 * Library.HEALTH_MAX) {
			canvasObjectLayer.setPaint(healthPaintFull);
		} else if (health > 0.2 * Library.HEALTH_MAX) {
			canvasObjectLayer.setPaint(healthPaintDamaged);
			outline = Color.ORANGE;
		} else {
			canvasObjectLayer.setPaint(healthPaintNearDeath);
			outline = Color.RED;
		}

		int width = (frame.getHealth() * 300) / Library.HEALTH_MAX;
		canvasObjectLayer.fillRect(5, 5, width, 32);
		canvasObjectLayer.setPaint(outline);
		canvasObjectLayer.drawRect(5, 5, 300, 32);

	}

	/**
	 * Draw PowerUp icon.
	 */
	private void drawPowerUp(Graphics2D canvasObjectLayer, NeuroFrame frame) {

		// PowerUp p = player.getPowerUp();
		// if(p != null){
		// p.render(graphics);
		// }

		PowerUp powerUp = frame.getPowerUp();

		canvasObjectLayer.drawImage(sprites.get("powerupBackground"),
				windowWidth - 101, 5, 96, 96, null);
		if (powerUp != null) {
			if (powerUp.isInUse()) {
				// Create an alpha composition using the PowerUp's alpha.
				AlphaComposite ac = AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, powerUp.getAlpha());
				canvasObjectLayer.setComposite(ac);
				canvasObjectLayer.drawImage(powerUp.getUIImage(),
						windowWidth - 101, 5, 96, 96, null);
				// Restore the default alpha composition.
				canvasObjectLayer.setComposite(AlphaComposite
						.getInstance(AlphaComposite.SRC_OVER));
			} else {
				canvasObjectLayer.drawImage(powerUp.getUIImage(),
						windowWidth - 101, 5, 96, 96, null);
			}
			String flavorText = powerUp.getFlavorText();
			canvasObjectLayer.setColor(Color.WHITE);
			canvasObjectLayer.setFont(new Font("Serif", Font.PLAIN, 14));
			canvasObjectLayer.drawString(flavorText, windowWidth - 101
					- (flavorText.length() * 7), 55);
		}
	}
}