package neurogame.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import org.lwjgl.input.Controller;

public abstract class MenuScreen implements ActionListener, KeyListener, MouseListener{
	public ArrayList<MenuButton> buttonList = new ArrayList<MenuButton>();
	private boolean MovingUp, MovingDown;
	private boolean ButtonPressed = false;
	private int currentButton;
	
	
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

	public void keyPressed(KeyEvent arg0) {
	}

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

	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		Object src = arg0.getSource();
		
		for(int i=0;i<this.buttonList.size();i++){
			if (buttonList.get(i).b == src){
				buttonList.get(i).setSelected(true);
			}
			else{
				buttonList.get(i).setSelected(false);
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}
}
