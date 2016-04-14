import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ShowAccountPanel extends JPanel {
    public ShowAccountPanel(Account account) {
        JLabel nameLabel = new JLabel();
        nameLabel.setText("Name:");
        JTextField nameField = new JTextField();
        nameField.setText(account.getName());

        JLabel emailLabel = new JLabel();
        emailLabel.setText("Email:");
        JTextField emailField = new JTextField();
        emailField.setText(account.getUsername());

        JButton showPassword = new JButton();
        showPassword.setText("Show Password");
        JLabel passwordLabel = new JLabel();
        showPassword.addActionListener(new ActionListener(){
            boolean passwordShowing = false;
            public void actionPerformed(ActionEvent e) {
                if (!passwordShowing) {
                    passwordShowing = true;
                    showPassword.setText("Hide Password");
                    passwordLabel.setText(account.getPassword());
                } else {
                    passwordShowing = false;
                    showPassword.setText("Show Password");
                    passwordLabel.setText("");
                }
            }
        });

        JLabel changePasswordLabel = new JLabel();
        changePasswordLabel.setText("Change Password:");
        JPasswordField passwordField = new JPasswordField(10);
        passwordField.setText(account.getPassword());

        JButton generatePassword = new JButton();
        generatePassword.setText("Generate Secure Password");
        generatePassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                passwordField.setText(new ComplexPasswordGenerator().next(12));
            }
        });

        JButton save = new JButton();
        save.setText("Save Changes");

        JLabel errorLabel = new JLabel();
        save.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                ClientFrame frame = ClientFrame.getFrameForComponent(save);
                int id = account.getId();
                String name = nameField.getText();
                String username = emailField.getText();
                String password = passwordField.getText();
                Account updated = new Account(id, name, username, password);
                boolean success = frame.getApp().updateAccount(updated);

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
                ClientFrame frame = ClientFrame.getFrameForComponent(back);
                Account.Header[] accounts = frame.getApp().getAccounts();
                frame.setPanel(new ShowAccountsPanel(accounts));
            }
        });

        add(back);
        add(new JPanel());
        add(nameLabel);
        add(nameField);
        add(emailLabel);
        add(emailField);
        add(showPassword);
        add(passwordLabel);
        add(changePasswordLabel);
        add(passwordField);
        add(generatePassword);
        add(save);
        add(errorLabel);
        add(new JPanel());
        setLayout(new GridLayout(7, 2));
    }
}
