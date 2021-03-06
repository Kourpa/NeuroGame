package neurogame.main;

import neurogame.io.InputController;
import neurogame.io.User;
import neurogame.library.Library;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@SuppressWarnings("serial")
public class TitleScreen extends JPanel implements ActionListener
{
  private BufferedImage backgroundImage;

  private static final int BUTTON_USER_IDX = 0;
  private static final int BUTTON_BANDIT_IDX = 1;
  private static final int BUTTON_ODDBALL_IDX = 2;
  private static final int BUTTON_START_IDX = 3;
  private static final int BUTTON_EXIT_IDX = 4;
  private static final int BUTTON_COUNT = 5;

  private static final int SELECT_CURRENT = 0;
  private static final int SELECT_UP = 1;
  private static final int SELECT_DOWN = 2;

  private int buttonSelectedIdx;
  private int tutorialNum = 0;

  // private static JButton[] buttonList = new JButton[BUTTON_COUNT];

  private static JButton button_start, button_oddball, button_bandit, button_exit;

  private NeuroGame game;
  private InputController gameController;
  private User selectedUser;

  // Credits
  private JLabel label_creditNames;

  private String creditNameString;
  private double creditIndex = 0;

  private String[] Names =
  { "Joel Castellanos", "Martin Lidy", "Marcus Lemus", "Danny Gomez", "Ramon A. Lovato" };

  private Map<String, BufferedImage> sprites;

  // private JComboBox<String> dropDown_userList;

  // private JPanel userPanel;
  // private JLabel label_userName;
  // private JCheckBox loggingBox;
  private JTextField edit_userName;
  private JLabel label_user, label_userName;

  // private JComboBox<String> dropDownController;
  // private JComboBox<Integer> dropDown_joystickX;
  // private JComboBox<Integer> dropDown_joystickY;
  // private JButton but_configOK, but_configCancel;
  // private JoystickTestDrawPanel joyTestPanel;

  public TitleScreen(NeuroGame game, InputController gameController)
  {
    this.game = game;
    this.gameController = gameController;

    this.setLayout(null);

    sprites = Library.getSprites();

    // Get the images.
    backgroundImage = sprites.get("titleBackground");

    label_user = GUI_util.makeLabel30("User ", this);
    // dropDown_userList = new JComboBox<String>();
    // updateUserList();
    // dropDown_userList.setSelectedIndex(0);
    // this.add(dropDown_userList);
    // dropDown_userList.setFont(Library.FONT30);

    // Credits
    ArrayList<String> nameSorted = new ArrayList<String>();
    nameSorted.addAll(Arrays.asList(Names));
    Collections.shuffle(nameSorted);

    creditNameString = "Created By: ";
    for (int i = 0; i < nameSorted.size(); i++)
    {
      creditNameString += "   " + nameSorted.get(i) + "   ";
    }

    // // Configure Button
    // buttonList[BUTTON_CONFIG_IDX] = GUI_util.makeButton("Configuration...",
    // this, this);
    // buttonList[BUTTON_CONFIG_IDX].setEnabled(false);

    // Start Button
    button_start = GUI_util.makeButton("Start Game", this, this);

    buttonSelectedIdx = BUTTON_START_IDX;

    // Oddball button
    button_oddball = GUI_util.makeButton("Oddball Test", this, this);

    // Bandit button
    button_bandit = GUI_util.makeButton("Gambling Parlor", this, this);

    // Exit button
    button_exit = GUI_util.makeButton("Exit", this, this);

    // Scrolling Credits
    label_creditNames = new JLabel(creditNameString);
    label_creditNames.setFont(Library.FONT20);
    label_creditNames.setForeground(Library.UNSELECTED_TEXT_COLOR);
    this.add(label_creditNames);

    // userPanel = new JPanel();
    // userPanel.setVisible(false);
    // userPanel.setLayout(null);
    // this.add(userPanel);

    // Border raisedbevel = BorderFactory.createRaisedBevelBorder();
    // Border loweredbevel = BorderFactory.createLoweredBevelBorder();
    // Border compound = BorderFactory.createCompoundBorder(raisedbevel,
    // loweredbevel);
    // userPanel.setBorder(compound);

    // Integer[] joystickIndexes =
    // { 0, 1, 2, 3 };
    //
    // dropDown_joystickX = new JComboBox<Integer>(joystickIndexes);
    // dropDown_joystickY = new JComboBox<Integer>(joystickIndexes);
    //
    // label_userName = GUI_util.makeLabel20("User Name ", userPanel);
    // label_controller = GUI_util.makeLabel20("Controller ", userPanel);
    // label_Xaxis = GUI_util.makeLabel20("X-Axis ", userPanel);
    // label_Yaxis = GUI_util.makeLabel20("Y-Axis ", userPanel);

    
    edit_userName = new JTextField();
    this.add(edit_userName);
    GUI_util.setSelected(edit_userName, true);

    label_userName = new JLabel();
    this.add(label_userName);
    GUI_util.setSelected(label_userName, false);
    
    setUser(null);

    // dropDownController = new JComboBox<String>();
    // dropDownController.addActionListener(this);
    //
    // but_configOK = new JButton("OK");
    // but_configCancel = new JButton("Cancel");
    //
    // userPanel.add(but_configOK);
    // userPanel.add(but_configCancel);
    //
    // but_configOK.addActionListener(this);
    // but_configCancel.addActionListener(this);
    // but_configOK.setFont(Library.FONT20);
    // but_configCancel.setFont(Library.FONT20);
    //
    // loggingBox = new JCheckBox("Logging");
    //
    // userPanel.add(nameInputField);
    // userPanel.add(loggingBox);
    //
    // userPanel.add(dropDownController);
    // userPanel.add(dropDown_joystickX);
    // userPanel.add(dropDown_joystickY);
    //
    //
    // loggingBox.setFont(Library.FONT20);
    //
    // dropDownController.setFont(Library.FONT20);
    // dropDown_joystickX.setFont(Library.FONT20);
    // dropDown_joystickY.setFont(Library.FONT20);
    //
    // joyTestPanel = new JoystickTestDrawPanel();
    // Border raisedetched =
    // BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
    // joyTestPanel.setBorder(raisedetched);
    // userPanel.add(joyTestPanel);

    this.addKeyListener(gameController);
    edit_userName.addKeyListener(gameController);

    // dropDown_userList.addActionListener(this);
    // dropDown_joystickX.addActionListener(this);
    // dropDown_joystickY.addActionListener(this);

  }

