package neurogame.library;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	private String Name;
	private ArrayList<Double> HighScores = new ArrayList<Double>();
	
	public User(String initName){
		Name = initName;
	}
	
	
	/**
	 * Get the highest scores
	 * @param amount
	 */
	public Double[] getHighScores(int amount){
		Double[] best = new Double[amount];
		return best;
	}	
	
	/**
	 * Getters and setters for the name
	 */
	public String getName(){
		return Name;
	}
	public void setName(String newName){
		Name = newName;
	}
}
