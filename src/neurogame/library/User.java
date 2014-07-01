package neurogame.library;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	private String Name;
	private ArrayList<Integer> HighScores = new ArrayList<Integer>();
	private String path;
	
	public User(String initName){
		Name = initName;
	}
	
	
	/**
	 * Get the highest scores
	 * @param amount
	 */
	public Integer[] getHighScores(int amount){
		Integer[] best;
		
		if(amount > HighScores.size()){
			best = HighScores.subList(0,HighScores.size()).toArray(new Integer[0]);
		}
		else{
			best = HighScores.subList(0, amount).toArray(new Integer[0]);
		}
		
		return best;
	}	
	
	public void saveHighscore(int score){
		HighScores.add(score);
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
