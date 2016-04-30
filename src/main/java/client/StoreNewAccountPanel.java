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
        passwordLabel.setText("Password:");
        JPasswordField passwordField = new JPasswordField(10);

        JButton save = new JButton();
        save.setText("Save");

        JLabel errorLabel = new JLabel();
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String username = emailField.getText();
                String password = passwordField.getText();
                UserFrame frame = UserFrame.getFrameForComponent(save);
                boolean success = frame.getApp().storeNewAccount(name,
                    username, password);

                if (success) {
                    Account.Header[] accounts = frame.getApp().getAccounts();
                    frame.setPanel(new ShowAccountsPanel(accounts));
                } else {
                    errorLabel.setText("Sorry there was a problem saving.");
                }
            }
        });

        JButton back = new JButton();
        back.setText("Back");
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	UserFrame frame = UserFrame.getFrameForComponent(back);
                frame.setPanel(new VaultPanel());
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
        add(errorLabel);
        add(new JPanel());
        setLayout(new GridLayout(6, 2));
    }
}
