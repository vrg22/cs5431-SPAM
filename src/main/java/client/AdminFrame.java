import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class AdminFrame extends JFrame
{
    private AdminApplication app;

    public AdminFrame(AdminApplication app) {
        this.app = app;
    }

    public void start()
    {
        setPanel(new AdminLoginPanel());
    	//setPanel(new AdminLoginPanel(false));
        setLayout(new FlowLayout());
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void setPanel(JPanel newView) {
        getContentPane().removeAll();
        getContentPane().add(newView);
        pack();
    }

    public AdminApplication getApp() {
        return app;
    }

    public static AdminFrame getFrameForComponent(Component c) {
        return (AdminFrame)SwingUtilities.getWindowAncestor(c);
    }
}
