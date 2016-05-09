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
        setPanel(new UserLoginPanel(false));
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
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
