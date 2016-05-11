import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AllLogsPanel extends JPanel {

	public AllLogsPanel(String[] logNames) {

		//Create button display for all existing logNames
		loadLogButtons(logNames);

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


    //Load display with all the currently available logs, one button per log
    private void loadLogButtons(String[] logNames) {

    	// Some error retrieving log names that should NOT have happened, put that on Error label
    	if (logNames == null) {
        	setLayout(new GridLayout(2, 3));

            JLabel errorLabel = new JLabel();
            errorLabel.setText("Unknown error with log names.");

            add(errorLabel);
            add(new JPanel());
            add(new JPanel());

            return;
    	}

    	// Set Layout with one extra button
    	setLayout(new GridLayout(((int)Math.ceil(logNames.length / 3.0)) + 2, 3));

    	for (int i=0; i<logNames.length; i++) {

            JButton thisLog = new JButton();
            int logNo = i;
            thisLog.setText("Log " + logNo);

            thisLog.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    AdminFrame frame = AdminFrame.getFrameForComponent(thisLog);
                    String log = frame.getApp().getLogs()[logNo];
                    frame.setPanel(new ViewLogPanel(logNo, log));
                }
            });

            add(thisLog);
    	}

    	// Fill up remaining buttons in last row with empty panels
    	for (int i = logNames.length; i < 3 * ((int) Math.ceil(logNames.length / 3.0)); i++) {
            add(new JPanel());
    	}

    }
}
