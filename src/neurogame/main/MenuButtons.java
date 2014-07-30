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

public class MenuButtons {
	
	public JButton b;
	private Color SelectedColor;
	private int MaxSize = 12;
	private Font FONT30;
	private Font FONT40;
	
	MenuButtons(String text) {
		BuildButton(text, 30);
	}
	
    MenuButtons(String text,int textSize) {
    	BuildButton(text,textSize);
    }
    
    private void BuildButton(String text, int textSize){
    	String newText = "";
    	for(int i=0; i < 6 - (int)(text.length()/2); i++){
    		newText += "   ";
    	}
    	newText += text;
        b = new JButton(newText);//new ImageIcon(image1));
        //b.setRolloverIcon(new ImageIcon(image2));
        
        //
        b.setIcon(null);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setBorder(null);
        b.setMargin(new Insets(5,5,5,5));
        
        loadFont();
        FONT30 = new Font("Karmatic Arcade", Font.PLAIN, textSize);
		FONT40 = new Font("Karmatic Arcade", Font.PLAIN, 25);
		
        b.setFont(FONT30);
        SelectedColor = new Color(100, 191, 255);
        
        b.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setSelected(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
            	setSelected(false);
            }
        });
    }
    
    private void loadFont(){
		String path = System.getProperty("user.dir");
		path += "/resources/fonts/";

		try {
			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(path
					+ "KarmaticArcade.ttf")));
			System.out.println("Registered Font");
		} catch (IOException | FontFormatException e) {
			System.out.println("Error Loading Font - MenuButtons.java");
		}
	}
    
    public void setSelected(boolean choice){
    	if (choice == true){
    		//b.setIcon(new ImageIcon(img2));
    		b.setForeground(SelectedColor);
    		//b.setFont(FONT40);
    	}
    	else{
    		//b.setIcon(new ImageIcon(img1));
    		b.setForeground(Color.DARK_GRAY);
    		//b.setFont(FONT30);
    	}
    }
}
