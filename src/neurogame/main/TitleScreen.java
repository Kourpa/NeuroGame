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
public class TitleScreen implements ActionListener, KeyListener {
	private Image titleBackground, profileBackground, startButtonPlain,
			startButtonSelected, rewindButtonSelected, backButtonPlain,
			newUserButtonPlain, newUserButtonSelected, checkboxSelected,
			checkboxPlain;

	private Image exitButtonPlain, exitButtonSelected, rewindButtonPlain,
			backButtonSelected, configButtonPlain, configButtonSelected;

	private static MenuButton oddballButton, exitButton, startButton,
			startButtonProfile, rewindButton, backButtonProfile, newUserButton;

	public boolean IsExiting, IsStarting, IsOption;
	public int selectedJoystick;
	public User selectedUser;
	public int selectedJoystickIndex;
	public Color TextColor; 
	
	//
	private NeuroFrame frame;
	private JLayeredPane lpane;

	// Keys and joystick support
	private ArrayList<MenuButton> buttonList = new ArrayList<MenuButton>();
	private KeyAdapter Keys;
	private boolean MovingUp, MovingDown;
	private boolean ButtonPressed = false;
	private int currentButton;

	private Font FONT_SMALL;
	private JLabel creditNames;
	
	private JCheckBox loggingBox;
	private static JTextField nameInputField;
	private JComboBox<String> userList = new JComboBox<String>(
			Library.getUserNames());
	// private JComboBox<Integer> joystickIndexList = new JComboBox<Integer>();

	private String selected;
	private int creditIndex;

	private boolean enableLogging;

	private BufferedImage masterImage;
	private JComboBox<String> controllerList;
	private Map<String, BufferedImage> sprites;

	private int width;
	private int height;
	
	private int buttonPanelWidth,buttonPanelHeight;

	/**
	 * Instantiate a new TitleScreen.
	 * 
	 * @param frame
	 *            NeuroFrame to contain this TitleScreen.
	 */
	public TitleScreen(final NeuroFrame frame) {
		this.frame = frame;

		// For Polling
		IsExiting = false;
		IsStarting = false;
		IsOption = false;

		width = Library.getWindowPixelWidth();
		height = Library.getWindowPixelHeight();
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
		
		// Fonts
		FONT_SMALL = new Font("Karmatic Arcade", Font.PLAIN, 23);
		
		// Colors
		TextColor = new Color(200,200,200);
		
		// Panel Size
		buttonPanelWidth = 400;
		buttonPanelHeight = 400;

		// New UI
		CreateMainMenu();

		//
		frame.getRootPane().addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				width = frame.getWidth();
				height = frame.getHeight();
			}
		});
	}

	/**
	 * The Customization screen after you selected Start Game
	 */
