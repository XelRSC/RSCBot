import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class ClickButton {
  public static void main(String args[]) {
    JFrame frame = new JFrame("Button Sample");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JButton button = new JButton("Select Me");

    ActionListener actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        System.out.println("I was selected.");
      }
    };

    MouseListener mouseListener = new MouseAdapter() {
      public void mousePressed(MouseEvent mouseEvent) {
        int modifiers = mouseEvent.getModifiers();
        if ((modifiers & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
          // Mask may not be set properly prior to Java 2
          // See SwingUtilities.isLeftMouseButton() for workaround
          System.out.println("Left button pressed.");
        }
        if ((modifiers & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK) {
          System.out.println("Middle button pressed.");
        }
        if ((modifiers & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
          System.out.println("Right button pressed.");
        }
      }

      public void mouseReleased(MouseEvent mouseEvent) {
        if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
          System.out.println("Left button released.");
        }
        if (SwingUtilities.isMiddleMouseButton(mouseEvent)) {
          System.out.println("Middle button released.");
        }
        if (SwingUtilities.isRightMouseButton(mouseEvent)) {
          System.out.println("Right button released.");
        }
        System.out.println();
      }
    };

    button.addActionListener(actionListener);
    button.addMouseListener(mouseListener);

    Container contentPane = frame.getContentPane();
    contentPane.add(button, BorderLayout.SOUTH);
    frame.setSize(300, 100);
    frame.setVisible(true);
  }
}