import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class LogPanel extends JPanel {

    public LogPanel() {
        JButton view = new JButton();
        view.setText("View Recent Logs");
    
    
        view.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                AdminFrame frame = AdminFrame.getFrameForComponent(view);
                //frame.setPanel(???);
            }
        });
        
        add(view);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }
    
}
