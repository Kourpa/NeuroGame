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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;

import neurogame.library.Library;
import neurogame.library.User;

/**
 * The title screen for NeuroGame.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 * @team Martin Lidy
 */
public class GameOverScreen extends MenuScreen {
	private Image backgroundImage;

	public boolean IsExiting, IsStarting, IsOption, IsRestarting;
	public int selectedJoystick;
	public User selectedUser;

	private static MenuButton exitButton, restartButton;
	private Map<String, BufferedImage> sprites;
	
	private Color BLUE = new Color(100, 191, 255);
	private Font FONT_SMALL = new Font("Karmatic Arcade", Font.PLAIN, 20);
	private Font FONT_LARGE = new Font("Karmatic Arcade", Font.PLAIN, 40);
	
	private NeuroFrame frame;
	private JLayeredPane lpane;

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
		this.frame = frame;

		// For Polling
		IsExiting = false;
		IsStarting = false;

		//
		width = frame.getWidth();
		height = frame.getHeight();
		sprites = Library.getSprites();

		// Get the images.
		backgroundImage = sprites.get("profileBackground");

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

	/**
	 * Fills the Layered Pane with content
	 */
	/**
	 * @param frame
	 */
	private void CreateGameOverScreen(final NeuroFrame frame) {
		frame.getContentPane().setLayout(new BorderLayout());

		// Main pane for the UI
		lpane = new JLayeredPane();
		lpane.setLayout(null);
		lpane.setBackground(new Color(17, 17, 17, 255));

		// Background
		final JLabel background = new JLabel(new ImageIcon(backgroundImage));

		// Grab the highscore arrays
		Object[] userHighScores = frame.getUser().getHighScores(5);
		String[] highscoresPersonal = new String[userHighScores.length];

		// Parse the highscore bits: YYMMDD + Highscore
		for (int i = 0; i < userHighScores.length; i++) {
			highscoresPersonal[i] = " "
					+ userHighScores[i].toString().substring(2, 4)
					+ "-"
					+ userHighScores[i].toString().substring(4, 6)
					+ "-"
					+ userHighScores[i].toString().substring(0, 2)
					+ ":       "
					+ userHighScores[i].toString().substring(6,
							userHighScores[i].toString().length());
		}

		// Personal best highscores JList
		JList<String> personalJList = new JList<String>(highscoresPersonal);
		personalJList.setBackground(Color.getColor("TRANSLUCENT"));
		personalJList.setOpaque(false);

		personalJList.setForeground(BLUE);
		personalJList.setFont(new Font("Karmatic Arcade", Font.PLAIN, 20));

		Object[] bestHighScores = Library.getBestHighScores(5);
		String[] highscoresGlobal = new String[bestHighScores.length];

		// Parse the highscore bits: YYMMDD + Highscore
		for (int i = 0; i < bestHighScores.length; i++) {
			highscoresGlobal[i] = " "
					+ bestHighScores[i].toString().substring(2, 4)
					+ "-"
					+ bestHighScores[i].toString().substring(4, 6)
					+ "-"
					+ bestHighScores[i].toString().substring(0, 2)
					+ ":       "
					+ bestHighScores[i].toString().substring(6,
							bestHighScores[i].toString().length());
		}

		// Global best highscores JList
		JList<String> globalJList = new JList<String>(highscoresGlobal);
		globalJList.setBackground(Color.getColor("TRANSLUCENT"));
		globalJList.setOpaque(false);
		globalJList.setForeground(BLUE);
		globalJList.setFont(FONT_SMALL);
		
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
		personalMessage.setFont(FONT_SMALL);
		personalMessage.setForeground(Color.white);
		
		highscores.add(personalMessage);
		highscores.add(personalJList);

		// All time best scores message
		JLabel bestMessage = new JLabel("All Time Best: ");
		bestMessage.setFont(FONT_SMALL);
		bestMessage.setForeground(Color.white);
		
		besthighscores.add(bestMessage);
		besthighscores.add(globalJList);

		// RestartButton
		restartButton = new MenuButton(" Restart", 22, this);// restartButtonPlain,restartButtonSelected);
		restartButton.b.addMouseListener(this);
		buttonList.add(restartButton);

		// MainMenu
		exitButton = new MenuButton("Main Menu", 22, this);
		exitButton.b.addMouseListener(this);
		buttonList.add(0, exitButton);
		
		// Keyboard/Joystick support
		exitButton.setSelected(true);

		// Highscore update message
		JPanel Message = new JPanel();
		Message.setBackground(Color.BLACK);
		Message.setLayout(new BoxLayout(Message, BoxLayout.Y_AXIS));

		JLabel highscoreMessage = new JLabel("Highscores:");
		highscoreMessage.setFont(FONT_LARGE);
		highscoreMessage.setForeground(Color.WHITE);

		JLabel newHighscoreMessage = new JLabel(" You got a highscore! ");
		newHighscoreMessage.setFont(FONT_SMALL);
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
		backButtonArea.setBounds((int) (width * 0.28) - 110, (int) (height * 0.82),
				220, 50);

		backButtonArea.add("North", exitButton.b);

		// Place the restart button
		final JPanel restartButtonArea = new JPanel();
		restartButtonArea.setBackground(new Color(100, 100, 100, 0));
		restartButtonArea.setOpaque(false);

		restartButtonArea.setLayout(new BoxLayout(restartButtonArea, 1));
		restartButtonArea.setBounds((int) (width * 0.74) - 110, (int) (height * 0.82),
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
	
	private void onExitButtonPress(){
		lpane.setVisible(false);
		frame.getContentPane().setLayout(new GridLayout(1, 1));
		frame.getContentPane().remove(lpane);
		IsRestarting = true;
	}
	
	private void onMainMenuButtonPress(){
		lpane.setVisible(false);
		frame.getContentPane().setLayout(new GridLayout(1, 1));
		frame.getContentPane().remove(lpane);
		IsStarting = true;
	}

	public void actionPerformed(ActionEvent arg0) {
		if (exitButton.isSelected()){
			onMainMenuButtonPress();
		} 
		else if (restartButton.isSelected()){
			onExitButtonPress();
		}
	}
}

