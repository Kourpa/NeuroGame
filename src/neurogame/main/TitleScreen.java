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

import neurogame.library.Library;
import neurogame.library.User;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * The title screen for NeuroGame.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 * @team Martin Lidy
 */
public class TitleScreen extends MenuScreen {
	private Image titleBackground, checkboxSelected,
			checkboxPlain;


	private static MenuButton oddballButton, exitButton, startButton,
			configureButton;

	public boolean IsExiting, IsStarting, IsOption;
	public int selectedJoystick;
	public User selectedUser;
	public int selectedJoystickIndex;
	public Color TextColor;

	//
	private NeuroFrame frame;
	private NeuroGame game;
	private JLayeredPane lpane;

	private Font FONT_SMALL;

	// Credits
	private JLabel creditNames;
	private String creditNameString;
	private int creditIndex, creditCheck;

	private String[] Names = { "Joel Castellanos", "Martin Lidy",
			"Marcus Lemus", "Danny Gomez", "Ramon A. Lovato" };

	private JCheckBox loggingBox;
	private static JTextField nameInputField;
	private JComboBox<String> userList = new JComboBox<String>(
			Library.getUserNames());

	private JComboBox<Integer> xJoystickIndex;
	private JComboBox<Integer> yJoystickIndex;

	private String selected;

	private BufferedImage masterImage;
	private JComboBox<String> controllerList;
	private Map<String, BufferedImage> sprites;

	private int width;
	private int height;

	private int buttonPanelWidth, buttonPanelHeight;

