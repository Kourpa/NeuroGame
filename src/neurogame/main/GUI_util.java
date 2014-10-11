package neurogame.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class GUI_util
{
	public static final Color COLOR_SELECTED = new Color(100, 191, 255);
	public static final Color COLOR_DESELECTED = new Color(220,220,220);
	
	public static final Font FONT36 = new Font("Karmatic Arcade", Font.PLAIN, 36);
	public static final Font FONT30 = new Font("Karmatic Arcade", Font.PLAIN, 30);
	public static final Font FONT20 = new Font("Karmatic Arcade", Font.PLAIN, 20);
  public static final Font FONT_ARIAL30 = new Font("Arial", Font.PLAIN, 48);
	public static final Font FONT_ARIAL20 = new Font("Arial", Font.PLAIN, 28);
	

  public static JButton makeButton(String text, ActionListener listener, java.awt.Container parent)
  {
    JButton button = new JButton(text);
        
    parent.add(button);
    button.setIcon(null);
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    button.setMargin(new Insets(5,5,5,5));
    button.setBorder(null);
        
    button.addActionListener(listener);
        
    button.setFont(FONT30);

    button.setForeground(COLOR_DESELECTED);
    return button;
  }
  
  
  public static JLabel makeLabel30(String text, java.awt.Container parent)
  {
    JLabel label = new JLabel(text);
    parent.add(label);
    label.setFont(FONT30);
    label.setForeground(COLOR_DESELECTED);
    return label;
  }
  
  
  public static JLabel makeLabel20(String text, java.awt.Container parent)
  {
    JLabel label = new JLabel(text);
    parent.add(label);
    label.setFont(FONT20);
    return label;
  }
  
  
  public static JLabel makeLabelArial30(String text, java.awt.Container parent)
  {
    JLabel label = new JLabel(text);
    parent.add(label);
    label.setFont(FONT_ARIAL30);
    label.setForeground(COLOR_DESELECTED);
    return label;
  }
  
  public static JLabel makeLabelArial20(String text, java.awt.Container parent)
  {
    JLabel label = new JLabel(text);
    parent.add(label);
    label.setFont(FONT_ARIAL20);
    label.setForeground(COLOR_DESELECTED);
    return label;
  }
  
  
  public static JTextArea makeTextAreaArial20(String text, java.awt.Container parent)
  {
    JTextArea area = new JTextArea(text);
    parent.add(area);
    area.setFont(FONT_ARIAL20);
    area.setBackground(Color.BLACK);
    area.setForeground(COLOR_DESELECTED);
    area.setEditable(false);
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    return area;
  }

    
  public static void setSelected(JButton button, boolean selected)
  {
    button.setSelected(selected);
    
    if (selected)
    { 
      button.setForeground(COLOR_SELECTED);
      button.setFont(FONT36);
    }
    else 
    {
      button.setForeground(COLOR_DESELECTED);
      button.setFont(FONT30);
    }

  }
}