  public void resizeHelper(int width, int height)
  {
    this.setSize(width, height);

    FontMetrics fm30 = this.getFontMetrics(Library.FONT36);
    FontMetrics fm20 = this.getFontMetrics(Library.FONT20);

    int fontW30 = fm30.stringWidth("X");

    int left = 25;

    int menuItemWidth = Math.min(width - 2 * left, fontW30 * 18);

    int column1 = left + (width - 2 * left - menuItemWidth) / 2;

    int spaceW20 = fm20.stringWidth(" ");
    int leadingCreditSpaces = (menuItemWidth / spaceW20) - 14;
    String length = Integer.toString(leadingCreditSpaces + creditNameString.length());
    creditNameString = String.format("%" + length + "s", creditNameString);

    int fontH30 = fm30.getLeading() + fm30.getMaxAscent() + fm30.getMaxDescent();
    int boxH30 = fontH30 + 8;
    int rowH30 = boxH30 + 3;

    int top = 400;

    int row1 = top;
    int row2 = row1 + 2 * rowH30;
    int row3 = row2;// row2 + rowH30;
    int row4 = row3 + rowH30;
    int row5 = row4 + rowH30;
    int row6 = row5 + rowH30;

    int label_userWidth = fm30.stringWidth(label_user.getText());
    label_user.setBounds(column1, row1, label_userWidth, boxH30);
    // dropDown_userList.setBounds(column1 + label_userWidth, row1,
    // menuItemWidth - label_userWidth, boxH30);

    edit_userName.setBounds(column1 + label_userWidth, row1, menuItemWidth - label_userWidth, boxH30);
    label_userName.setBounds(column1 + label_userWidth, row1, menuItemWidth - label_userWidth, boxH30);
    // buttonList[BUTTON_CONFIG_IDX].setBounds(column1, row2, menuItemWidth,
    // boxH30);

    
    button_bandit.setBounds(column1, row3, menuItemWidth, boxH30);
    button_oddball.setBounds(column1, row4, menuItemWidth, boxH30);
    button_start.setBounds(column1, row5, menuItemWidth, boxH30);
    button_exit.setBounds(column1, row6, menuItemWidth, boxH30);

    int rowBottom = height - 2 * rowH30;
    int fullWidth = width - 2 * left;
    label_creditNames.setBounds(left, fullWidth, rowBottom, boxH30);

    // int userPanelLeft = 10;
    // int userPanelTop = 10;
    // int userPanelWidth = menuItemWidth;
    // int userPanelHeight = rowH30 * 7;
    // int userPanelColWidth = menuItemWidth - 2 * userPanelLeft;
    // int userCol1Width = fontW20 * 10;
    // int userCol2Left = userPanelLeft + userCol1Width + 2;
    // int userCol2Width = userPanelColWidth - userCol1Width - 2;
    // userPanel.setBounds(column1, row1, userPanelWidth, userPanelHeight);
    //
    // label_userName.setBounds(userPanelLeft, userPanelTop, userCol1Width,
    // boxH20);
    // nameInputField.setBounds(userCol2Left, userPanelTop, userCol2Width,
    // boxH20);
    //
    // label_controller.setBounds(userPanelLeft, userPanelTop + rowH20,
    // userCol1Width, boxH20);
    // dropDownController.setBounds(userCol2Left, userPanelTop + rowH20,
    // userCol2Width, boxH20);
    //
    // label_Xaxis.setBounds(userPanelLeft, userPanelTop + 2 * rowH20,
    // userCol1Width, boxH20);
    // dropDown_joystickX.setBounds(userCol2Left, userPanelTop + 2 * rowH20,
    // userCol2Width, boxH20);
    //
    // label_Yaxis.setBounds(userPanelLeft, userPanelTop + 3 * rowH20,
    // userCol1Width, boxH20);
    // dropDown_joystickY.setBounds(userCol2Left, userPanelTop + 3 * rowH20,
    // userCol2Width, boxH20);
    //
    // loggingBox.setBounds(userPanelLeft, userPanelTop + 5 * rowH20,
    // userCol1Width, boxH20);
    //
    // int butRow = userPanelHeight - 2 * rowH20;
    // int butAreaWidth = 2 * userCol1Width + fontW30;
    // int col1L = (userPanelWidth - butAreaWidth) / 2;
    // int col2L = col1L + userCol1Width + fontW30;
    // but_configOK.setBounds(col1L, butRow, userCol1Width, boxH30);
    // but_configCancel.setBounds(col2L, butRow, userCol1Width, boxH30);
    //
    // int joyBoxTop = userPanelTop + 4 * rowH20 + 10;
    // joyTestPanel.setLocation(col2L, joyBoxTop);

  }

