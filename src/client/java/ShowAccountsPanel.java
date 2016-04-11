import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ShowAccountsPanel extends JPanel {
    public ShowAccountsPanel(Account[] accounts) {
        for (Account account : accounts) {
            JLabel nameLabel = new JLabel();
            nameLabel.setText(account.getName());
            add(nameLabel);

            JButton view = new JButton();
            view.setText("View/Edit");
            add(view);

            view.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

                    // TODO: go to view/edit account page for this account
                }
            });
        }

        setLayout(new GridLayout(accounts.length, 2));
    }
}
