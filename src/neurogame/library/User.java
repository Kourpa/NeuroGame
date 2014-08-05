package neurogame.library;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	private String Name;
	private ArrayList<Long> HighScores = new ArrayList<Long>();
	
	public User(String initName){
		Name = initName;
	}
	
	
	/**
	 * Get the highest scores
	 * @param amount
	 */
	public Long[] getHighScores(int amount){
		Long[] best;
		
		Collections.sort(HighScores);
		Collections.reverse(HighScores);
		
		if((HighScores != null) && (amount > HighScores.size())){
			best = HighScores.subList(0,HighScores.size()).toArray(new Long[0]);
		}
		else{
			best = HighScores.subList(0,amount).toArray(new Long[0]);
		}
		
		return best;
	}
	
	public Long getBestScore(){
		if((HighScores != null) && (HighScores.size() != 0)){
			Collections.sort(HighScores);
			Collections.reverse(HighScores);
			return HighScores.get(0);
		}else{
			return 0l;
		}
	}
	
	public void saveHighscore(long score){
		DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
		
		score = Long.parseLong(dateFormat.format(date).toString() + score+"");
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

class HighscoreComparator implements Comparator<Long>{
	@Override
	public int compare(Long arg0, Long arg1) {
		boolean result = arg0 < arg1;
		if(result){
			return 1;
		}
		return 0;
	}
  
}
