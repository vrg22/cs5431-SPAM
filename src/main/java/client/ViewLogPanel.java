import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ViewLogPanel extends JPanel {
    
	public ViewLogPanel(int logId, String log) {
		
        setLayout(new GridLayout(3, 2)); //CHECK
        
		//Display with the log text
		JTextArea textArea = new JTextArea(20, 45);
    	textArea.setText(log);
		JScrollPane scrollPane = new JScrollPane(textArea);
		textArea.setEditable(false);
		
		//Back to all logs
		JButton back = new JButton();
        back.setText("Back");
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	AdminFrame frame = AdminFrame.getFrameForComponent(back);
                String[] logNames = frame.getApp().getLogNames();
                frame.setPanel(new AllLogsPanel(logNames));
            }
        });
        
        JLabel errorLabel = new JLabel();
        
        JButton delete = new JButton();
        delete.setText("Delete");
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	AdminFrame frame = AdminFrame.getFrameForComponent(back);
                boolean success = frame.getApp().deleteLog(logId);
                if (success) {
                	boolean retrieved = frame.getApp().retrieveLogs();
                	if (retrieved) {
                        String[] logNames = frame.getApp().getLogNames();
                    	frame.setPanel(new AllLogsPanel(logNames));
                	} else {
                        errorLabel.setText("Log deleted, but unknown error retrieving latest logs.");
                	}
                } else {
                    errorLabel.setText("Unknown error deleting logs.");
                }
            }
        });
        
        add(scrollPane);
        add(new JPanel());
        add(back);
        add(delete);
        add(errorLabel);
	}
	
}
