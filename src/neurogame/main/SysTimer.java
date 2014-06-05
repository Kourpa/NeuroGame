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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * A specialized timer for NeuroGame, which utilizes the system timer for
 * greater accuracy.
 * 
 * @author Ramon A. Lovato
 * @team Danny Gomez
 * @team Marcos Lemus
 */
public class SysTimer {
	private Thread thread;
	private int delay;
	private ArrayList<ActionListener> listeners;
	private long startTime;
	private long lastAction;
	
	/**
	 * Instantiate a new SysTimer with the provided frequency and
	 * ActionListener.
	 * 
	 * @param delay Time between ticks in milliseconds.
	 * @param listener ActionListener to call when delay has expired.
	 */
	public SysTimer(int delay, ActionListener listener) {
		if (delay < 0) {
			throw new IllegalArgumentException();
		}
		this.delay = delay;
		listeners = new ArrayList<ActionListener>();
		if (listener != null) {
			listeners.add(listener);
		}
		thread = new Thread() {
			@Override
			public void run() {
				while (!isInterrupted()) {
					update();
				}
			}
		};
	}
	
	/**
	 * Set the timer delay.
	 * 
	 * @param delay New timer delay.
	 */
	public void setDelay(int delay) {
		if (delay < 0) {
			throw new IllegalArgumentException();
		}
		this.delay = delay;
	}
	
	/**
	 * Add an ActionListener to be activated when the timer fires.
	 * 
	 * @param listener ActionListener to add.
	 */
	public void addActionListener(ActionListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}
		listeners.add(listener);
	}
	
	/**
	 * Remove an ActionListener.
	 * 
	 * @param listener ActionListener to remove.
	 */
	public void removeActionListener(ActionListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Getter for the list of ActionListeners.
	 * 
	 * @return ArrayList of ActionListeners attached to this timer.
	 */
	public ArrayList<ActionListener> getActionListeners() {
		return listeners;
	}
	
	/**
	 * Start the timer.
	 */
	public void start() {
		startTime = System.currentTimeMillis();
		lastAction = startTime;
		thread.start();
		update();
	}
	
	/**
	 * Stop the timer.
	 */
	public void stop() {
		thread.interrupt();
	}
	
	/**
	 * Request the system time and trigger the ActionListener if necessary.
	 */
	private void update() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastAction >= delay) {
			// The difference between when the timer was expected to fire and 
			// when it actually did.
//			long delta = currentTime - (lastAction + delay);
//			System.out.println("delta: " + delta);
			lastAction = currentTime;
			for (ActionListener listener : listeners) {
				listener.actionPerformed(new ActionEvent(
						this, ActionEvent.ACTION_PERFORMED,
						"fire: " + currentTime));
			}
		}
	}
	
	/**
	 * Get the time (in milliseconds) that the timer has been running.
	 * 
	 * @return Time (in milliseconds) since the timer started.
	 */
	public long elapsedTime() {
		return System.currentTimeMillis() - startTime;
	}
	
	/**
	 * Get the time (in milliseconds) since the last action.
	 * 
	 * @return Time (in milliseconds) since the last action.
	 */
	public long timeSinceLastAction() {
		return System.currentTimeMillis() - lastAction;
	}
	
//	/**
//	 * Main method for testing.
//	 * 
//	 * @param args Command-line arguments.
//	 */
//	public static void main(String[] args) {
//		SysTimer timer = new SysTimer(1000/500, new ActionListener() {
//			int n = 0;
//			@Override
//			public void actionPerformed(ActionEvent ev) {
//				System.out.println(n++);
//			}
//		});
//		timer.start();
//	}
	
}
