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
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;

import neurogame.library.Library;
import neurogame.library.User;

import org.lwjgl.input.Controller;

/**
 * The title screen for NeuroGame.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 * @team Martin Lidy
 */
public class GameOverScreen implements ActionListener, KeyListener {
	private Image profileBackground;

	public boolean IsExiting, IsStarting, IsOption, IsRestarting;
	public int selectedJoystick;
	public User selectedUser;
	private KeyAdapter Keys2;

	private int currentButton;
	private boolean MovingDown, MovingUp, ButtonPressed;
	private ArrayList<MenuButton> MenuButtons = new ArrayList<MenuButton>();

	private static MenuButton exitButton, restartButton, startButton;
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

		//
		width = frame.getWidth();
		height = frame.getHeight();
		sprites = Library.getSprites();

		// Get the images.
		profileBackground = sprites.get("profileBackground");

		// KeyListener for using keyboard to select
		/*Keys2 = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					MoveUp();
					break;
				case KeyEvent.VK_DOWN:
					MoveDown();
					break;
				case KeyEvent.VK_LEFT:
					MoveDown();
					break;
				case KeyEvent.VK_RIGHT:
					MoveUp();
					break;
				case KeyEvent.VK_ENTER:
					UseButtons();
				default:
					break;
				}
			}
		};*/

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

