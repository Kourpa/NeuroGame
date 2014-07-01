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
import org.xml.sax.SAXException;

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
			startButtonSelected, rewindButtonSelected;
	private Image exitButtonPlain, exitButtonSelected, rewindButtonPlain;
	private Image configButtonPlain, configButtonSelected;

	public boolean IsExiting, IsStarting, IsOption;
	public int selectedJoystick;
	public User selectedUser;
	private KeyAdapter Keys;

	private int currentButton;
	private int maxButton = 3;
	private ArrayList<MenuButtons> MenuButtons = new ArrayList<MenuButtons>();

	private static MenuButtons configButton, exitButton, startButton,
			startButtonProfile, rewindButton;
	private static JButton newUserButton;
	private static ArrayList<String> Users;
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

		startButtonProfile = new MenuButtons(startButtonPlain,
				startButtonSelected);

		startButtonProfile.b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.requestFocus();
				background.setVisible(false);
				lpane.setVisible(false);
				frame.getContentPane().remove(background);
				frame.getContentPane().remove(lpane);

				frame.getContentPane().setLayout(null);
				selectedJoystick = controllerList.getSelectedIndex();
				selectedUser = Library.getUser(userList.getSelectedIndex());
				savePreferences();
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
		userList.setFont( new Font("Consolas", Font.BOLD, 12));
		userInfo.add(userList);

		JLabel text2 = new JLabel("   New User: ");
		text2.setForeground(Color.WHITE);
		text2.setFont(new Font("Consolas", Font.BOLD, 32));
		userInfo.add(text2);

		userInfo.add(nameInputField);

		newUserButton = new JButton("New User");
		newUserButton.setFont( new Font("Karmatic Arcade", Font.BOLD, 12));
		newUserButton.setIcon(null);
		
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
		userPanel4.setLayout(new BoxLayout(userPanel4, BoxLayout.Y_AXIS));
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
		restorePreferences();

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
				//selectedUser = Library.getUser(userList.getSelectedIndex());
				// IsStarting = true;
				CreateProfileScreen(frame);
			}
		});

		configButton = new MenuButtons(configButtonPlain, configButtonSelected);
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
			dom = db.parse(path+"pref.xml");

			Element doc = dom.getDocumentElement();

			perfs.add(doc.getElementsByTagName("Controller").item(0).getFirstChild().getNodeValue());
			perfs.add(doc.getElementsByTagName("User").item(0).getFirstChild().getNodeValue());

		} catch (ParserConfigurationException pce) {
			System.out.println(pce.getMessage());
		} catch (SAXException se) {
			System.out.println(se.getMessage());
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
		
		System.out.println("Finished Loading Perfs: ");
		userList.setSelectedIndex(Integer.parseInt(perfs.get(1)));
		controllerList.setSelectedIndex(Integer.parseInt(perfs.get(0)));
		
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
		controllerList = new JComboBox<String>(
				ControllerNames.toArray(new String[0]));
		controllerList.setPreferredSize(new Dimension(250, 40));
		controllerList.setFont( new Font("Consolas", Font.BOLD, 12));

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
