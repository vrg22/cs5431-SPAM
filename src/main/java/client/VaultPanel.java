import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class VaultPanel extends JPanel {
    public VaultPanel() {
        JButton view = new JButton();
        view.setText("View/Edit My Stored Accounts");

        view.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                ClientFrame frame = ClientFrame.getFrameForComponent(view);
                frame.setPanel(new ShowAccountsPanel());
            }
        });

        JButton create = new JButton();
        create.setText("Store New Account");

        create.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                ClientFrame frame = ClientFrame.getFrameForComponent(create);
                frame.setPanel(new StoreNewAccountPanel());
            }
        });

        add(view);
        add(create);
        setLayout(new FlowLayout());
    }
}
