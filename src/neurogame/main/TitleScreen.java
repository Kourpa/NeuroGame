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
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import neurogame.library.Library;
import neurogame.library.User;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The title screen for NeuroGame.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 * @team Martin Lidy
 */
public class TitleScreen {
	private Image titleBackground, profileBackground, startButtonPlain,
			startButtonSelected, rewindButtonSelected, backButtonPlain,
			newUserButtonPlain, newUserButtonSelected, checkboxSelected,
			checkboxPlain;

	private Image exitButtonPlain, exitButtonSelected, rewindButtonPlain,
			backButtonSelected, configButtonPlain, configButtonSelected;

	private static MenuButtons configButton, exitButton, startButton,
			startButtonProfile, rewindButton, backButtonProfile, newUserButton;

	public boolean IsExiting, IsStarting, IsOption;
	public int selectedJoystick;
	public User selectedUser;
	public int selectedJoystickIndex;

	// Keys and joystick support
	private ArrayList<MenuButtons> MenuButtons = new ArrayList<MenuButtons>();
	private KeyAdapter Keys;
	private boolean MovingUp, MovingDown;
	private boolean ButtonPressed = false;
	private int currentButton;

	private JCheckBox loggingBox;
	private static JTextField nameInputField;
	private JComboBox<String> userList = new JComboBox<String>(
			Library.getUserNames());
	//private JComboBox<Integer> joystickIndexList = new JComboBox<Integer>();

	private String selected;

