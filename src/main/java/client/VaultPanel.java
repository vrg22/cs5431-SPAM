import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class VaultPanel extends JPanel {
    public VaultPanel() {
        JButton view = new JButton();
        view.setText("View/Edit My Stored Accounts");

        view.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                ClientApplication app = ClientApplication.getFrameForComponent(view);
                app.setPanel(new ShowAccountsPanel());
            }
        });

        JButton create = new JButton();
        create.setText("Store New Account");

        create.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                ClientApplication app = ClientApplication.getFrameForComponent(create);
                app.setPanel(new StoreNewAccountPanel());
            }
        });

        add(view);
        add(create);
        setLayout(new FlowLayout());
    }
}
