import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ViewLogPanel extends JPanel {
    
	public ViewLogPanel(String log) {
		
        setLayout(new GridLayout(3, 1)); //CHECK
		
		//Display with the log text
		JTextArea textArea = new JTextArea(30, 70); //CHECK
		textArea.setText(log);
		JScrollPane scrollPane = new JScrollPane(textArea);
		textArea.setEditable(false);
		
		//Back to all logs
		JButton back = new JButton();
        back.setText("Back");
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	AdminFrame frame = AdminFrame.getFrameForComponent(back);
                String[] logs = frame.getApp().getLogs(); //TODO: CHANGE!!!
                frame.setPanel(new AllLogsPanel(logs));
            }
        });
        
        add(scrollPane);
        add(back);
	}
	
}
