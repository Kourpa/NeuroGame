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

	public boolean IsExiting, IsStarting, IsOption,IsRestarting;
	public int selectedJoystick;
	public User selectedUser;
	private KeyAdapter Keys;

	private int currentButton;
	private int maxButton = 3;
	private ArrayList<MenuButtons> MenuButtons = new ArrayList<MenuButtons>();

	private static MenuButtons exitButton, restartButton, startButton;
	private static JTextField nameInputField;
	private JComboBox<String> userList;

	private String selected;

	private BufferedImage masterImage;
	private JComboBox<String> controllerList;
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
		IsOption = false;

		// masterImage = new BufferedImage(Library.getWindowWidth(),
		// Library.getWindowHeight(), BufferedImage.TYPE_INT_ARGB);
		// masterGraphics = masterImage.createGraphics();
		// image = masterImage;
		// graphics = image.createGraphics();

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
		CreateMainMenu(frame);

		//
		frame.getRootPane().addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				width = frame.getWidth();
				height = frame.getHeight();
			}
		});

		selected = "start";
	}

	private void CreateMainMenu(final NeuroFrame frame) {
		frame.getContentPane().setLayout(new BorderLayout());

		// Main pane for the UI
		final JLayeredPane lpane = new JLayeredPane();
		lpane.setBackground(new Color(17,17,17,255));
		
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

		// Start Button
		startButton = new MenuButtons(startButtonPlain, startButtonSelected);
		MenuButtons.add(0, startButton);

		// ExitButton
		exitButton = new MenuButtons(exitButtonPlain, exitButtonSelected);
		/*MenuButtons.add(exitButton);
		exitButton.b.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				IsExiting = true;
			}
		});*/
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

		// RestartButton
		restartButton = new MenuButtons(restartButtonPlain,
				restartButtonSelected);
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

		// Keyboard/Joystick support
		startButton.b.addKeyListener(Keys);
		restartButton.b.addKeyListener(Keys);
		exitButton.b.addKeyListener(Keys);
		startButton.b.requestFocus();
		frame.addKeyListener(Keys);

		//
		//Buttons.add(startButton.b);
		Buttons.add(restartButton.b);
		Buttons.add(exitButton.b);
		
		Buttons.setBounds(width / 2 - 150, (int) (height * 0.65), 300, 180);
		Buttons.setOpaque(false);
		Buttons.setBackground(Color.getColor("TRANSLUCENT"));
		
		highscores.setBounds(width / 2 - 420, (int) (height * 0.45), 300, 150);
		highscores.setOpaque(false);
		highscores.setBackground(Color.getColor("TRANSLUCENT"));

		besthighscores.setBounds((int)(width * 0.52), (int) (height * 0.45), 300, 150);
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

	/**
	 * Move to the button bellow with a joystick
	 */
	private void MoveDown() {
		currentButton += 1;
		if (currentButton > maxButton) {
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
			currentButton = maxButton;
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
			}
		}
	}

	/**
	 * Creates the panel for the controller options
	 * 
	 * @param frame
	 * @return
	 */
	public JPanel Options(final NeuroFrame frame) {
		ArrayList<String> ControllerNames = new ArrayList<String>();
		new JDialog(frame, "Options");

		// Joysticks
		try {
			Controllers.create();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		// add the keyboard as default
		ControllerNames.add("Keyboard");

		int count = Controllers.getControllerCount();

		for (int i = 0; i < count; i++) {
			Controller controller = Controllers.getController(i);
			System.out.println(controller.getName());
			ControllerNames.add(controller.getName());
		}

		// Options Menu
		controllerList = new JComboBox<String>(
				ControllerNames.toArray(new String[0]));
		controllerList.setPreferredSize(new Dimension(250, 40));

		JPanel mainBox = new JPanel();
		mainBox.setLayout(new BoxLayout(mainBox, BoxLayout.Y_AXIS));

		// Joysticks
		JPanel message = new JPanel();
		message.setLayout(new BorderLayout());
		// message.add(new JLabel("Select Controller:"), BorderLayout.WEST);
		message.setBackground(Color.WHITE);
		message.add(controllerList, BorderLayout.WEST);
		return message;
	}

	/**
	 * Gets the name and starts the creation process in Library.java
	 * 
	 * @param frame
	 */
	public void CreateNewUser(final NeuroFrame frame) {
		final JDialog dialog = new JDialog(frame, "NewUser");

		JPanel mainBox = new JPanel();
		mainBox.setLayout(new BoxLayout(mainBox, BoxLayout.Y_AXIS));

		JPanel message = new JPanel();
		message.setLayout(new BorderLayout());
		message.add(new JLabel("New User Name:  "), BorderLayout.WEST);
		message.setBackground(Color.WHITE);

		nameInputField = new JTextField(16);
		nameInputField.setPreferredSize(new Dimension(400, 50));

		JButton newUserButton2 = new JButton("Add");
		newUserButton2.setPreferredSize(new Dimension(100, 50));

		newUserButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Library.addUser(nameInputField.getText());
				nameInputField.setText("");
				updateUsers();
				dialog.dispose();
			}
		});

		message.add(nameInputField, BorderLayout.CENTER);
		message.add(newUserButton2, BorderLayout.EAST);

		mainBox.add(message);

		dialog.setModal(true);
		dialog.setContentPane(mainBox);
		dialog.pack();
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}

	/**
	 * Updates the user list after adding a new user
	 */
	private void updateUsers() {
		String[] names = Library.getUserNames();
		userList.removeAllItems();

		for (int i = 0; i < names.length; i++) {
			userList.addItem(names[i]);
		}
	}

	/**
	 * Toggles which button is selected.
	 */
	public void switchButton() {
		selected = (selected == "start" ? "exit" : "start");
		// draw();
	}

	/**
	 * Getter for selected.
	 * 
	 * @return selected String representation of selected button.
	 */
	public String getSelected() {
		return selected;
	}

	/**
	 * Getter for the buffered image.
	 * 
	 * @return BufferedImage of the title screen.
	 */
	public BufferedImage getImage() {
		// System.out.println("TitleScreen.getImage(): image.size = ("+image.getWidth()+", "+image.getHeight()+")");
		return masterImage;
	}

}