  public void showTitleScreen()
  {
    backgroundImage = sprites.get("titleBackground");
    showComponents(true);

    buttonSelectedIdx = BUTTON_USER_IDX;
    tutorialNum = 0;
    this.setVisible(true);
    this.requestFocus();
    selectComponent(SELECT_CURRENT);
    
    this.repaint();
  }

  private void showComponents(boolean visible)
  {
    edit_userName.setVisible(false);
    label_user.setVisible(visible);
    label_userName.setVisible(visible);
    button_start.setVisible(visible);
    button_oddball.setVisible(visible);
    button_bandit.setVisible(visible);
    button_exit.setVisible(visible);
    label_creditNames.setVisible(visible);
    this.requestFocus();
  }

  // private void copyUserToGUI()
  // {
  // int idx = dropDown_userList.getSelectedIndex();
  // if (idx < 0)
  // {
  // dropDown_userList.setSelectedIndex(0);
  // }
  // User user = User.getUser(dropDown_userList.getSelectedIndex());
  // System.out.println("TitleScreen.copyUserToGUI(idx=" +
  // dropDown_userList.getSelectedIndex() + ") user=" + user);
  //
  // nameInputField.setText(user.getName());
  // loggingBox.setSelected(user.isLogging());
  //
  // String[] controllerList = readControllerList();
  // dropDownController.removeAllItems();
  // dropDownController.addItem("Keyboard (Arror Keys)");
  // for (int i = 0; i < controllerList.length; i++)
  // {
  // dropDownController.addItem(controllerList[i]);
  // if (user.getController().equals(controllerList[i]))
  // {
  // dropDownController.setSelectedIndex(i);
  // }
  // }
  //
  // dropDown_joystickX.setSelectedIndex(user.getControllerXAxis());
  // dropDown_joystickY.setSelectedIndex(user.getControllerYAxis());
  //
  // updateJoystickAxisDropdowns();
  // }

