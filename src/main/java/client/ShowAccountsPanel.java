import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ShowAccountsPanel extends JPanel {
    public ShowAccountsPanel(Account.Header[] accounts) {
        JButton back = new JButton();
        back.setText("Back");
        add(back);
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ClientFrame frame = ClientFrame.getFrameForComponent(back);
                frame.setPanel(new VaultPanel());
            }
        });
        add(new JPanel());

        for (Account.Header account : accounts) {
            JLabel nameLabel = new JLabel();
            nameLabel.setText(account.getName());
            add(nameLabel);

            JButton view = new JButton();
            view.setText("View/Edit");
            add(view);

            view.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    Account fullAccount = ClientApplication.getAccount(account.getId());
                    ClientFrame frame = ClientFrame.getFrameForComponent(view);
                    frame.setPanel(new ShowAccountPanel(fullAccount));
                }
            });
        }
        if (accounts.length == 0) {
            JLabel emptyLabel = new JLabel();
            emptyLabel.setText("You have not stored any accounts yet.");
            add(emptyLabel);
            add(new JPanel());
        }

        setLayout(new GridLayout(accounts.length + 1, 2));
    }
}