	private boolean enableLogging;

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
	public TitleScreen(final NeuroFrame frame) {

		// For Polling
		IsExiting = false;
		IsStarting = false;
		IsOption = false;

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
		backButtonPlain = sprites.get("backButtonPlain");
		backButtonSelected = sprites.get("backButtonSelected");

		newUserButtonPlain = sprites.get("newUserButtonPlain");
		newUserButtonSelected = sprites.get("newUserButtonSelected");

		checkboxSelected = sprites.get("checkboxSelected");
		checkboxPlain = sprites.get("checkboxPlain");

		// KeyListener for using keyboard to select
		Keys = new KeyAdapter() {
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
		};
		
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

	/**
	 * The Customization screen after you selected Start Game
	 */
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

		/* Buttons */
		startButtonProfile = new MenuButtons("Start Game",23);//startButtonPlain,startButtonSelected);
		MenuButtons.add(startButtonProfile);
		startButtonProfile.b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// User is selected
				if(Library.getUser(userList.getSelectedIndex()) != null){					
					frame.requestFocus();
					background.setVisible(false);
					lpane.setVisible(false);
					frame.getContentPane().remove(background);
					frame.getContentPane().remove(lpane);
					frame.getContentPane().setLayout(null);
					frame.removeKeyListener(Keys);
		
					selectedJoystick = controllerList.getSelectedIndex();
					selectedUser = Library.getUser(userList.getSelectedIndex());
					enableLogging = loggingBox.isSelected();
		
					savePreferences();
					IsStarting = true;
				}
			}
		});

		// Back Button
		backButtonProfile = new MenuButtons("Back",23);//backButtonPlain, backButtonSelected);
		MenuButtons.add(backButtonProfile);
		backButtonProfile.b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				background.setVisible(false);
				frame.getContentPane().setLayout(new GridLayout(1, 1));
				frame.getContentPane().remove(lpane);
				MenuButtons.clear();
				CreateMainMenu(frame);
			}
		});

		// Place the back button
		final JPanel backArea = new JPanel();
		backArea.setBackground(new Color(100, 100, 100, 0));
		backArea.setOpaque(false);

		backArea.setLayout(new BoxLayout(backArea, 1));
		backArea.setBounds((int) (width * 0.255) - 110, (int) (height * 0.82),
				220, 50);

		backArea.add("North", backButtonProfile.b);

		// Place the start button
		final JPanel startArea = new JPanel();
		startArea.setBackground(new Color(100, 100, 100, 0));
		startArea.setOpaque(false);

		startArea.setLayout(new BoxLayout(startArea, 1));
		startArea.setBounds((int) (width * 0.715) - 110, (int) (height * 0.82),
				230, 50);
		startArea.add("North", startButtonProfile.b);
		
		// Keyboard
		startButtonProfile.b.addKeyListener(Keys);
		backButtonProfile.b.addKeyListener(Keys);
		
		// Default button
		frame.requestFocus();
		startButtonProfile.setSelected(true);

		// User Panel
		final JPanel userPanel2 = new JPanel();
		userPanel2.setBackground(Color.BLACK);
		userPanel2.setLayout(new FlowLayout());

		//nameInputField = new JTextField(16);
		//nameInputField.setPreferredSize(new Dimension(400, 50));

		// User Selection
		userList.setPreferredSize(new Dimension(250, 40));
		userList.setFont(new Font("KarmaticArcade", Font.BOLD, 12));
		userList.setBackground(Color.BLACK);
		userList.setForeground(Color.WHITE);

		//JLabel text2 = new JLabel("   New User: ");
		//text2.setForeground(Color.WHITE);
		//text2.setFont(new Font("Consolas", Font.BOLD, 32));

		newUserButton = new MenuButtons("New User",20);//newUserButtonPlain,newUserButtonSelected);
		newUserButton.b.setPreferredSize(new Dimension(195, 80));
		newUserButton.b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CreateNewUser(frame);
			}
		});

		JLabel text1 = new JLabel("User: ");
		Font FONT30 = new Font("Karmatic Arcade", Font.PLAIN, 23);
		text1.setFont(FONT30);
		text1.setForeground(Color.WHITE);
		userPanel2.add(text1);
		userPanel2.add(userList);
		userPanel2.add(newUserButton.b);
		userPanel2.setBackground(Color.getColor("TRANSLUCENT"));
		userPanel2.setOpaque(false);
		userPanel2.setBounds((int) (width * 0.25), (int) (height * 0.32), 750,
				150);

		// Controller
		final JPanel userPanel3 = Options(frame);
		userPanel3.setBackground(Color.getColor("TRANSLUCENT"));
		userPanel3.setOpaque(false);
		userPanel3.setBounds((int) (width * 0.32), (int) (height * 0.46), 400,
				40);

		// Logging
		final JPanel userPanel4 = new JPanel();
		userPanel4.setBackground(Color.BLACK);
		userPanel4.setLayout(new BoxLayout(userPanel4, BoxLayout.X_AXIS));
		userPanel4.setBounds((int) (width * 0.32), (int) (height * 0.55), 700,
				70);

		loggingBox = new JCheckBox();
		loggingBox.setBackground(Color.getColor("TRANSLUCENT"));
		loggingBox.setOpaque(false);
		loggingBox.setIcon(new ImageIcon(checkboxPlain));
		loggingBox.setSelectedIcon(new ImageIcon(checkboxSelected));
		loggingBox.setSelected(true);

		loggingBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				enableLogging = loggingBox.isSelected();
			}
		});

		JLabel text2 = new JLabel("Logging: ");
		text2.setFont(FONT30);
		text2.setForeground(Color.white);
		userPanel4.add(text2);
		userPanel4.add(loggingBox);
		userPanel4.add(new JLabel("  "));
		userPanel4.setBackground(Color.getColor("TRANSLUCENT"));
		userPanel4.setOpaque(false);

		// Add everything to Layered Panel
		lpane.setBounds(0, 0, 600, 400);
		lpane.add(background, 0, 0);
		lpane.add(startArea, 1, 0);
		lpane.add(userPanel2, 2, 0);
		lpane.add(userPanel3, 3, 0);
		lpane.add(userPanel4, 4, 0);
		lpane.add(backArea, 5, 0);
		frame.getContentPane().add(lpane);
		frame.setVisible(true);
		restorePreferences();
	}

	/**
	 * Create the main menu screen
	 */
	private void CreateMainMenu(final NeuroFrame frame) {
		final JLayeredPane lpane = new JLayeredPane();
		frame.getContentPane().setLayout(new BorderLayout());
		loadFont();

		// Background
		final JLabel background = new JLabel(new ImageIcon(titleBackground));
		background.setBackground(Color.GRAY);
		JLabel img = new JLabel("");
		background.add(img);

		// Panels
		final JPanel test = new JPanel();
		test.setBackground(Color.BLACK);
		test.setLayout(new BoxLayout(test, 1));
		test.setBackground(Color.WHITE);

		// Start Button
		startButton = new MenuButtons("Start Game");//startButtonPlain, startButtonSelected);
		MenuButtons.add(0, startButton);
		startButton.setSelected(true);
		startButton.b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				background.setVisible(false);
				frame.getContentPane().setLayout(new GridLayout(1, 1));
				frame.getContentPane().remove(lpane);
				MenuButtons.clear();
				CreateProfileScreen(frame);
			}
		});

		// Oddball button
		configButton = new MenuButtons("Visual Test");//configButtonPlain, configButtonSelected);
		MenuButtons.add(configButton);
		configButton.b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.requestFocus();
				background.setVisible(false);
				lpane.setVisible(false);
				lpane.removeAll();
				test.removeAll();
				MenuButtons.clear();
				
				frame.getContentPane().remove(background);
				frame.getContentPane().remove(lpane);
				frame.getContentPane().removeAll();
				test.setVisible(false);
				
				frame.getContentPane().setLayout(null);
				frame.removeKeyListener(Keys);
				IsOption = true;
			}
		});

		// Rewind button
		rewindButton = new MenuButtons("Rewind");//rewindButtonPlain, rewindButtonSelected);
		MenuButtons.add(rewindButton);

		// Exit button
		exitButton = new MenuButtons("Exit");//exitButtonPlain, exitButtonSelected);
		MenuButtons.add(exitButton);
		exitButton.b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				IsExiting = true;
			}
		});

		// Keyboard
		startButton.b.addKeyListener(Keys);
		configButton.b.addKeyListener(Keys);
		rewindButton.b.addKeyListener(Keys);
		exitButton.b.addKeyListener(Keys);
		frame.addKeyListener(Keys);
		frame.requestFocus();

		// Panels
		test.add("North", startButton.b);
		test.add("Center", configButton.b);
		test.add(rewindButton.b);
		test.add("South", exitButton.b);

		test.setBackground(Color.BLACK);
		test.setBounds(width / 2 - 150, height / 2, 300, 300);
		test.setOpaque(true);

		background.setBackground(Color.BLACK);
		background.setBounds(0, 0, width, height);
		background.setOpaque(true);

		lpane.setBounds(0, 0, 600, 400);
		lpane.add(background, 0, 0);
		lpane.add(test, 1, 0);
		frame.getContentPane().add(lpane);
		frame.setVisible(true);

		restorePreferences();
	}

    /**
     * Load the local fonts
     */
    private void loadFont(){
		String path = System.getProperty("user.dir");
		path += "/resources/fonts/";

		try {
			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(path
					+ "KarmaticArcade.ttf")));
			System.out.println("Registered Font");
		} catch (IOException | FontFormatException e) {
			System.out.println("Error Loading Font - MenuButtons.java");
		}
	}
    
	/**
	 * Getters and setters for game options
	 */
	public User GetSelectedUser() {
		return this.selectedUser;
	}

	public int GetSelectedJoystick() {
		return this.selectedJoystick;
	}
	
	public int GetSelectedJoystickIndex() {
		return selectedJoystickIndex;
	}

	public boolean GetLogging() {
		return this.enableLogging;
	}

	/**
	 * Save user preferences to a file
	 */
	private void savePreferences() {
		Document dom;
		Element e = null;
		String path = System.getProperty("user.dir");
		path += "/Users/";

		// instance of a DocumentBuilderFactory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			// use factory to get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// create instance of DOM
			dom = db.newDocument();

			// create the root element
			Element rootEle = dom.createElement("pref");

			// create data elements and place them under root
			e = dom.createElement("Controller");
			e.appendChild(dom.createTextNode(""
					+ controllerList.getSelectedIndex()));
			rootEle.appendChild(e);

			e = dom.createElement("User");
			e.appendChild(dom.createTextNode("" + userList.getSelectedIndex()));
			rootEle.appendChild(e);

			e = dom.createElement("Logging");
			e.appendChild(dom.createTextNode("" + this.enableLogging));
			rootEle.appendChild(e);

			dom.appendChild(rootEle);

			try {
				Transformer tr = TransformerFactory.newInstance()
						.newTransformer();
				tr.setOutputProperty(OutputKeys.INDENT, "yes");
				tr.setOutputProperty(OutputKeys.METHOD, "xml");
				tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				tr.setOutputProperty(
						"{http://xml.apache.org/xslt}indent-amount", "4");

				// send DOM to file
				FileOutputStream out = new FileOutputStream(path + "pref.xml");
				out.close();

				tr.transform(new DOMSource(dom), new StreamResult(
						new FileOutputStream(path + "pref.xml")));

			} catch (TransformerException te) {
				System.out.println(te.getMessage());
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage());
			}
		} catch (ParserConfigurationException pce) {
			System.out
					.println("UsersXML: Error trying to instantiate DocumentBuilder "
							+ pce);
		}

		System.out.println("Finished Saving Perfs: ");
	}

	/**
	 * Restore the saved user preferences file
	 */
	private void restorePreferences() {
		String path = System.getProperty("user.dir");
		path += "/Users/";

		ArrayList<String> perfs = new ArrayList<String>();
		Document dom;
		// Make an instance of the DocumentBuilderFactory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			// use the factory to take an instance of the document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// parse using the builder to get the DOM mapping of the
			// XML file
			dom = db.parse(path + "pref.xml");

			Element doc = dom.getDocumentElement();

			perfs.add(doc.getElementsByTagName("Controller").item(0)
					.getFirstChild().getNodeValue());
			perfs.add(doc.getElementsByTagName("User").item(0).getFirstChild()
					.getNodeValue());
			perfs.add(doc.getElementsByTagName("Logging").item(0)
					.getFirstChild().getNodeValue());

		} catch (Exception pce) {
			System.out.println(pce.getMessage());
		}

		System.out.println("Finished Loading Perfs: ");

		// Joystick perfs
		try {
			selectedJoystick = Integer.parseInt(perfs.get(0));
		} catch (Exception e) {
			System.out.println("Error with joystick");
		}

		//
		try {
			controllerList.setSelectedIndex(Integer.parseInt(perfs.get(0)));
			loggingBox.setSelected(Boolean.parseBoolean(perfs.get(2)));
			userList.setSelectedIndex(Integer.parseInt(perfs.get(1)));
		} catch (Exception e) {
		}

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
		Y=0;
		try{
			Y = joystick.getAxisValue(JOYSTICK_Y);
		}catch(Exception e){
		}

		if (Math.abs(Y) > 0.5) {
			//System.out.println(""+currentButton);
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

	/**
	 * Creates the panel for the controller options
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

		Font FONT30 = new Font("Karmatic Arcade", Font.PLAIN, 23);
		JLabel text1 = new JLabel("Input:  ");
		text1.setFont(FONT30);
		text1.setForeground(Color.WHITE);
		
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
		controllerList.setPreferredSize(new Dimension(250, 10));
		controllerList.setFont(new Font("Consolas", Font.BOLD, 12));
		controllerList.setBackground(Color.BLACK);
		controllerList.setForeground(Color.WHITE);
		
		// Index
		//Integer[] controlOptions = {1,2,3};

		// Joysticks
		JPanel message = new JPanel();
		message.setLayout(new BoxLayout(message, BoxLayout.X_AXIS));
		message.add(text1);
		message.add(controllerList);
		//message.add(new JLabel(".... Main Joystick: "));
		//message.add(joystickIndexList);
		return message;
	}

	/**
	 * Gets the name and starts the creation process in Library.java
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
		System.out.println("Update Users: Length " + names.length);

		userList.removeAllItems();
		for (int i = 0; i < names.length; i++) {
			System.out.println(names[i]);
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
