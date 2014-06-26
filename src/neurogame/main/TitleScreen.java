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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
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
 */
public class TitleScreen {
	private Image titleBackground,profileBackground, startButtonPlain, startButtonSelected,
			rewindButtonSelected;
	private Image exitButtonPlain, exitButtonSelected, rewindButtonPlain;
	private Image configButtonPlain, configButtonSelected;

	public boolean IsExiting, IsStarting, IsOption;
	public int selectedJoystick;
	public User selectedUser;
	private KeyAdapter Keys;

	private int currentButton;
	private int maxButton = 3;
	private ArrayList<MenuButtons> MenuButtons = new ArrayList<MenuButtons>();
	private NeuroFrame SavedFrame;

	private static MenuButtons configButton, exitButton, startButton, startButtonProfile,rewindButton;
	private static JButton newUserButton;
	private static ArrayList<String> Users;
	private static JTextField nameInputField;
	private JComboBox<String> userList;

	private String selected;

	private BufferedImage masterImage;
	private Graphics masterGraphics;

	private BufferedImage image;
	private Graphics graphics;
	private Map<String, BufferedImage> sprites;

	private int width;
	private int height;

	/**
	 * Instantiate a new TitleScreen.
	 * 
	 * @param frame
	 *            NeuroFrame to contain this TitleScreen.
	 */
	public TitleScreen(final NeuroFrame frame) {

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
		titleBackground = sprites.get("titleBackground");
		profileBackground = sprites.get("profileBackground");
		startButtonPlain = sprites.get("startButtonPlain");
		startButtonSelected = sprites.get("startButtonSelected");
		exitButtonPlain = sprites.get("exitButtonPlain");
		exitButtonSelected = sprites.get("exitButtonSelected");
		configButtonPlain = sprites.get("configButtonPlain");
		configButtonSelected = sprites.get("configButtonSelected");
		rewindButtonPlain = sprites.get("rewindButtonPlain");
		rewindButtonSelected = sprites.get("rewindButtonSelected");

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

	private void CreateProfileScreen(final NeuroFrame frame) {
		final JLayeredPane lpane = new JLayeredPane();
		frame.getContentPane().removeAll();
		frame.getContentPane().setLayout(new BorderLayout());

		// Background
		final JLabel background = new JLabel(new ImageIcon(profileBackground));
		background.setBackground(Color.BLACK);
		background.setBounds(0, 0, width, height);
		background.setOpaque(true);
		JLabel img = new JLabel("");
		background.add(img);
		
		startButtonProfile = new MenuButtons(startButtonPlain, startButtonSelected);
		
		startButtonProfile.b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.requestFocus();
				background.setVisible(false);
				lpane.setVisible(false);
				frame.getContentPane().remove(background);
				frame.getContentPane().remove(lpane);
				
				frame.getContentPane().setLayout(null);
				IsStarting = true;
			}
		});
		
		exitButton.b.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {

				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					System.out.println("up 4");
					MoveUp();
					break;
				case KeyEvent.VK_DOWN:
					System.out.println("Down! 4");
					MoveDown();
					break;
				case KeyEvent.VK_ENTER:
					UseButtons();
				default:
					break;
				}
			}
		});

		configButton.b.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {

				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					System.out.println("up 2");
					MoveUp();
					break;
				case KeyEvent.VK_DOWN:
					System.out.println("Down! 2");
					MoveDown();
					break;
				case KeyEvent.VK_ENTER:
					UseButtons();
				default:
					break;
				}
			}
		});

		// Button Panel
		final JPanel test = new JPanel();
		test.setBackground(Color.BLACK);
		test.setLayout(new BoxLayout(test, 1));
		test.setBackground(Color.BLACK);
		test.setBounds((int) (width * 0.5) - 150, (int) (height * 0.3), 300,
				200);
		test.setOpaque(true);
		test.add("North", startButtonProfile.b);

		// User Panel
		final JPanel userPanel2 = new JPanel();
		userPanel2.setBackground(Color.BLACK);
		userPanel2.setLayout(new FlowLayout());
		
		nameInputField = new JTextField(16);
		nameInputField.setPreferredSize(new Dimension(400, 50));
		
		// User Selection
		JPanel userInfo = new JPanel(new GridBagLayout());
		userInfo.setBackground(Color.BLACK);

		JLabel text = new JLabel("Select User: ");
		text.setFont(new Font("Consolas", Font.BOLD, 32));
		text.setForeground(Color.WHITE);
		userInfo.add(text);

		userList = new JComboBox<String>(Library.getUserNames());
		userList.setPreferredSize(new Dimension(250, 50));
		userInfo.add(userList);

		JLabel text2 = new JLabel("   New User: ");
		text2.setForeground(Color.WHITE);
		text2.setFont(new Font("Consolas", Font.BOLD, 32));
		userInfo.add(text2);

		userInfo.add(nameInputField);

		newUserButton = new JButton("New User");
		newUserButton.setPreferredSize(new Dimension(100, 50));
		userInfo.add(newUserButton);

		newUserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CreateNewUser(frame);
			}
		});

		userPanel2.add(userList);
		userPanel2.add(newUserButton);
		userPanel2.setBackground(Color.BLACK);
		userPanel2.setBounds((int) (width * 0.6) - 250, (int) (height * 0.4),
				500, 150);

		// Controller
		final JPanel userPanel3 = Options(frame);
		userPanel3.setBackground(Color.BLACK);
		userPanel3.setLayout(new FlowLayout());
		userPanel3.setBounds((int) (width * 0.6) - 300, (int) (height * 0.5),
				500, 50);

		// Logging
		final JPanel userPanel4 = new JPanel();
		userPanel4.setBackground(Color.BLACK);
		userPanel4.setLayout(new BoxLayout(userPanel4,BoxLayout.Y_AXIS));
		userPanel4.setBounds((int) (width * 0.6) - 200, (int) (height * 0.63),
				400, 70);

		JCheckBox loggingBox = new JCheckBox();
		loggingBox.setBackground(Color.BLACK);
		JCheckBox debugBox = new JCheckBox();
		debugBox.setBackground(Color.BLACK);

		userPanel4.add(loggingBox);
		userPanel4.add(new JLabel("  "));
		userPanel4.add(debugBox);

		// Add everything to Layered Panel
		lpane.setBounds(0, 0, 600, 400);
		lpane.add(background, 0, 0);
		lpane.add(test, 1, 0);
		lpane.add(userPanel2, 2, 0);
		lpane.add(userPanel3, 3, 0);
		lpane.add(userPanel4, 4, 0);
		lpane.setBackground(Color.YELLOW);
		frame.getContentPane().add(lpane);
		frame.setVisible(true);

	}

	private void CreateMainMenu(final NeuroFrame frame) {
		final JLayeredPane lpane = new JLayeredPane();

		// Background
		frame.getContentPane().setLayout(new BorderLayout());

		final JLabel background = new JLabel(new ImageIcon(titleBackground));
		background.setBackground(Color.GRAY);

		// To align the buttons...
		JLabel img = new JLabel("");
		background.add(img);

		// Panels
		final JPanel test = new JPanel();
		test.setBackground(Color.BLACK);
		test.setLayout(new BoxLayout(test, 1));
		test.setBackground(Color.WHITE);

		// Buttons
		startButton = new MenuButtons(startButtonPlain, startButtonSelected);
		MenuButtons.add(0, startButton);
		startButton.b.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				background.setVisible(false);
				frame.getContentPane().setLayout(new GridLayout(1, 1));
				frame.getContentPane().remove(lpane);
				frame.removeKeyListener(Keys);
				// selectedUser = Library.getUser(userList.getSelectedIndex());
				// IsStarting = true;
				CreateProfileScreen(frame);
			}
		});

		configButton = new MenuButtons(configButtonPlain, configButtonSelected);// =
																				// new
																				// JButton("");
		MenuButtons.add(configButton);
		configButton.b.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				IsOption = true;
				Options(frame);
			}
		});

		rewindButton = new MenuButtons(rewindButtonPlain, rewindButtonSelected);// new
																				// JButton("");
		// KeyListener
		Keys = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					System.out.println("up ! frame");
					MoveUp();
					break;
				case KeyEvent.VK_DOWN:
					System.out.println("Down! frame!");
					MoveDown();
					break;
				case KeyEvent.VK_ENTER:
					UseButtons();
				default:
					break;
				}
			}
		};
		
		MenuButtons.add(rewindButton);
		rewindButton.b.addKeyListener(Keys);

		exitButton = new MenuButtons(exitButtonPlain, exitButtonSelected);
		MenuButtons.add(exitButton);
		exitButton.b.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				IsExiting = true;
			}
		});

		startButton.b.addKeyListener(Keys);
		exitButton.b.addKeyListener(Keys);
		configButton.b.addKeyListener(Keys);
		frame.addKeyListener(Keys);

		//
		test.add("North", startButton.b);
		test.add("Center", configButton.b);
		test.add(rewindButton.b);
		test.add("South", exitButton.b);

		test.setBackground(Color.BLACK);
		test.setBounds(width / 2 - 150, height / 2, 300, 200);
		test.setOpaque(true);

		background.setBackground(Color.BLACK);
		background.setBounds(0, 0, width, height);
		background.setOpaque(true);

		lpane.setBounds(0, 0, 600, 400);
		lpane.add(background, 0, 0);
		lpane.add(test, 1, 0);
		lpane.setBackground(Color.YELLOW);
		frame.getContentPane().add(lpane);
		frame.setVisible(true);
	}

	private void MoveDown() {
		currentButton += 1;
		if (currentButton > maxButton) {
			currentButton = 0;
		}
		updateButtons();
	}

	private void MoveUp() {
		currentButton += -1;

		if (currentButton < 0) {
			currentButton = maxButton;
		}
		updateButtons();
	}

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

	private void UseButtons() {

		switch (currentButton) {
		case 0:
			startButton.b.doClick();
			break;
		case 1:
			configButton.b.doClick();
			break;
		case 2:
			rewindButton.b.doClick();
		case 3:
			exitButton.b.doClick();
		default:
			break;
		}
	}

	//
	//
	// private void CreatePanelsOLD(final NeuroFrame frame){
	// // Background
	// frame.getContentPane().setLayout(new FlowLayout());
	//
	// final JLabel background = new JLabel(new ImageIcon(titleBackground));
	// background.setLayout(new GridLayout(3,1));
	// background.setBackground(Color.BLACK);
	//
	// // To align the buttons...
	// background.add(new JLabel(""));
	//
	// // Panels
	// final JPanel test = new JPanel();
	// test.setBackground(Color.BLACK);
	// test.setLayout(new FlowLayout(1,100,0));
	//
	// // Buttons
	// startButton = new JButton("");
	// startButton.setIcon(new ImageIcon(startButtonPlain));
	// startButton.setPreferredSize(new Dimension(300,100));
	// startButton.setBackground(Color.BLACK);
	// startButton.addActionListener(new ActionListener() {
	//
	// public void actionPerformed(ActionEvent e)
	// {
	// background.setVisible(false);
	// frame.getContentPane().setLayout(new GridLayout(1,1));
	// frame.getContentPane().remove(background);
	//
	// selectedUser = Library.getUser(userList.getSelectedIndex());
	// IsStarting = true;
	// }
	// });
	//
	// exitButton = new JButton("");
	// exitButton.setIcon(new ImageIcon(exitButtonPlain));
	// exitButton.setPreferredSize(new Dimension(300,100));
	// exitButton.setBackground(Color.BLACK);
	// exitButton.addActionListener(new ActionListener() {
	//
	// public void actionPerformed(ActionEvent e)
	// {
	// IsExiting = true;
	// }
	// });
	//
	// configButton = new JButton("");
	// configButton.setIcon(new ImageIcon(exitButtonPlain));
	// configButton.setPreferredSize(new Dimension(300,100));
	// configButton.setBackground(Color.BLACK);
	// configButton.addActionListener(new ActionListener() {
	//
	// public void actionPerformed(ActionEvent e)
	// {
	// IsOption = true;
	// Options(frame);
	// }
	// });
	//
	// // User Selection
	// JPanel userInfo = new JPanel(new GridBagLayout());
	// userInfo.setBackground(Color.BLACK);
	//
	// JLabel text = new JLabel("Select User: ");
	// text.setFont(new Font("Consolas", Font.BOLD, 32));
	// text.setForeground(Color.WHITE);
	// userInfo.add(text);
	//
	// userList = new JComboBox<String>(Library.getUserNames());
	// userList.setPreferredSize(new Dimension(250,50));
	// userInfo.add(userList);
	//
	// JLabel text2 = new JLabel("   New User: ");
	// text2.setForeground(Color.WHITE);
	// text2.setFont(new Font("Consolas", Font.BOLD, 32));
	// userInfo.add(text2);
	//
	// nameInputField = new JTextField(16);
	// nameInputField.setPreferredSize(new Dimension(400,50));
	// userInfo.add(nameInputField);
	//
	// newUserButton = new JButton("New User");
	// newUserButton.setPreferredSize(new Dimension(100,50));
	// userInfo.add(newUserButton);
	//
	// newUserButton.addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e)
	// {
	// Library.addUser(nameInputField.getText());
	//
	// nameInputField.setText("");
	// userList.removeAll();
	// userList = new JComboBox<String>(Users.toArray(new String[0]));
	// System.out.println("Users: "+Users);
	// }
	// });
	//
	// //
	// test.add(startButton);
	// background.add(userInfo);
	// test.add(configButton);
	// test.add(exitButton);
	//
	// background.add(test);
	// frame.getContentPane().add(background);
	// frame.setVisible(true);
	// }

	public JPanel Options(final NeuroFrame frame) {
		ArrayList<String> ControllerNames = new ArrayList<String>();
		final JDialog dialog = new JDialog(frame, "Options");

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
		final JComboBox<String> controllerList = new JComboBox<String>(
				ControllerNames.toArray(new String[0]));
		controllerList.setPreferredSize(new Dimension(250, 40));

		JPanel mainBox = new JPanel();
		mainBox.setLayout(new BoxLayout(mainBox, BoxLayout.Y_AXIS));

		// Joysticks
		JPanel message = new JPanel();
		message.setLayout(new BorderLayout());
		//message.add(new JLabel("Select Controller:"), BorderLayout.WEST);
		message.setBackground(Color.WHITE);
		message.add(controllerList, BorderLayout.WEST);
		return message;
	}

	// // Buttons
	// JPanel message2 = new JPanel();
	// JButton exit = new JButton("Exit");
	// exit.addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// dialog.dispose();
	// }
	// });
	//
	// JButton accept = new JButton("Accept");
	// accept.addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// // Set the Joystick then close
	// selectedJoystick = controllerList.getSelectedIndex();
	// dialog.dispose();
	// }
	// });
	// message2.add(exit);
	// message2.add(accept);
	//
	// //
	// mainBox.add(message);
	// mainBox.add(message2);
	//
	// dialog.setModal(true);
	// dialog.setContentPane(mainBox);
	// dialog.pack();
	// dialog.setLocationRelativeTo(frame);
	// dialog.setVisible(true);
	// }

	public void CreateNewUser(final NeuroFrame frame) {
		final JDialog dialog = new JDialog(frame, "NewUser");

		JPanel mainBox = new JPanel();
		mainBox.setLayout(new BoxLayout(mainBox, BoxLayout.Y_AXIS));

		JPanel message = new JPanel();
		message.setLayout(new BorderLayout());
		message.add(new JLabel("New User Name:  "), BorderLayout.WEST);
		message.setBackground(Color.WHITE);
		
		nameInputField = new JTextField(16);
		nameInputField.setPreferredSize(new Dimension(400,50));
		
		newUserButton = new JButton("Add");
		newUserButton.setPreferredSize(new Dimension(100,50));
		
		message.add(nameInputField, BorderLayout.CENTER);
		message.add(newUserButton, BorderLayout.EAST);
		

		//
		mainBox.add(message);

		dialog.setModal(true);
		dialog.setContentPane(mainBox);
		dialog.pack();
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
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
