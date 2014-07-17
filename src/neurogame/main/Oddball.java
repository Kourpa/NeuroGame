package neurogame.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

import neurogame.library.Library;

/**
 * @author kourpa
 */
public class Oddball {
	private long time;
	private final int numberOfGoodScreens = 50;
	private final int numberOfBadScreens = 200;
	private int currentNumber;
	private boolean testFinished;
	private boolean showCount;
	private final ArrayList<Screen> options;
	private Screen currentScreen;
	private BufferedImage badImage, goodImage, welcomeImage, instructionImage,finishedImage;

	private long startTime, screenTime, waitTime, InstructionTime;
	private boolean wait, instructions;
	private int currentImageNum = 0;

	private JLabel background;
	private NeuroFrame frame;

	public Oddball(final NeuroFrame frame, BufferedImage badImage,
			BufferedImage goodImage, BufferedImage instructionImage, BufferedImage welcomeImage, BufferedImage countImage) {

		this.frame = frame;
		int width = frame.getWidth();
		int height = frame.getHeight();

		// Frame
		frame.getContentPane().removeAll();
		frame.getContentPane().setLayout(new BorderLayout());
		
		KeyAdapter Keys = new KeyAdapter() {
			public void keyReleased(KeyEvent e) {				
				instructions = false;
				
				if (showCount){
					background.setVisible(false);
					frame.getContentPane().removeAll();
					frame.getContentPane().setLayout(null);
					testFinished = true;
				}
			}
		};
		frame.addKeyListener(Keys);
		frame.requestFocus();

		// Draw the image here
		background = new JLabel(new ImageIcon(badImage));
		background.setBackground(Color.BLACK);
		background.setBounds(0, 0, width, height);
		background.setOpaque(true);

		frame.getContentPane().add(background);

		//
		wait = instructions = false;
		startTime = System.currentTimeMillis();
		screenTime = 800 + Library.RANDOM.nextInt(400);
		waitTime = 1600 + Library.RANDOM.nextInt(400);
		InstructionTime = 2000;
		time = startTime;
		options = new ArrayList<>();

		this.badImage = badImage;
		this.goodImage = goodImage;
		this.welcomeImage = welcomeImage;
		this.instructionImage = instructionImage;
		this.finishedImage = countImage;

		for (int i = 0; i < numberOfGoodScreens; i++) {
			options.add(Screen.GOOD);
		}
		for (int i = 0; i < numberOfBadScreens; i++) {
			options.add(Screen.BAD);
		}

		Collections.shuffle(options);
		instructions = true;
		showCount = false;
		testFinished = false;
		currentNumber = 0;

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
		float randomFloat;
		double probabilityOfTarget;

		time = System.currentTimeMillis();

		if (instructions) {
			currentScreen = Screen.INSTRUCTIONS;
			if (time - startTime > InstructionTime) {
				currentScreen = Screen.WELCOME;
			}
			else if(time - startTime > InstructionTime*2){
				currentScreen = Screen.INSTRUCTIONS;
			}
		} else if(showCount){
			currentScreen = Screen.FINISHED;
		} else if (wait) {
			if (time - startTime > waitTime) {

				probabilityOfTarget = currentImageNum / 5.0; // Max of 6

				// Target or False screen 
				randomFloat = Library.RANDOM.nextFloat();
				if (randomFloat < probabilityOfTarget) {
					currentImageNum = 0;
					currentScreen = Screen.BAD;
				} else {
					currentScreen = Screen.GOOD;
					currentImageNum += 1;
					currentNumber++;
				}

				wait = false;
				startTime = System.currentTimeMillis();
				
				// Finished the test
				if(currentNumber > numberOfGoodScreens){
					showCount = true;
				}
			}
		} else if (time - startTime > screenTime) {
			wait = true;
			currentScreen = Screen.WAIT;
		}
		
		render();
	}
	
	public boolean isFinished(){
		return testFinished;
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
			background.setIcon(null); // new ImageIcon(waitImage)
			break;
		case WELCOME:
			background.setIcon(new ImageIcon(welcomeImage));
			break;
		case INSTRUCTIONS:
			background.setIcon(new ImageIcon(instructionImage));
			break;
		case FINISHED:
			background.setIcon(new ImageIcon(finishedImage));
			break;
		}
	}

	private enum Screen {
		GOOD, BAD, WAIT, WELCOME, INSTRUCTIONS,FINISHED;
	}
}
