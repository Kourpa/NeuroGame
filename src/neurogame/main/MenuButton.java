package neurogame.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;

public class MenuButton {
	
	public JButton b;
	private Color SelectedColor,DeselectedColor;
	private int MaxSize = 12;
	private Font FONT30;
	private Font FONT40;
	
	MenuButton(String text) {
		BuildButton(text, 30);
	}
	
    MenuButton(String text,int textSize) {
    	BuildButton(text,textSize);
    }
    
    private void BuildButton(String text, int textSize){
    	
    	// Center Text
    	String newText = "";
    	for(int i=0; i < 6 - (int)(text.length()/2); i++){
    		newText += "   ";
    	}
    	newText += text;
        b = new JButton(newText);
        
        // Defaults
        b.setIcon(null);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setBorder(null);
        b.setMargin(new Insets(5,5,5,5));
        
        FONT30 = new Font("Karmatic Arcade", Font.PLAIN, textSize);
		FONT40 = new Font("Karmatic Arcade", Font.PLAIN, 25);
		
        b.setFont(FONT30);
        SelectedColor = new Color(100, 191, 255);
        DeselectedColor = new Color(180,180,180);
        
        /*
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setSelected(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
            	setSelected(false);
            }
        });*/
    }
    
    public void setSelected(boolean choice){
    	if (choice == true){
    		b.setForeground(SelectedColor);
    	}
    	else{
    		b.setForeground(DeselectedColor);
    	}
    }
}