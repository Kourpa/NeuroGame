package neurogame.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

import neurogame.library.Library;

/**
 * 
 * @author kourpa
 */
public class Oddball {
	private long time;
	private final int numberOfGoodScreens = 50;
	private final int numberOfBadScreens = 200;
	private final ArrayList<Screen> options;
	private Screen currentScreen;
	private BufferedImage badImage, goodImage, waitImage;

	private long startTime, screenTime, waitTime;
	private boolean wait, instructions;

	private JLabel background;

	public Oddball(final NeuroFrame frame, BufferedImage badImage,
			BufferedImage goodImage, BufferedImage waitImage) {

		int width = frame.getWidth();
		int height = frame.getHeight();

		// Frame
		frame.getContentPane().removeAll();
		frame.getContentPane().setLayout(new BorderLayout());

		// Draw the image here
		background = new JLabel(new ImageIcon(badImage));
		background.setBackground(Color.BLACK);
		background.setBounds(0, 0, width, height);
		background.setOpaque(true);

		frame.getContentPane().add(background);

		//
		wait = instructions = false;
		startTime = System.currentTimeMillis();
		screenTime = 400 + Library.RANDOM.nextInt(400);
		waitTime = 400 + Library.RANDOM.nextInt(400);
		time = startTime;
		options = new ArrayList<>();

		this.badImage = badImage;
		this.goodImage = goodImage;
		this.waitImage = waitImage;

		for (int i = 0; i < numberOfGoodScreens; i++) {
			options.add(Screen.GOOD);
		}

		for (int i = 0; i < numberOfBadScreens; i++) {
			options.add(Screen.BAD);
		}

		Collections.shuffle(options);
		instructions = false;

		// Timer
		Timer timer = new Timer(100, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				update();
			}
		});
		timer.start();
	}

	public void update() {
		time = System.currentTimeMillis();

		if (instructions) { // inital screen to explain the test

		} else if (wait) {
			if (time - startTime > waitTime) {
				wait = false;
				currentScreen = options.remove(0);

				startTime = System.currentTimeMillis();
				screenTime = 400 + Library.RANDOM.nextInt(400);
				waitTime = 400 + Library.RANDOM.nextInt(400);
			}
		} else if (time - startTime > screenTime) {
			wait = true;
			currentScreen = Screen.WAIT;
		}
		render();
	}

	public void render() {
		if (currentScreen == null) {
			return;
		}

		switch (currentScreen) {
		case GOOD:
			background.setIcon(new ImageIcon(goodImage));
			break;
		case BAD:
			background.setIcon(new ImageIcon(badImage));
			break;
		case WAIT:
			background.setIcon(null); //new ImageIcon(waitImage)
			break;
		}
	}

	private enum Screen {
		GOOD, BAD, WAIT;
	}
}
