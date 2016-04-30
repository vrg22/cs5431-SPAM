import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class UserFrame extends JFrame
{
    private UserApplication app;

    public UserFrame(UserApplication app) {
        this.app = app;
    }

    public void start()
    {
        setPanel(new UserLoginPanel());
        setLayout(new FlowLayout());
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void setPanel(JPanel newView) {
        getContentPane().removeAll();
        getContentPane().add(newView);
        pack();
    }

    public UserApplication getApp() {
        return app;
    }

    public static UserFrame getFrameForComponent(Component c) {
        return (UserFrame)SwingUtilities.getWindowAncestor(c);
    }
}
