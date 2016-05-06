import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AllLogsPanel extends JPanel {

	public AllLogsPanel(String[] logs) {
		
		//Create button display for all existing logNames
		loadLogs(logs);
		
		// Logout button
        JButton logout = new JButton();
        logout.setText("Logout");
    
        logout.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                AdminFrame frame = AdminFrame.getFrameForComponent(logout);
                frame.getApp().logout();
                frame.setPanel(new AdminLoginPanel());
                //frame.setPanel(new AdminLoginPanel(true));
            }
        });
        
        add(logout);
        //setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); //CHECK
    }
	
    
    //Load display with all the currently available logs, one button per file (?)
    private void loadLogs(String[] logs) {
    	
    	// Some error retrieving log files, put that on Error label
    	if (logs == null) {
        	setLayout(new GridLayout(2, 3));

            JLabel errorLabel = new JLabel();
            errorLabel.setText("Error retrieving logs.");
            
            add(errorLabel);
            add(new JPanel());
            add(new JPanel());
            
            return;
    	}
    	
    	// Set Layout with one extra button 
    	setLayout(new GridLayout((int) (Math.ceil(logs.length / 3)) + 1, 3));
    	
    	for (int i=0; i<logs.length; i++) {
    		
    		String log = logs[i];
            JButton thisLog = new JButton();
            thisLog.setText("Log " + i);
            
            thisLog.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    AdminFrame frame = AdminFrame.getFrameForComponent(thisLog);
                    frame.setPanel(new ViewLogPanel(log));
                }
            });
            
            add(thisLog);
    	}
    	
    	// Fill up remaining buttons in last row with empty panels
    	for (int i = logs.length; i < 3 * ((int) Math.ceil(logs.length / 3.0)); i++) {
            add(new JPanel());
    	}
    	    	
    }
}