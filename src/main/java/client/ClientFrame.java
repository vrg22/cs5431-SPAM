import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ClientFrame extends JFrame
{
    public void start()
    {
        setPanel(new LoginPanel());
        setLayout(new FlowLayout());
        setSize(600,600);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void setPanel(JPanel newView) {
        getContentPane().removeAll();
        getContentPane().add(newView);
        pack();
    }

    public static ClientFrame getFrameForComponent(Component c) {
        return (ClientFrame)SwingUtilities.getWindowAncestor(c);
    }
}