  // private void copyGUI_toUser()
  // {
  // String name = nameInputField.getText();
  // User user = User.getUser(name);
  // if (user == null) user = User.addUser(name);
  //
  // user.setLogging(loggingBox.isSelected());
  //
  // if (dropDownController.getSelectedIndex() != 0)
  // {
  // user.setController((String) dropDownController.getSelectedItem());
  // user.setControllerXAxis(dropDown_joystickX.getSelectedIndex());
  // user.setControllerYAxis(dropDown_joystickY.getSelectedIndex());
  // }
  // else
  // {
  // user.setController(InputController.JOYSTICK_NOT_CONNECTED);
  // }
  // // User.saveUsers();
  // updateUserList();
  // dropDown_userList.setSelectedItem(name);
  // gameController.setupJoystick(user);
  // }

  private void selectComponent(int code)
  {
    if (code == SELECT_DOWN) buttonSelectedIdx++;
    else if (code == SELECT_UP) buttonSelectedIdx--;

    if (buttonSelectedIdx < 0) buttonSelectedIdx = BUTTON_COUNT - 1;
    else if (buttonSelectedIdx >= BUTTON_COUNT) buttonSelectedIdx = 0;

    edit_userName.setVisible(false);
    label_userName.setVisible(true);
    GUI_util.setSelected(label_user, false);
    GUI_util.setSelected(button_start, false);
    GUI_util.setSelected(button_oddball, false);
    GUI_util.setSelected(button_bandit, false);
    GUI_util.setSelected(button_exit, false);

    if (buttonSelectedIdx == BUTTON_USER_IDX)
    {
      GUI_util.setSelected(label_user, true);
      label_userName.setVisible(false);
      edit_userName.setVisible(true);
      edit_userName.requestFocus();
      
    }
    else
    {
      this.requestFocus();
      if (buttonSelectedIdx == BUTTON_START_IDX) GUI_util.setSelected(button_start, true);
      else if (buttonSelectedIdx == BUTTON_ODDBALL_IDX) GUI_util.setSelected(button_oddball, true);
      else if (buttonSelectedIdx == BUTTON_BANDIT_IDX) GUI_util.setSelected(button_bandit, true);
      else if (buttonSelectedIdx == BUTTON_EXIT_IDX) GUI_util.setSelected(button_exit, true);
    }
  }

  public void doSelected()
  {
    // if (buttonSelectedIdx == BUTTON_CONFIG_IDX) toggleUserPanel();
    if (buttonSelectedIdx == BUTTON_START_IDX) advanceTutorial();
    else if (buttonSelectedIdx == BUTTON_ODDBALL_IDX)
    {
      startOddballGame();
    }
    else if (buttonSelectedIdx == BUTTON_BANDIT_IDX) startBanditGame();
    else if (buttonSelectedIdx == BUTTON_EXIT_IDX) game.quit();
  }

  // public void toggleUserPanel()
  // {
  // boolean showUserPanel = !userPanel.isVisible();
  //
  // label_user.setVisible(!showUserPanel);
  // dropDown_userList.setVisible(!showUserPanel);
  // //buttonList[BUTTON_CONFIG_IDX].setVisible(!showUserPanel);
  // buttonList[BUTTON_START_IDX].setVisible(!showUserPanel);
  // buttonList[BUTTON_ODDBALL_IDX].setVisible(!showUserPanel);
  // buttonList[BUTTON_BANDIT_IDX].setVisible(!showUserPanel);
  // buttonList[BUTTON_EXIT_IDX].setVisible(!showUserPanel);
  //
  // if (showUserPanel)
  // {
  // copyUserToGUI();
  // }
  //
  // userPanel.setVisible(showUserPanel);
  //
  // }

  // public String[] readControllerList()
  // {
  //
  // try
  // {
  // Controllers.create();
  // }
  // catch (Exception e)
  // {
  // e.printStackTrace();
  // System.exit(0);
  // }
  //
  // int count = Controllers.getControllerCount();
  // System.out.println(count + " Controllers Found");
  // String[] controllerList = new String[count];
  //
  // for (int i = 0; i < count; i++)
  // {
  // Controller controller = Controllers.getController(i);
  //
  // // System.out.println(controller.getName());
  // controllerList[i] = controller.getName();
  // // if (controller.getName().contains("Gamepad"))
  // // if (controller.getName().contains("Joystick"))
  // // {
  // // joystick = controller;
  // // System.out.println("Gamepad found at index " + i);
  // // break;
  // // }
  // }
  //
  // // if (joystick == null)
  // // {
  // // System.out.println("Gamepad not found");
  // // System.exit(0);
  // // }
  //
  // return controllerList;
  // }

