package neurogame.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import neurogame.library.Library;

public class PlayerHud {
	private int windowWidth;
	private int windowHeight;

	private Map<String, BufferedImage> sprites;

	private Color healthPaintFull = new Color(113, 188, 120);
	private Color healthPaintDamaged = Color.ORANGE;
	private Color healthPaintNearDeath = Color.RED;

	private BufferedImage MissleIcon;
	Rectangle rect = new Rectangle(0, 0, 100, 50);
	BufferedImage bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
	Graphics2D big;

	int last_x, last_y;

	boolean firstTime = true;
	boolean isDead = false;

	private static final Color BLUE = new Color(100, 151, 255);
	private final Font FONT30;

	Rectangle area;

	public PlayerHud(NeuroFrame frame) {
		sprites = Library.getSprites();

		windowWidth = frame.getWidth();
		windowHeight = frame.getHeight();

		String path = System.getProperty("user.dir");
		path += "/resources/fonts/";

		try {
			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(path
					+ "KarmaticArcade.ttf")));
			System.out.println("Registered Font");
		} catch (IOException | FontFormatException e) {
			System.out.println("Error Loading Font - PlayerHUD.java");
		}

		FONT30 = new Font("Karmatic Arcade", Font.PLAIN, 30);

		MissleIcon = sprites.get("missileIcon");
	}

	public void updateHUD(Graphics2D canvasObjectLayer, NeuroFrame frame,
			int ammo) {

		canvasObjectLayer.setFont(FONT30);
		canvasObjectLayer.setColor(Color.BLACK);

		canvasObjectLayer.setColor(Color.WHITE);
		canvasObjectLayer.drawString("Score: ",
				(int) (windowWidth * 0.5 - 100), (int) (windowHeight * 0.04));

		canvasObjectLayer.setColor(BLUE);
		canvasObjectLayer.drawString("" + frame.getScore(),
				(int) (windowWidth * 0.5 + 100), (int) (windowHeight * 0.04));

		// Ammo
		for (int i = 0; i < ammo; i++) {
			if(i<10){
			canvasObjectLayer.drawImage(MissleIcon, null,
					(int) (windowWidth * 0.8 + MissleIcon.getWidth()
									* (i%10) * 1.1), (int) (windowHeight * 0.015));
			}
			else{
				canvasObjectLayer.drawImage(MissleIcon, null,
						(int) (windowWidth * 0.8 + MissleIcon.getWidth()
										* (i%10) * 1.1), (int) (windowHeight * 0.055));
			}
		}

		//
		drawHealth(canvasObjectLayer, frame);

		// Show game over
		if (isDead) {
			canvasObjectLayer.setFont(new Font("Karmatic Arcade", Font.PLAIN,
					70));
			canvasObjectLayer.setColor(new Color(255, 255, 255));
			canvasObjectLayer
					.drawString("GAME OVER", (int) (windowWidth * 0.5 - 225),
							(int) (windowHeight * 0.5));

			canvasObjectLayer.setFont(new Font("Karmatic Arcade", Font.PLAIN,
					25));
			canvasObjectLayer.setColor(new Color(100, 151, 255));
			canvasObjectLayer
					.drawString("[ Press any Button ]",
							(int) (windowWidth * 0.5 - 125),
							(int) (windowHeight * 0.6));
		}
	}

	public void drawGameOver(boolean dead) {
		isDead = dead;
	}

	/**
	 * Draw the health display.
	 */
	public void drawHealth(Graphics2D canvasObjectLayer, NeuroFrame frame) {
		int health = frame.getHealth();

		// Color outline = Color.GREEN;

		if (health >= 0.9 * Library.HEALTH_MAX) {
			canvasObjectLayer.setPaint(healthPaintFull);
		} else if (health > 0.2 * Library.HEALTH_MAX) {
			canvasObjectLayer.setColor(healthPaintDamaged);
			// outline = Color.ORANGE;
		} else {
			canvasObjectLayer.setColor(healthPaintNearDeath);
			// outline = Color.RED;
		}

		int width = (frame.getHealth() * 300) / Library.HEALTH_MAX;

		canvasObjectLayer.fillRect(5, 5, width, 32);
		// canvasObjectLayer.setPaint(outline);
		canvasObjectLayer.drawRect(5, 5, 300, 32);
	}
}