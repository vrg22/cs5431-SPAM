import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ClientFrame extends JFrame
{
    private ClientApplication app;

    public ClientFrame(ClientApplication app) {
        this.app = app;
    }

    public void start()
    {
        setPanel(new LoginPanel());
        setLayout(new FlowLayout());
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void setPanel(JPanel newView) {
        getContentPane().removeAll();
        getContentPane().add(newView);
        pack();
    }

    public ClientApplication getApp() {
        return app;
    }

    public static ClientFrame getFrameForComponent(Component c) {
        return (ClientFrame)SwingUtilities.getWindowAncestor(c);
    }
}