  //
  // /**
  // * Called when the start button is activated
  // */
  // private void onStartButtonPress() {
  //
  // // User is selected
  //
  // //if (Library.getUser(userList.getSelectedIndex()-2) != null) {
  // frame.requestFocus();
  // lpane.setVisible(false);
  //
  // IsStarting = true;
  // //}
  // }
  //
  // private void onExitButtonPress() {
  // IsExiting = true;
  // }
  //

  private void startOddballGame()
  {
    System.out.println("TitleScreen.startOddballGame() ");

    game.startOddBall(selectedUser);
  }

  private void startBanditGame()
  {
    // game.startBandit(User.getUser(dropDown_userList.getSelectedIndex()));
    game.startBandit(selectedUser);
  }

  private void startGame()
  {
    game.startGame(selectedUser, true);
  }

  public void update(double deltaSec, int keycode)
  {

    // System.out.println(dropDown_userList.isPopupVisible());;
    // System.out.println("TitleScreen().update() dropDown_userList.getSelectedIndex()="+dropDown_userList.getSelectedIndex());
    if (tutorialNum > 0)
    {
      if (keycode == KeyEvent.VK_ESCAPE) startGame();
      else if (keycode == KeyEvent.VK_ENTER) advanceTutorial();
      //System.out.println("TitleScreen.update() keycode="+keycode);
    }
    else if (buttonSelectedIdx == BUTTON_USER_IDX)
    {
      if ((keycode == KeyEvent.VK_ENTER) || (keycode == KeyEvent.VK_DOWN))
      {
        setUser(edit_userName.getText());
        selectComponent(SELECT_DOWN);
      }
      else if (keycode == KeyEvent.VK_UP)
      {
        setUser(edit_userName.getText());
        selectComponent(SELECT_UP);
      }
    }
    else if (gameController.isPlayerPressingButton())
    {
      doSelected();
      // System.out.println("TitleScreen.keyReleased(code=ENTER");
      // if (buttonSelectedIdx > 0)
      // {
      // System.out.println("TitleScreen.keyReleased: buttonSelectedIdx"+buttonSelectedIdx);
      // buttonSelectedIdx = BUTTON_CONFIG_IDX;
      // this.selectComponent(SELECT_CURRENT);
      // }
    }
    else
    {
//      DirectionVector dir = gameController.getPlayerInputDirectionVector();
//      // System.out.println("TitleScreen.update() dir="+buttonSelectedIdx);
//      if (dir.y > 0.1) selectComponent(SELECT_DOWN);
//      else if (dir.y < -0.1) selectComponent(SELECT_UP);
      if (keycode == KeyEvent.VK_UP) selectComponent(SELECT_UP);
      else if (keycode == KeyEvent.VK_DOWN) selectComponent(SELECT_DOWN);
    }

    // //if (buttonSelectedIdx < 0) return;
    //
    // if (keyCode == KeyEvent.VK_UP) selectComponent(SELECT_UP);
    // else if (keyCode == KeyEvent.VK_DOWN) selectComponent(SELECT_DOWN);

    // if ((dropDown_userList.isPopupVisible() == false) &&
    // (userPanel.isVisible() == false))
    // {
    // this.requestFocus();
    // }

    // if (dropDown_userList.isPopupVisible() == false)
    // {
    // this.requestFocus();
    // }

//    if (buttonSelectedIdx < 0)
//    {
//      // if (!dropDown_userList.isPopupVisible())
//      // {
//      buttonSelectedIdx = BUTTON_START_IDX;// BUTTON_CONFIG_IDX;
//      selectComponent(SELECT_CURRENT);
//      // }
//    }

    // if (userPanel.isVisible())
    // {
    // DirectionVector dir = gameController.getPlayerInputDirectionVector();
    // joyTestPanel.updateJoy(dir);
    // joyTestPanel.repaint();
    //
    // }

    creditIndex += 5 * deltaSec;

    if (creditIndex > creditNameString.length() - 1) creditIndex = 0;

    label_creditNames.setText(creditNameString.substring((int) creditIndex));

    repaint();

  }

  // private void updateUserList()
  // {
  // dropDown_userList.removeAllItems();
  // ArrayList<User> userList = User.getUserList();
  // for (User user : userList)
  // {
  // dropDown_userList.addItem(user.getName());
  // }
  // dropDown_userList.addItem("Add User...");
  // }

