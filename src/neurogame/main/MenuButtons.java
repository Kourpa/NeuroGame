package neurogame.main;

import java.awt.*;

import javax.swing.*;

public class MenuButtons {
	
	public JButton b;
	private Image img1, img2;
	
    MenuButtons(Image image1, Image image2) {
        b = new JButton(new ImageIcon(image1));
        b.setRolloverIcon(new ImageIcon(image2));
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setBorder(null);
        img1 = image1;
        img2 = image2;
    }
    
    public void setSelected(boolean choice){
    	if (choice == true){
    		b.setIcon(new ImageIcon(img2));
    	}
    	else{
    		b.setIcon(new ImageIcon(img1));
    	}
    }
}
//
//    public static void main(String[] args) throws Exception {
//        URL url1 = new URL("http://pscode.org/media/citymorn1.jpg");
//        URL url2 = new URL("http://pscode.org/media/citymorn2.jpg");
//        
//        final Image img1 = ImageIO.read(url1);
//        final Image img2 = ImageIO.read(url2);
//        
//        //Create the frame on the event dispatching thread
//        SwingUtilities.invokeLater(new Runnable(){
//            @Override
//            public void run() {
//                new MenuButtons(img1, img2);
//            }
//        });
//    }
//}