//	private void CreateProfileScreen() {
//		lpane = new JLayeredPane();
//		frame.getContentPane().removeAll();
//		frame.getContentPane().setLayout(new BorderLayout());
//
//		// Background
//		final JLabel background = new JLabel(new ImageIcon(profileBackground));
//		background.setBackground(Color.BLACK);
//		background.setBounds(0, 0, width, height);
//		background.setOpaque(true);
//
//		JLabel img = new JLabel("");
//		background.add(img);
//
//		/* Buttons */
//		startButtonProfile = new MenuButton("Start Game", 23);// startButtonPlain,startButtonSelected);
//		buttonList.add(startButtonProfile);
//		startButtonProfile.b.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//
//				// User is selected
//				if (Library.getUser(userList.getSelectedIndex()) != null) {
//					frame.requestFocus();
//					background.setVisible(false);
//					lpane.setVisible(false);
//					frame.getContentPane().remove(background);
//					frame.getContentPane().remove(lpane);
//					frame.getContentPane().setLayout(null);
//					frame.removeKeyListener(Keys);
//
//					selectedJoystick = controllerList.getSelectedIndex();
//					selectedUser = Library.getUser(userList.getSelectedIndex());
//					enableLogging = loggingBox.isSelected();
//
//					savePreferences();
//					IsStarting = true;
//				}
//			}
//		});
//
//		// Back Button
//		backButtonProfile = new MenuButton("Back", 23);// backButtonPlain,
//														// backButtonSelected);
//		buttonList.add(backButtonProfile);
//		backButtonProfile.b.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				background.setVisible(false);
//				frame.getContentPane().setLayout(new GridLayout(1, 1));
//				frame.getContentPane().remove(lpane);
//				buttonList.clear();
//				CreateMainMenu();
//			}
//		});
//
//		// Place the back button
//		final JPanel backArea = new JPanel();
//		backArea.setBackground(new Color(100, 100, 100, 0));
//		backArea.setOpaque(false);
//
//		backArea.setLayout(new BoxLayout(backArea, 1));
//		backArea.setBounds((int) (width * 0.255) - 110, (int) (height * 0.82),
//				220, 50);
//
//		backArea.add("North", backButtonProfile.b);
//
//		// Place the start button
//		final JPanel startArea = new JPanel();
//		startArea.setBackground(new Color(100, 100, 100, 0));
//		startArea.setOpaque(false);
//
//		startArea.setLayout(new BoxLayout(startArea, 1));
//		startArea.setBounds((int) (width * 0.715) - 110, (int) (height * 0.82),
//				230, 50);
//		startArea.add("North", startButtonProfile.b);
//
//		// Keyboard
//		startButtonProfile.b.addKeyListener(Keys);
//		backButtonProfile.b.addKeyListener(Keys);
//
//		// Default button
//		frame.requestFocus();
//		startButtonProfile.setSelected(true);
//
//		// User Panel
//		final JPanel userPanel2 = new JPanel();
//		userPanel2.setBackground(Color.BLACK);
//		userPanel2.setLayout(new FlowLayout());
//
//		// nameInputField = new JTextField(16);
//		// nameInputField.setPreferredSize(new Dimension(400, 50));
//
//		// User Selection
//		userList.setPreferredSize(new Dimension(250, 40));
//		userList.setFont(new Font("KarmaticArcade", Font.BOLD, 12));
//		userList.setBackground(Color.BLACK);
//		userList.setForeground(Color.WHITE);
//
//		// JLabel text2 = new JLabel("   New User: ");
//		// text2.setForeground(Color.WHITE);
//		// text2.setFont(new Font("Consolas", Font.BOLD, 32));
//
//		newUserButton = new MenuButton("New User", 20);// newUserButtonPlain,newUserButtonSelected);
//		newUserButton.b.setPreferredSize(new Dimension(195, 80));
//		newUserButton.b.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				CreateNewUser(frame);
//			}
//		});
//
//		JLabel text1 = new JLabel("User: ");
//		Font FONT30 = new Font("Karmatic Arcade", Font.PLAIN, 23);
//		text1.setFont(FONT30);
//		text1.setForeground(Color.WHITE);
//		userPanel2.add(text1);
//		userPanel2.add(userList);
//		userPanel2.add(newUserButton.b);
//		userPanel2.setBackground(Color.getColor("TRANSLUCENT"));
//		userPanel2.setOpaque(false);
//		userPanel2.setBounds((int) (width * 0.25), (int) (height * 0.32), 750,
//				150);
//
//		// Controller
//		final JPanel userPanel3 = null;//Options();
//		userPanel3.setBackground(Color.getColor("TRANSLUCENT"));
//		userPanel3.setOpaque(false);
//		userPanel3.setBounds((int) (width * 0.32), (int) (height * 0.46), 400,
//				40);
//
//		// Logging
//		final JPanel userPanel4 = new JPanel();
//		userPanel4.setBackground(Color.BLACK);
//		userPanel4.setLayout(new BoxLayout(userPanel4, BoxLayout.X_AXIS));
//		userPanel4.setBounds((int) (width * 0.32), (int) (height * 0.55), 700,
//				70);
//
//		loggingBox = new JCheckBox();
//		loggingBox.setBackground(Color.getColor("TRANSLUCENT"));
//		loggingBox.setOpaque(false);
//		loggingBox.setIcon(new ImageIcon(checkboxPlain));
//		loggingBox.setSelectedIcon(new ImageIcon(checkboxSelected));
//		loggingBox.setSelected(true);
//
//		loggingBox.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				enableLogging = loggingBox.isSelected();
//			}
//		});
//
//		JLabel text2 = new JLabel("Logging: ");
//		text2.setFont(FONT30);
//		text2.setForeground(Color.white);
//		userPanel4.add(text2);
//		userPanel4.add(loggingBox);
//		userPanel4.add(new JLabel("  "));
//		userPanel4.setBackground(Color.getColor("TRANSLUCENT"));
//		userPanel4.setOpaque(false);
//
//		// Add everything to Layered Panel
//		lpane.setBounds(0, 0, 600, 400);
//		lpane.add(background, 0,   0);
//		lpane.add(startArea,  1,   0);
//		lpane.add(userPanel2, 2,   0);
//		lpane.add(userPanel3, 3,   0);
//		lpane.add(userPanel4, 4,   0);
//		lpane.add(backArea,   5,   0);
//
//		//frame.getContentPane().add(lpane);
//		frame.setVisible(true);
//		restorePreferences();
//	}

	/**
	 * Create the main menu screen
	 */
	private void CreateMainMenu() {
		lpane = new JLayeredPane();
		lpane.setLayout(null);
		
		// Background
		final JLabel background = new JLabel(new ImageIcon(titleBackground));
		background.setBackground(Color.GRAY);

		// Panels
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.BLACK);
		buttonPanel.setLayout(null);

		// Start Button
		startButton = new MenuButton("Start Game", this);
		buttonList.add(startButton);
		startButton.setSelected(true);

		// Oddball button
		oddballButton = new MenuButton("Visual Test", this);
		buttonList.add(oddballButton);

		// Rewind button
		rewindButton = new MenuButton("Rewind", this);
		buttonList.add(rewindButton);

		// Exit button
		exitButton = new MenuButton("Exit", this);
		buttonList.add(exitButton);
		
		// List of users to select
		userList.setPreferredSize(new Dimension(375, 40));
		userList.setFont(new Font("KarmaticArcade", Font.BOLD, 12));
		userList.setBackground(Color.BLACK);
		userList.setForeground(Color.WHITE);
		updateUsers();

		// Add a new user profile
		/*newUserButton = new MenuButton("New User", 20, this);
		newUserButton.b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CreateNewUser(frame);
			}
		});*/
		
		/*userList.addActionListener(new ActionListener(){
			
		});*/
		
		JPanel userPanel = new JPanel();
		userPanel.setBackground(new Color(20,20,20));
		
		// Controller list
		JComboBox<String> joysticks = Options();
		joysticks.setPreferredSize(new Dimension(375, 40));
		
		JLabel inputMessage = new JLabel("Input: ");
		inputMessage.setFont(FONT_SMALL);
		inputMessage.setForeground(TextColor);
		
		JLabel userMessage = new JLabel("User:  ");
		userMessage.setFont(FONT_SMALL);
		userMessage.setForeground(TextColor);

		// Logging info
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

		JLabel loggingMessage = new JLabel("Logging: ");
		loggingMessage.setFont(FONT_SMALL);
		loggingMessage.setForeground(TextColor);

		// Scrolling Credits
		creditNames = new JLabel("Here are the credits");
		creditNames.setFont(FONT_SMALL);
		creditNames.setForeground(TextColor);
		
		//
		userPanel.add(userMessage);
		userPanel.add(userList);
		userPanel.add(inputMessage);
		userPanel.add(joysticks);
		userPanel.add(loggingMessage);
		userPanel.add(loggingBox);
	
		// Panels
		buttonPanel.add(startButton.b);
		buttonPanel.add(oddballButton.b);
		buttonPanel.add(rewindButton.b);
		buttonPanel.add(exitButton.b);
		buttonPanel.add(creditNames);		
		startButton.b.setBounds(0,0,buttonPanelWidth,50);
		oddballButton.b.setBounds(0,50,buttonPanelWidth,50);
		rewindButton.b.setBounds(0,100,buttonPanelWidth,50);
		exitButton.b.setBounds(0,150,buttonPanelWidth,50);
		creditNames.setBounds(0,250,buttonPanelWidth,50);

		buttonPanel.setBounds(width / 2 - buttonPanelWidth/2, (int) (height * 0.57), buttonPanelWidth, buttonPanelHeight);
		buttonPanel.setOpaque(false);
		
		userPanel.setBounds((int)(width * 0.5) - 250, (int) (height * 0.37), 500, 140);

		background.setBackground(Color.BLACK);
		background.setBounds(0, 0, width, height);
		background.setOpaque(true);

		lpane.setBounds(0, 0, width, height);
		lpane.add(background, 0, 0);
		lpane.add(buttonPanel, 1, 0);
		lpane.add(userPanel, 2, 0);

		//
		frame.requestFocus();
		frame.addKeyListener(this);
		frame.getContentPane().add(lpane);
		frame.setVisible(true);
		frame.repaint();

		restorePreferences();
	}
	
	public void showTitleScreen(boolean bShow){
		
		// Reset everything and display screen
		if(bShow){
			this.IsExiting = false;
			this.IsOption = false;
			this.IsStarting = false;
			lpane.setVisible(true);
		
			frame.getContentPane().setLayout(null); // dont need this.  fix in Gameover screen
			frame.getContentPane().add(lpane);
			lpane.repaint();
			frame.requestFocus();
			
		}
		else{
			frame.getContentPane().remove(lpane);
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

	public void ScrollCredits(){
		String Names = "Martin Joel Blah Blah Blah Blah Blah Blah Blah";
		
		if(creditIndex > Names.length()){
			creditIndex = 0;
		}
		else if(creditIndex+10 > Names.length()){
			creditNames.setText(Names.substring(creditIndex, Names.length()) + Names.substring(1,creditIndex));
		}
		else{
			creditNames.setText(Names.substring(1, 5));
		}
		creditIndex++;
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

		Y = 0;
		try {
			Y = joystick.getAxisValue(JOYSTICK_Y);
		} catch (Exception e) {
		}

		if (Math.abs(Y) > 0.5) {
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
		if (currentButton >= buttonList.size()) {
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
			currentButton = buttonList.size() - 1;
		}
		updateButtons();
	}

	/**
	 * Update the button focus when moving up/down with joystick
	 */
	private void updateButtons() {
		for (int i = 0; i < buttonList.size(); i++) {
			if (i == currentButton) {
				buttonList.get(i).setSelected(true);
			} else {
				buttonList.get(i).setSelected(false);
			}
		}
	}

	/**
	 * Click a button using the joystick
	 */
	private void UseButtons() {
		for (int i = 0; i < buttonList.size(); i++) {
			if (i == currentButton) {
				buttonList.get(i).b.doClick();
				break;
			}
		}
	}

	/**
	 * Creates the panel for the controller options
	 */
	public JComboBox<String> Options() {
		ArrayList<String> ControllerNames = new ArrayList<String>();

		new JDialog(frame, "Options");

		// Joysticks
		try {
			Controllers.create();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		JLabel text1 = new JLabel("Input:  ");
		text1.setFont(FONT_SMALL);
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
		// Integer[] controlOptions = {1,2,3};

		// Joysticks
		JPanel message = new JPanel();
		message.setLayout(new BoxLayout(message, BoxLayout.X_AXIS));
		message.add(text1);
		message.add(controllerList);
		// message.add(new JLabel(".... Main Joystick: "));
		// message.add(joystickIndexList);
		return controllerList;
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
		userList.addItem("NEW USER");
		for (int i = 0; i < names.length; i++) {
			System.out.println("TitleScreen - UserName: "+names[i]);
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

	// Start button has been pressed
	private void onStartButtonPress() {

		// User is selected
		if (Library.getUser(userList.getSelectedIndex()) != null) {
			frame.requestFocus();
			lpane.setVisible(false);
			
			//frame.getContentPane().remove(lpane);

			selectedJoystick = controllerList.getSelectedIndex();
			selectedUser = Library.getUser(userList.getSelectedIndex());
			enableLogging = loggingBox.isSelected();

			savePreferences();
			IsStarting = true;
		}
	}

	private void onExitButtonPress() {
		IsExiting = true;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object src = arg0.getSource();

		// Start
		if (src == startButton.b) {
			onStartButtonPress();
		}

		else if (src == exitButton.b) {
			onExitButtonPress();
		}
	}

	// Oddball
	/*
	 * frame.requestFocus(); background.setVisible(false);
	 * lpane.setVisible(false); lpane.removeAll(); buttonPanel.removeAll();
	 * buttonList.clear();
	 * 
	 * frame.getContentPane().remove(background);
	 * frame.getContentPane().remove(lpane); frame.getContentPane().removeAll();
	 * buttonPanel.setVisible(false);
	 * 
	 * frame.getContentPane().setLayout(null); frame.removeKeyListener(Keys);
	 * IsOption = true;
	 */
	// Rewind

	// Exit
	// public void actionPerformed(ActionEvent e) {
	// IsExiting = true;
	// }

	public void keyPressed(KeyEvent arg0) {}

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

	public void keyTyped(KeyEvent arg0) {}

}