	private void CreateGameOverScreen(final NeuroFrame frame) {
		frame.getContentPane().setLayout(new BorderLayout());

		// Main pane for the UI
		final JLayeredPane lpane = new JLayeredPane();
		lpane.setLayout(null);
		lpane.setBackground(new Color(17, 17, 17, 255));

		// Background
		final JLabel background = new JLabel(new ImageIcon(profileBackground));

		// Grab the highscore arrays
		Object[] data = frame.getUser().getHighScores(5);
		String[] highscoresPersonal = new String[data.length];

		// Parse the highscore bits: YYMMDD + Highscore
		for (int i = 0; i < data.length; i++) {
			highscoresPersonal[i] = " "
					+ data[i].toString().substring(2, 4)
					+ "-"
					+ data[i].toString().substring(4, 6)
					+ "-"
					+ data[i].toString().substring(0, 2)
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

		Object[] data2 = Library.getBestHighScores(5);
		String[] highscoresGlobal = new String[data2.length];

		// Parse the highscore bits: YYMMDD + Highscore
		for (int i = 0; i < data2.length; i++) {
			highscoresGlobal[i] = " "
					+ data2[i].toString().substring(2, 4)
					+ "-"
					+ data2[i].toString().substring(4, 6)
					+ "-"
					+ data2[i].toString().substring(0, 2)
					+ ":       "
					+ data2[i].toString().substring(6,
							data2[i].toString().length());
		}

		// Global best highscores JList
		JList<String> globalJList = new JList<String>(highscoresGlobal);
		globalJList.setBackground(Color.getColor("TRANSLUCENT"));
		globalJList.setOpaque(false);
		globalJList.setForeground(new Color(110, 170, 255));
		globalJList.setFont(new Font("Karmatic Arcade", Font.PLAIN, 20));
		
		// Create the fonts
		Font FONT30 = new Font("Karmatic Arcade", Font.PLAIN, 20);
		Font FONT_LARGE = new Font("Karmatic Arcade", Font.PLAIN, 40);

		// High Score Panels
		final JPanel highscores = new JPanel();
		highscores.setBackground(Color.BLACK);
		highscores.setLayout(new BoxLayout(highscores, BoxLayout.Y_AXIS));

		final JPanel besthighscores = new JPanel();
		besthighscores.setBackground(Color.BLACK);
		besthighscores
				.setLayout(new BoxLayout(besthighscores, BoxLayout.Y_AXIS));
		
		// Personal best scores message
		JLabel personalMessage = new JLabel("Personal Best: ");
		personalMessage.setFont(FONT30);
		personalMessage.setForeground(Color.white);
		
		highscores.add(personalMessage);
		highscores.add(personalJList);

		// All time best scores message
		JLabel bestMessage = new JLabel("All Time Best: ");
		bestMessage.setFont(FONT30);
		bestMessage.setForeground(Color.white);
		
		besthighscores.add(bestMessage);
		besthighscores.add(globalJList);

		// RestartButton
		restartButton = new MenuButton(" Restart", 22);// restartButtonPlain,restartButtonSelected);
		MenuButtons.add(restartButton);
		restartButton.b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				background.setVisible(false);
				lpane.setVisible(false);
				frame.getContentPane().setLayout(new GridLayout(1, 1));
				frame.getContentPane().remove(lpane);
				frame.removeKeyListener(Keys2);
				IsRestarting = true;
			}
		});

		// MainMenu
		exitButton = new MenuButton("Main Menu", 22);
		MenuButtons.add(0, exitButton);
		exitButton.b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				background.setVisible(false);
				lpane.setVisible(false);
				frame.getContentPane().setLayout(new GridLayout(1, 1));
				frame.getContentPane().remove(lpane);
				IsStarting = true;
			}
		});

		// Keyboard/Joystick support
		restartButton.b.addKeyListener(Keys2);
		exitButton.b.addKeyListener(Keys2);
		frame.addKeyListener(Keys2);
		frame.requestFocus();
		exitButton.setSelected(true);

		// Highscore update message
		JPanel Message = new JPanel();
		Message.setBackground(Color.BLACK);
		Message.setLayout(new BoxLayout(Message, BoxLayout.Y_AXIS));

		JLabel highscoreMessage = new JLabel("Highscores:");
		highscoreMessage.setFont(FONT_LARGE);
		highscoreMessage.setForeground(Color.WHITE);

		JLabel newHighscoreMessage = new JLabel(" You got a highscore! ");
		newHighscoreMessage.setFont(FONT30);
		newHighscoreMessage.setForeground(Color.WHITE);

		Message.add(highscoreMessage);
		Message.add(newHighscoreMessage);
		Message.setBounds(width / 2 - 150, (int) (height * 0.3), 600, 180);
		Message.setOpaque(false);
		Message.setBackground(Color.getColor("TRANSLUCENT"));

		// Place the back button
		final JPanel backButtonArea = new JPanel();
		backButtonArea.setBackground(new Color(100, 100, 100, 0));
		backButtonArea.setOpaque(false);

		backButtonArea.setLayout(new BoxLayout(backButtonArea, 1));
		backButtonArea.setBounds((int) (width * 0.255) - 110, (int) (height * 0.82),
				220, 50);

		backButtonArea.add("North", exitButton.b);

		// Place the restart button
		final JPanel restartButtonArea = new JPanel();
		restartButtonArea.setBackground(new Color(100, 100, 100, 0));
		restartButtonArea.setOpaque(false);

		restartButtonArea.setLayout(new BoxLayout(restartButtonArea, 1));
		restartButtonArea.setBounds((int) (width * 0.715) - 110, (int) (height * 0.82),
				230, 50);
		restartButtonArea.add("North", restartButton.b);

		
		// Bounds for all the layered panels //
		highscores.setBounds(width / 2 - 420, (int) (height * 0.45), 400, 155);
		highscores.setOpaque(false);
		highscores.setBackground(Color.getColor("TRANSLUCENT"));

		besthighscores.setBounds((int) (width * 0.52), (int) (height * 0.45),
				400, 155);
		besthighscores.setOpaque(false);
		besthighscores.setBackground(Color.getColor("TRANSLUCENT"));

		background.setBounds(0, 0, width, height);
		background.setOpaque(false);
		background.setBackground(Color.getColor("TRANSLUCENT"));

		// Add everything to the main panel
		lpane.setBounds(0, 0, 600, 400);
		lpane.add(background, 0, 0);
		lpane.add(highscores, 1, 0);
		lpane.add(besthighscores, 2, 0);
		lpane.add(Message, 3, 0);
		lpane.add(restartButtonArea, 4, 0);
		lpane.add(backButtonArea, 5, 0);

		//
		frame.requestFocus();
		frame.addKeyListener(this);
		frame.getContentPane().add(lpane);
		frame.setVisible(true);
		frame.repaint();
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

		try {
			joystick.poll();
		} catch (Exception e) {
		}

		// X = joystick.getAxisValue(JOYSTICK_X);
		Y = 0;
		try {
			Y = joystick.getAxisValue(JOYSTICK_Y);
		} catch (Exception e) {
		}

		if (Math.abs(Y) > 0.5) {
			//System.out.println("" + currentButton);
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

		if ((ButtonCheck == false) && (ButtonPressed == true)) {
			UseButtons();
			ButtonPressed = false;
		} else if (ButtonCheck == true) {
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
			currentButton = MenuButtons.size() - 1;
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

	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
	}
}

