import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class StoreNewAccountPanel extends JPanel {
    public StoreNewAccountPanel() {
        JLabel nameLabel = new JLabel();
        nameLabel.setText("Name:");
        JTextField nameField = new JTextField();

        JLabel emailLabel = new JLabel();
        emailLabel.setText("Email:");
        JTextField emailField = new JTextField();

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("Master Password:");
        JPasswordField passwordField = new JPasswordField(10);

        JButton save = new JButton();
        save.setText("Save");

        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String username = emailField.getText();
                String password = passwordField.getText();
                boolean success = ClientApplication.storeNewAccount(name,
                    username, password);

                if (success) {
                    Account.Header[] accounts = ClientApplication.getAccounts();
                    ClientFrame frame = ClientFrame.getFrameForComponent(save);
                    frame.setPanel(new ShowAccountsPanel(accounts));
                } else {
                    JLabel errorLabel = new JLabel();
                    errorLabel.setText("Sorry there was a problem.");
                    add(errorLabel);
                }
            }
        });

        JButton back = new JButton();
        back.setText("Back");
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Account.Header[] accounts = ClientApplication.getAccounts();
                ClientFrame frame = ClientFrame.getFrameForComponent(back);
                frame.setPanel(new ShowAccountsPanel(accounts));
            }
        });

        add(back);
        add(new JPanel());
        add(nameLabel);
        add(nameField);
        add(emailLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(save);
        add(new JPanel());
        setLayout(new GridLayout(5, 2));
    }
}