	/**
	 * Instantiate a new TitleScreen.
	 * 
	 * @param frame
	 *            NeuroFrame to contain this TitleScreen.
	 */
	public TitleScreen(final NeuroFrame frame, NeuroGame game) {
		this.frame = frame;
		this.game = game;
		
		// For Polling
		IsExiting = false;
		IsStarting = false;
		IsOption = false;

		width = Library.getWindowPixelWidth();
		height = Library.getWindowPixelHeight();
		sprites = Library.getSprites();

		// Get the images.
		titleBackground = sprites.get("titleBackground");
		checkboxSelected = sprites.get("checkboxSelected");
		checkboxPlain = sprites.get("checkboxPlain");

		// Fonts
		FONT_SMALL = new Font("Karmatic Arcade", Font.PLAIN, 23);

		// Colors
		TextColor = new Color(200, 200, 200);

		// Credits
		ArrayList<String> nameSorted = new ArrayList<String>();
		nameSorted.addAll(Arrays.asList(Names));
		Collections.shuffle(nameSorted);

		creditNameString = "Created By... ";
		for (int i = 0; i < nameSorted.size(); i++) {
			creditNameString += "   " + nameSorted.get(i) + "   ";
		}

		System.out.println("    Credit Names: " + creditNameString);

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
	 * Adds content to the layered pane
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
		startButton.b.addMouseListener(this);
		buttonList.add(startButton);
		startButton.setSelected(true);

		// Oddball button
		oddballButton = new MenuButton("P300 Responce Test", this);
		oddballButton.b.addMouseListener(this);
		buttonList.add(oddballButton);

//		// Rewind button
//		rewindButton = new MenuButton("Rewind", this);
//		rewindButton.b.addMouseListener(this);
//		buttonList.add(rewindButton);

		// Exit button
		exitButton = new MenuButton("Exit", this);
		exitButton.b.addMouseListener(this);
		buttonList.add(exitButton);

		// List of users to select
		userList.setPreferredSize(new Dimension(375, 40));
		userList.setFont(new Font("KarmaticArcade", Font.BOLD, 12));
		userList.setBackground(Color.BLACK);
		userList.setForeground(Color.WHITE);
		updateUsers();

		userList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (userList.getSelectedIndex() == 1) {
					CreateNewUser(frame);
				}
				else if(userList.getSelectedIndex() < 2){
					startButton.b.setEnabled(false);
				}
				else{
					startButton.b.setEnabled(true);
					frame.requestFocus();
					savePreferences();
				}
			}
		});

		JPanel userPanel = new JPanel();
		userPanel.setBackground(Color.black);

		// Choose the joystick index
		Integer[] joystickIndexes = {0,1,2,3,4};
	
		xJoystickIndex = new JComboBox<Integer>(joystickIndexes);
		xJoystickIndex.setSelectedIndex(0);
		yJoystickIndex = new JComboBox<Integer>(joystickIndexes);
		xJoystickIndex.setSelectedIndex(1);
		
		xJoystickIndex.setBackground(Color.BLACK);
		xJoystickIndex.setForeground(Color.WHITE);
		yJoystickIndex.setBackground(Color.BLACK);
		yJoystickIndex.setForeground(Color.WHITE);
		
		xJoystickIndex.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (xJoystickIndex.getSelectedIndex() == yJoystickIndex.getSelectedIndex()){
					yJoystickIndex.setSelectedIndex(yJoystickIndex.getSelectedIndex() + 1);
				}
			}
		});
		
		yJoystickIndex.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (xJoystickIndex.getSelectedIndex() == yJoystickIndex.getSelectedIndex()){
					xJoystickIndex.setSelectedIndex(xJoystickIndex.getSelectedIndex() + 1);
				}
			}
		});
		

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
				game.setLoggingMode(loggingBox.isSelected());
				frame.requestFocus();
				savePreferences();
			}
		});



		// Scrolling Credits
		JPanel creditPanel = new JPanel();
		creditNames = new JLabel("Here are the credits");
		creditNames.setFont(FONT_SMALL);
		creditNames.setForeground(TextColor);
		creditPanel.add(creditNames);

		// Joystick configureButton
		configureButton = new MenuButton("  Settings", 22, this);
		configureButton.b.addMouseListener(this);
		buttonList.add(configureButton);

		userPanel.add(userMessage);
		userPanel.add(userList);

		userPanel.add(configureButton.b);

		// Panels
		buttonPanel.add(startButton.b);
		buttonPanel.add(oddballButton.b);
		//buttonPanel.add(rewindButton.b);
		buttonPanel.add(exitButton.b);

		startButton.b.setBounds(0, 0, buttonPanelWidth, 50);
		oddballButton.b.setBounds(0, 50, buttonPanelWidth, 50);
		//rewindButton.b.setBounds(0, 100, buttonPanelWidth, 50);
		exitButton.b.setBounds(0, 150, buttonPanelWidth, 50);

		buttonPanel.setBounds(width / 2 - buttonPanelWidth / 2,
				(int) (height * 0.57), buttonPanelWidth, buttonPanelHeight);
		buttonPanel.setOpaque(false);

		userPanel.setBounds((int) (width * 0.5) - 250, (int) (height * 0.37),
				500, 140);

		//
		creditNames.setBounds(0, 0, (int) (width * 0.8), 30);
		creditNames.setPreferredSize(new Dimension((int) (width * 0.6), 30));
		creditPanel.setBounds((int) (width * 0.5)-400,
				(int) (height * 0.9), (int) (width * 0.8), 30);
		creditPanel.setOpaque(false);

		background.setBackground(Color.BLACK);
		background.setBounds(0, 0, width, height);
		background.setOpaque(true);

		lpane.setBounds(0, 0, width, height);
		lpane.add(background, 0, 0);
		lpane.add(buttonPanel, 1, 0);
		lpane.add(userPanel, 2, 0);
		lpane.add(creditPanel, 3, 0);

		//
		frame.requestFocus();
		frame.addKeyListener(this);
		frame.getContentPane().add(lpane);
		frame.setVisible(true);
		frame.repaint();

		restorePreferences();
	}

	public void showTitleScreen(boolean bShow) {

		// Reset everything and display screen
		if (bShow) {
			this.IsExiting = false;
			this.IsOption = false;
			this.IsStarting = false;
			lpane.setVisible(true);

			frame.getContentPane().setLayout(null); // dont need this. fix in
													// Gameover screen
			frame.getContentPane().add(lpane);
			lpane.repaint();
			frame.requestFocus();

		} else {
			frame.getContentPane().remove(lpane);
		}
	}

	public int getJoystickIndex(int index){
		if(index == 0){
			return xJoystickIndex.getSelectedIndex();			
		}
		
		return yJoystickIndex.getSelectedIndex(); 
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
			e.appendChild(dom.createTextNode("" + this.loggingBox.isSelected()));
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
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
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
			game.setLoggingMode(loggingBox.isSelected());
			userList.setSelectedIndex(0);
		} catch (Exception e) {
		}

	}

	/**
	 * Scrolls the credits
	 */
	public void ScrollCredits(float deltaTime) {
		int marqueeSize = 40;
		int updateSpeed = 10; // Higher is slower
		creditCheck++;

		// Update slower than the tick speed
		if (creditCheck % updateSpeed == 0) {

			// reset the index if it passes
			if (creditIndex > creditNameString.length()) {
				creditIndex = 0;
			}
			
			else if (creditIndex + marqueeSize > creditNameString.length()){
				creditNames.setText(creditNameString.substring(creditIndex, creditNameString.length())
								+ creditNameString.substring(0, marqueeSize - (creditNameString.length() - creditIndex)));
			}
			else{
				creditNames.setText(creditNameString.substring(creditIndex, creditIndex + marqueeSize));
			}

			creditIndex++;
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
		
		controllerList.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				frame.requestFocus();
				savePreferences();
			}
		});

		return controllerList;
	}

	
	/**
	 * Configuration screen for the joystick
	 */
	public void JoystickConfigure(final NeuroFrame frame) {
    Dimension dm = new Dimension(250, 200);

    final JDialog dialog = new JDialog(frame, "Configure Options");
    dialog.setMaximumSize(dm);
    dialog.setMinimumSize(dm);

    JPanel panel = new JPanel();
    panel.setBackground(Color.BLACK);
//    panel.setLayout(new BorderLayout());
    panel.setSize(dm);
    panel.setMaximumSize(dm);

    JLabel inputMessage = new JLabel("Input: ");
    inputMessage.setFont(FONT_SMALL);
    inputMessage.setForeground(TextColor);

    // Controller list
    JComboBox<String> joysticks = Options();
    joysticks.setPreferredSize(new Dimension(240, 40));

    // Messages
    JLabel xIndexMessage = new JLabel(" X");
    xIndexMessage.setFont(FONT_SMALL);
    xIndexMessage.setForeground(TextColor);

    JLabel yIndexMessage = new JLabel(" Y");
    yIndexMessage.setFont(FONT_SMALL);
    yIndexMessage.setForeground(TextColor);

    JLabel loggingMessage = new JLabel("Logging: ");
    loggingMessage.setFont(FONT_SMALL);
    loggingMessage.setForeground(TextColor);


    panel.add(inputMessage);
    panel.add(joysticks);
    panel.add(xIndexMessage);
    panel.add(xJoystickIndex);
    panel.add(yIndexMessage);
    panel.add(yJoystickIndex);
    panel.add(loggingMessage);
    panel.add(loggingBox);

//		JPanel message = new JPanel();
//		message.setLayout(new BorderLayout());
//
//		JLabel joystickInstructions = new JLabel("Press Left on the Joystick:");
//		joystickInstructions.setFont(FONT_SMALL);
//		joystickInstructions.setBackground(Color.WHITE);
//		message.add(joystickInstructions);
//		message.setBounds(0,0,150,150);
//		message.setBackground(new Color(50,50,50));

//		dialog.setContentPane(message);
//		dialog.setModal(true);
    dialog.pack();
    dialog.add(panel);
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}

	
	/**
	 * Gets the name and starts the creation process in Library.java
	 */
	public void CreateNewUser(final NeuroFrame frame) {
		final JDialog dialog = new JDialog(frame, "New User");

		JPanel mainBox = new JPanel();
		mainBox.setLayout(new BoxLayout(mainBox, BoxLayout.Y_AXIS));

		JPanel message = new JPanel();
		message.setLayout(new BorderLayout());
		JLabel msg = new JLabel("  New User Name:  ");
		msg.setFont(FONT_SMALL);
		msg.setForeground(this.TextColor);
		message.add(msg, BorderLayout.WEST);
		message.setBackground(new Color(50,50,50));

		nameInputField = new JTextField(16);
		nameInputField.setPreferredSize(new Dimension(400, 50));
		nameInputField.setBackground(Color.BLACK);
		nameInputField.setForeground(Color.WHITE);

		JButton newUserButton2 = new JButton("Add");
		newUserButton2.setIcon(null);
		newUserButton2.setBorderPainted(false);
		newUserButton2.setContentAreaFilled(false);
		newUserButton2.setMargin(new Insets(5,5,5,5));
		newUserButton2.setBorder(null);
		newUserButton2.setFont(FONT_SMALL);
		newUserButton2.setForeground(new Color(100, 191, 255));
		newUserButton2.setPreferredSize(new Dimension(100, 50));

		newUserButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String usrName = nameInputField.getText();
				Library.addUser(usrName);
				nameInputField.setText("");
				updateUsers(usrName);
				dialog.dispose();
				frame.requestFocus();
				savePreferences();
			}
		});

		message.add(nameInputField, BorderLayout.CENTER);
		message.add(newUserButton2, BorderLayout.EAST);

		mainBox.add(message);

		dialog.setModal(true);
		dialog.setContentPane(mainBox);
		dialog.pack();
		dialog.setLocationRelativeTo(frame);
		dialog.setIconImage(null);
		dialog.setVisible(true);
	}

	/**
	 * Updates the user list after adding a new user
	 */
	private void updateUsers() {
		updateUsers(null);
	}
	
	/**
	 * Update the user list when you add a new user
	 */
	private void updateUsers(String usrName){
		String[] names = Library.getUserNames();
		System.out.println("Update Users: Length " + names.length);

		userList.removeAllItems();
		userList.addItem("- Select User -");
		userList.addItem("NEW USER");
		
		for (int i = 0; i < names.length; i++) {
			userList.addItem(names[i]);
			
			if((usrName != null) && (names[i].compareTo(usrName))==0){
				userList.setSelectedIndex(i+2);
			}
		}
	}
	
	/**
	 * Called when the start button is activated
	 */
	private void onStartButtonPress() {

		// User is selected
		if (Library.getUser(userList.getSelectedIndex()-2) != null) {
			frame.requestFocus();
			lpane.setVisible(false);

			selectedJoystick = controllerList.getSelectedIndex();
			selectedUser = Library.getUser(userList.getSelectedIndex() - 2);
			game.setLoggingMode(loggingBox.isSelected());

			savePreferences();
			IsStarting = true;
		}
	}

	private void onExitButtonPress() {
		IsExiting = true;
	}

	private void onOddballButtonPress() {
		frame.requestFocus();
		lpane.setVisible(false);

		selectedJoystick = controllerList.getSelectedIndex();
		selectedUser = Library.getUser(userList.getSelectedIndex());
		game.setLoggingMode(loggingBox.isSelected());

		savePreferences();
		IsOption = true;
	}
	
	private void onConfigureButtonPress() {
    JoystickConfigure(frame);
	}
	
	public void actionPerformed(ActionEvent arg0) {
    if (startButton.isSelected()){
			onStartButtonPress();
		}
		else if (exitButton.isSelected()) {
			onExitButtonPress();
		}
		else if (oddballButton.isSelected()) {
			onOddballButtonPress();
		}
		else if (configureButton.isSelected()){
			onConfigureButtonPress();
		}
	}
}