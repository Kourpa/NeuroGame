package neurogame.main;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import neurogame.library.Library;

public class GUI_util
{

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
        
    button.setFont(Library.FONT30);

    button.setForeground(Library.UNSELECTED_TEXT_COLOR);
    return button;
  }
  
  
  public static JLabel makeLabel30(String text, java.awt.Container parent)
  {
    JLabel label = new JLabel(text);
    parent.add(label);
    label.setFont(Library.FONT30);
    label.setForeground(Library.UNSELECTED_TEXT_COLOR);
    return label;
  }
  
  
  public static JLabel makeLabel20(String text, java.awt.Container parent)
  {
    JLabel label = new JLabel(text);
    parent.add(label);
    label.setFont(Library.FONT20);
    return label;
  }
  
  
  public static JLabel makeLabelArial30(String text, java.awt.Container parent)
  {
    JLabel label = new JLabel(text);
    parent.add(label);
    label.setFont(Library.FONT_ARIAL30);
    label.setForeground(Library.UNSELECTED_TEXT_COLOR);
    return label;
  }
  
  public static JLabel makeLabelArial20(String text, java.awt.Container parent)
  {
    JLabel label = new JLabel(text);
    parent.add(label);
    label.setFont(Library.FONT_ARIAL20);
    label.setForeground(Library.UNSELECTED_TEXT_COLOR);
    return label;
  }
  
  
  public static JTextArea makeTextAreaArial20(String text, java.awt.Container parent)
  {
    JTextArea area = new JTextArea(text);
    parent.add(area);
    area.setFont(Library.FONT_ARIAL20);
    area.setBackground(Color.BLACK);
    area.setForeground(Library.UNSELECTED_TEXT_COLOR);
    area.setEditable(false);
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    return area;
  }

    
  public static void setSelected(JComponent component, boolean selected)
  {
    //component.setSelected(selected);
    
    if (selected)
    { 
      component.setForeground(Library.HIGHLIGHT_TEXT_COLOR);
      component.setFont(Library.FONT36);
    }
    else 
    {
      component.setForeground(Library.UNSELECTED_TEXT_COLOR);
      component.setFont(Library.FONT30);
    }

  }
}