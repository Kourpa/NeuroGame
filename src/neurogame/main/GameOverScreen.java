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
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
public class GameOverScreen {
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
		titleBackground = sprites.get("titleBackground");
		profileBackground = sprites.get("highscoreBackground");
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
	
	private void CreateMainMenu(final NeuroFrame frame) {
		final JLayeredPane lpane = new JLayeredPane();

		// Background
		frame.getContentPane().setLayout(new BorderLayout());

		final JLabel background = new JLabel(new ImageIcon(profileBackground));
		background.setBackground(Color.GRAY);

		// To align the buttons...
		JLabel img = new JLabel("");
		background.add(img);

		// Panels
		final JPanel highscores = new JPanel();
		highscores.setBackground(Color.BLACK);
		highscores.setLayout(new BoxLayout(highscores, 1));
				
		Object[] data = frame.getUser().getHighScores(5);
		
		List list = new List(data.length);
		for(int i=0;i<data.length;i++){
			list.add(""+data[i]);
		}
		
		list.setBackground(Color.BLACK);
		list.setForeground(Color.WHITE);
		
		list.setFont( new Font("Karmatic Arcade", Font.BOLD, 12));
		//JList list = new JList(testing);
		//list.setPreferredSize(new Dimension(1000,1000));

		highscores.add(list);
		
		//
		final JPanel test = new JPanel();
		test.setBackground(Color.BLACK);
		test.setLayout(new BoxLayout(test, 1));

		// Buttons
		startButton = new MenuButtons(startButtonPlain, startButtonSelected);
		MenuButtons.add(0, startButton);
		startButton.b.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				background.setVisible(false);
				frame.getContentPane().setLayout(new GridLayout(1, 1));
				frame.getContentPane().remove(lpane);
				frame.removeKeyListener(Keys);
			}
		});

		exitButton = new MenuButtons(exitButtonPlain, exitButtonSelected);
		MenuButtons.add(exitButton);
		exitButton.b.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				IsExiting = true;
			}
		});

		startButton.b.addKeyListener(Keys);
		exitButton.b.addKeyListener(Keys);
		frame.addKeyListener(Keys);

		//
		test.add("East", startButton.b);
		test.add("West", exitButton.b);

		test.setBackground(Color.BLACK);
		test.setBounds(width / 2 - 150, (int)(height*0.7), 300, 100);
		test.setOpaque(true);
		
		highscores.setBackground(Color.BLACK);
		highscores.setPreferredSize(new Dimension(500,500));
		highscores.setBounds(width / 2 - 150, (int)(height*0.4), 300, 200);
		highscores.setOpaque(true);

		background.setBackground(Color.BLACK);
		background.setBounds(0, 0, width, height);
		background.setOpaque(true);

		lpane.setBounds(0, 0, 600, 400);
		lpane.add(background, 0, 0);
		lpane.add(test, 1, 0);
		lpane.add(highscores,2,0);
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
			dom = db.parse(path + "pref.xml");

			Element doc = dom.getDocumentElement();

			perfs.add(doc.getElementsByTagName("Controller").item(0)
					.getFirstChild().getNodeValue());
			perfs.add(doc.getElementsByTagName("User").item(0).getFirstChild()
					.getNodeValue());

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
