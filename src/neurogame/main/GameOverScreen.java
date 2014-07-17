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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import neurogame.library.Library;
import neurogame.library.User;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

/**
 * The title screen for NeuroGame.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 * @team Martin Lidy
 */
public class GameOverScreen {
	private Image profileBackground, startButtonPlain, startButtonSelected;

	private Image exitButtonPlain, exitButtonSelected, restartButtonSelected,
			restartButtonPlain;

	public boolean IsExiting, IsStarting, IsOption, IsRestarting;
	public int selectedJoystick;
	public User selectedUser;
	private KeyAdapter Keys;

	private int currentButton;
	private boolean MovingDown, MovingUp, ButtonPressed;
	private ArrayList<MenuButtons> MenuButtons = new ArrayList<MenuButtons>();

	private static MenuButtons exitButton, restartButton, startButton;
	private Map<String, BufferedImage> sprites;

	private int width;
	private int height;

	/**
	 * Instantiate a new TitleScreen.
	 * 
	 * @param frame
	 *            NeuroFrame to contain this TitleScreen.
	 */
	public GameOverScreen(final NeuroFrame frame, User newUser) {

		this.selectedUser = newUser;

		// For Polling
		IsExiting = false;
		IsStarting = false;

		width = frame.getWidth();
		height = frame.getHeight();
		sprites = Library.getSprites();

		// Get the images.
		profileBackground = sprites.get("highscoreBackground");

		startButtonPlain = sprites.get("mainMenuButtonPlain");
		startButtonSelected = sprites.get("mainMenuButtonSelected");

		exitButtonPlain = sprites.get("exitButtonPlain");
		exitButtonSelected = sprites.get("exitButtonSelected");

		restartButtonPlain = sprites.get("restartButtonPlain");
		restartButtonSelected = sprites.get("restartButtonSelected");

		// New UI
		CreateGameOverScreen(frame);

		//
		frame.getRootPane().addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				width = frame.getWidth();
				height = frame.getHeight();
			}
		});
	}

	private void CreateGameOverScreen(final NeuroFrame frame){
		frame.getContentPane().setLayout(new BorderLayout());

		// Main pane for the UI
		final JLayeredPane lpane = new JLayeredPane();
		lpane.setBackground(new Color(17, 17, 17, 255));

		// Background
		final JLabel background = new JLabel(new ImageIcon(profileBackground));
		JLabel img = new JLabel("");
		background.add(img);

		// High Score Panels
		final JPanel highscores = new JPanel();
		highscores.setBackground(Color.BLACK);
		highscores.setLayout(new BoxLayout(highscores, 1));

		final JPanel besthighscores = new JPanel();
		besthighscores.setBackground(Color.BLACK);
		besthighscores.setLayout(new BoxLayout(besthighscores, 1));

		// Grab the highscore arrays
		Object[] data = frame.getUser().getHighScores(5);
		String[] highscoresPersonal = new String[data.length];

		// Parse the highscore bits: YYMMDD + Highscore
		for (int i = 0; i < data.length; i++) {
			highscoresPersonal[i] = " "
					+ data[i].toString().substring(0, 2)
					+ "-"
					+ data[i].toString().substring(2, 4)
					+ "-"
					+ data[i].toString().substring(4, 6)
					+ ":       "
					+ data[i].toString().substring(6,
							data[i].toString().length());
		}

		// Personal best highscores JList
		JList<String> personalJList = new JList<String>(highscoresPersonal);
		personalJList.setBackground(Color.getColor("TRANSLUCENT"));
		personalJList.setOpaque(false);

		personalJList.setForeground(new Color(110, 170, 255));
		personalJList.setFont(new Font("Karmatic Arcade", Font.PLAIN, 20));

		// Global best highscores JList
		JList<String> globalJList = new JList<String>(highscoresPersonal);
		globalJList.setBackground(Color.getColor("TRANSLUCENT"));
		globalJList.setOpaque(false);
		globalJList.setForeground(new Color(110, 170, 255));
		globalJList.setFont(new Font("Karmatic Arcade", Font.PLAIN, 20));

		highscores.add(personalJList);
		besthighscores.add(globalJList);

		// Menu Buttons
		final JPanel Buttons = new JPanel();
		Buttons.setBackground(Color.BLACK);
		Buttons.setLayout(new BoxLayout(Buttons, 1));

		// Back to Main menu Button
		startButton = new MenuButtons(startButtonPlain, startButtonSelected);
		
		// RestartButton
		restartButton = new MenuButtons(restartButtonPlain,
				restartButtonSelected);
		restartButton.b.requestFocus();
		MenuButtons.add(restartButton);
		restartButton.b.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				background.setVisible(false);
				lpane.setVisible(false);
				frame.getContentPane().setLayout(new GridLayout(1, 1));
				frame.getContentPane().remove(lpane);
				frame.removeKeyListener(Keys);
				IsRestarting = true;
			}
		});



		// ExitButton - NOT USED ANYMORE
		exitButton = new MenuButtons(exitButtonPlain, exitButtonSelected);
		MenuButtons.add(0, exitButton);
		exitButton.b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				background.setVisible(false);
				lpane.setVisible(false);
				frame.getContentPane().setLayout(new GridLayout(1, 1));
				frame.getContentPane().remove(lpane);
				frame.removeKeyListener(Keys);
				IsStarting = true;
			}
		});

		// Keyboard/Joystick support
		startButton.b.addKeyListener(Keys);
		restartButton.b.addKeyListener(Keys);
		exitButton.b.addKeyListener(Keys);
		startButton.b.requestFocus();
		frame.addKeyListener(Keys);

		//
		// Buttons.add(startButton.b);
		Buttons.add(restartButton.b);
		Buttons.add(exitButton.b);

		Buttons.setBounds(width / 2 - 150, (int) (height * 0.7), 300, 180);
		Buttons.setOpaque(false);
		Buttons.setBackground(Color.getColor("TRANSLUCENT"));

		highscores.setBounds(width / 2 - 420, (int) (height * 0.45), 300, 150);
		highscores.setOpaque(false);
		highscores.setBackground(Color.getColor("TRANSLUCENT"));

		besthighscores.setBounds((int) (width * 0.52), (int) (height * 0.45),
				300, 150);
		besthighscores.setOpaque(false);
		besthighscores.setBackground(Color.getColor("TRANSLUCENT"));

		background.setBounds(0, 0, width, height);
		background.setOpaque(false);
		background.setBackground(Color.getColor("TRANSLUCENT"));

		lpane.setBounds(0, 0, 600, 400);
		lpane.add(background, 0, 0);
		lpane.add(Buttons, 1, 0);
		lpane.add(highscores, 2, 0);
		lpane.add(besthighscores, 3, 0);

		frame.getContentPane().add(lpane);
		frame.setVisible(true);
	}

	
	/* Menu Button Methods */
	/**
	 * Called from the TitleUpdate() in gamecontroller To select the buttons
	 * with the joystick
	 */
	public void updateJoystick(Controller joystick, int JOYSTICK_X,
			int JOYSTICK_Y) {
		float Y;
		boolean ButtonCheck = false;
		
		try{
			joystick.poll();
		}
		catch(Exception e){}

		// X = joystick.getAxisValue(JOYSTICK_X);
		Y = joystick.getAxisValue(JOYSTICK_Y);
		
		System.out.println(Math.abs(Y));

		if (Math.abs(Y) > 0.5) {
			System.out.println(""+currentButton);
			if (Y < 0 && MovingDown == false) {
				MovingDown = true;
				MoveUp();
			} else if (Y > 0 && MovingUp == false) {
				MovingUp = true;
				MoveDown();
			}
		} else {
			MovingUp = false;
			MovingDown = false;
		}

		for (int i = 0; i < 5; i++) {
			if (joystick.isButtonPressed(i)) {
				ButtonCheck = true;
			}
		}
		
		if((ButtonCheck == false) && (ButtonPressed == true)){
			UseButtons();
			ButtonPressed = false;
		}
		else if(ButtonCheck == true){
			ButtonPressed = true;
		}
	}

	/**
	 * Move to the button bellow with a joystick
	 */
	private void MoveDown() {
		currentButton += 1;
		if (currentButton >= MenuButtons.size()) {
			currentButton = 0;
		}
		updateButtons();
	}

	/**
	 * Move to the button above
	 */
	private void MoveUp() {
		currentButton += -1;

		if (currentButton < 0) {
			currentButton = MenuButtons.size()-1;
		}
		updateButtons();
	}

	/**
	 * Update the button focus when moving up/down with joystick
	 */
	private void updateButtons() {
		for (int i = 0; i < MenuButtons.size(); i++) {
			if (i == currentButton) {
				MenuButtons.get(i).setSelected(true);
				MenuButtons.get(i).b.requestFocus();
			} else {
				MenuButtons.get(i).setSelected(false);
			}
		}
	}

	/**
	 * Click a button using the joystick
	 */
	private void UseButtons() {
		for (int i = 0; i < MenuButtons.size(); i++) {
			if (i == currentButton) {
				MenuButtons.get(i).b.doClick();
				break;
			}
		}
	}
}
