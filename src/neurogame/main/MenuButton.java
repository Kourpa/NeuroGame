package neurogame.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class MenuButton {
	
	public JButton b;
	private boolean selected;
	private Color SelectedColor,DeselectedColor;
	private int DEFAULT_BUTTON_SIZE = 30;
	private Font FONT30;
	private Font FONT40;
	
	MenuButton(String text) {
		BuildButton(text, DEFAULT_BUTTON_SIZE, null);
	}
	
    MenuButton(String text,int textSize) {
    	BuildButton(text,textSize, null);
    }
    
    MenuButton(String text, int textSize, ActionListener arg){
    	BuildButton(text, textSize, arg);
    }
    
    MenuButton(String text, ActionListener arg){
    	BuildButton(text, 30, arg);
    }
    
    private void BuildButton(String text, int textSize, ActionListener arg){
    	
    	// Center Text
    	String newText = "";
    	newText = text;
        b = new JButton(newText);
        
        // Defaults
        b.setIcon(null);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setMargin(new Insets(5,5,5,5));
        b.setBorder(null);
        
        if(arg != null){
        	b.addActionListener(arg);
        }
        
        // Fonts
        FONT30 = new Font("Karmatic Arcade", Font.PLAIN, textSize);
		FONT40 = new Font("Karmatic Arcade", Font.PLAIN, 25);
		
        b.setFont(FONT30);
        SelectedColor = new Color(100, 191, 255);
        DeselectedColor = new Color(180,180,180);
        b.setForeground(DeselectedColor);
    }
    
    public void setSelected(boolean choice){
    	selected = choice;
    	if (choice == true){
    		b.setForeground(SelectedColor);
    	}
    	else{
    		b.setForeground(DeselectedColor);
    	}
    }
    
    public boolean isSelected(){
    	return selected;
    }
}