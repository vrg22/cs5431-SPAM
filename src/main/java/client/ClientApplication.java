import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ClientApplication extends JFrame
{
    public static final String HTTPS_ROOT = "https://spam3.kevinmbeaulieu.com";
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

    public static ClientApplication getFrameForComponent(Component c) {
        return (ClientApplication)SwingUtilities.getWindowAncestor(c);
    }

    public static void main(String args[])
    {
        new ClientApplication().start();
    }
}
