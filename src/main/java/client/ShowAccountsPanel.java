import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ShowAccountsPanel extends JPanel {
    public ShowAccountsPanel() {
        // TODO: get accounts for user
        Account[] accounts = new Account[] {};

        JButton back = new JButton();
        back.setText("Back");
        add(back);
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ClientApplication app = ClientApplication.getFrameForComponent(back);
                app.setPanel(new VaultPanel());
            }
        });
        add(new JPanel());

        for (Account account : accounts) {
            JLabel nameLabel = new JLabel();
            nameLabel.setText(account.getName());
            add(nameLabel);

            JButton view = new JButton();
            view.setText("View/Edit");
            add(view);

            view.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    ClientApplication app = ClientApplication.getFrameForComponent(view);
                    app.setPanel(new ShowAccountPanel());
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
