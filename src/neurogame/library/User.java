package neurogame.library;

import java.io.Serializable;

public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	private String Name;
	
	public User(String initName){
		Name = initName;
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
