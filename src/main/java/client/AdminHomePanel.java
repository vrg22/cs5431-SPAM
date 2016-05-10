import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AdminHomePanel extends JPanel {

    public AdminHomePanel() {
    	
        JLabel errorLabel = new JLabel();

        JButton view = new JButton();
        view.setText("View Recent Logs");
    
    
        view.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                AdminFrame frame = AdminFrame.getFrameForComponent(view);
            	boolean success = frame.getApp().retrieveLogs();
            	if (success) {
                	String[] logNames = frame.getApp().getLogNames();
                	frame.setPanel(new AllLogsPanel(logNames));
                } else {
                    errorLabel.setText("Invalid admin passphrase.");
                }
            }
        });
                
        JButton logout = new JButton();
        logout.setText("Logout");
    
        logout.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                AdminFrame frame = AdminFrame.getFrameForComponent(logout);
                frame.getApp().logout();
                frame.setPanel(new AdminLoginPanel());
            }
        });
        
        add(view);
        add(logout);
        add(errorLabel);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }
    
}