  // private void updateJoystickAxisDropdowns()
  // {
  // if (dropDownController.getSelectedIndex() == 0)
  // {
  // dropDown_joystickX.setEnabled(false);
  // dropDown_joystickY.setEnabled(false);
  // label_Xaxis.setEnabled(false);
  // label_Yaxis.setEnabled(false);
  // joyTestPanel.setVisible(false);
  // }
  // else
  // {
  // dropDown_joystickX.setEnabled(true);
  // dropDown_joystickY.setEnabled(true);
  // label_Xaxis.setEnabled(true);
  // label_Yaxis.setEnabled(true);
  // joyTestPanel.setVisible(true);
  // }
  // }

  private void advanceTutorial()
  {
    tutorialNum++;
    if (tutorialNum == 1)
    {
      showComponents(false);
      backgroundImage = sprites.get("tutControls");
    }
    else if (tutorialNum == 2)
    {
      backgroundImage = sprites.get("tutEnemy");
    }
    else if (tutorialNum == 3)
    {
      backgroundImage = sprites.get("tutMissilesLeft");
    }
    else if (tutorialNum == 4)
    {
      backgroundImage = sprites.get("tutStar");
    }
    else if (tutorialNum == 5)
    {
      backgroundImage = sprites.get("tutHealthMeter");
    }
    else if (tutorialNum == 6)
    {
      backgroundImage = sprites.get("tutStart");
    }
    else
    {
      startGame();
    }
  }

  private void setUser(String userName)
  {
    // int idx = dropDown_userList.getSelectedIndex();
    // if (idx < 0) return;
    if (userName == null) selectedUser = User.getUser(0);
    else
    { 
      userName = userName.trim();
      if (userName.isEmpty()) selectedUser = User.getUser(0);
      else
      {
        selectedUser = User.getUser(userName);
        if (selectedUser == null)
        {
          selectedUser = User.addUser(userName);
        }
      }
    }
    gameController.setupJoystick(selectedUser);
    edit_userName.setText(selectedUser.getName());
    edit_userName.setCaretPosition(selectedUser.getName().length());
    label_userName.setText(selectedUser.getName());
  }

  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    g.drawImage(this.backgroundImage, 0, 0, this);
  }

  public void actionPerformed(ActionEvent event)
  {

    System.out.println("TitleScreen.actionPerformed()");
    Object source = event.getSource();
    if (source == button_start) advanceTutorial();
    else if (source == button_exit) game.quit();
    // else if (source == buttonList[BUTTON_CONFIG_IDX]) toggleUserPanel();
    else if (source == button_oddball) startOddballGame();
    else if (source == button_bandit) startBanditGame();
    // else if (source == but_configOK)
    // {
    // copyGUI_toUser();
    // toggleUserPanel();
    // }
    // else if (source == but_configCancel) toggleUserPanel();
    // else if (source == dropDownController) updateJoystickAxisDropdowns();

    // System.out.println("TitleScreen.actionPerformed()....selected="+dropDown_userList.getSelectedIndex()+",  visible="+dropDown_userList.isPopupVisible());
    //

    // else if (source == dropDown_userList) setUser();

    // else if (source == dropDown_joystickX || source == dropDown_joystickY)
    // {
    // copyGUI_toUser();
    // }

  }

  // class JoystickTestDrawPanel extends JPanel
  // {
  //
  // // private Graphics2D canvasObjectLayer;
  // // private BufferedImage imageObjectLayer;
  //
  // public static final int joyBoxWidth = 100;
  // public static final int joyBoxHeight = 100;
  // private static final int centerX = joyBoxWidth / 2;
  // private static final int centerY = joyBoxHeight / 2;
  // private int joyX, joyY;

  // public JoystickTestDrawPanel()
  // {
  // setSize(joyBoxWidth, joyBoxHeight);
  // }
  //
  // public void updateJoy(DirectionVector dir)
  // {
  // joyX = centerX + (int) (centerX * dir.x);
  // joyY = centerY + (int) (centerY * dir.y);
  //
  // // System.out.println("TitleScreen.updateJoy()=(" + joyX + ", "+joyY+")");
  // }
  //
  // public void paintComponent(Graphics g)
  // {
  // // g.drawImage(imageObjectLayer, 0, 0, null);
  // g.setColor(Color.WHITE);
  // g.fillRect(0, 0, joyBoxWidth, joyBoxHeight);
  // g.setColor(Color.BLUE);
  // g.drawLine(centerX, centerY, joyX, joyY);
  // g.fillOval(joyX - 5, joyY - 5, 10, 10);
  // }
  // }
}