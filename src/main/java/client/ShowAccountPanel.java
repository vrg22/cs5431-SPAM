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

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("Master Password:");
        JPasswordField passwordField = new JPasswordField(10);
        passwordField.setText(account.getPassword());

        JButton save = new JButton();
        save.setText("Save Changes");

        save.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                ClientFrame frame = ClientFrame.getFrameForComponent(save);
                int id = account.getId();
                String name = nameField.getText();
                String username = emailField.getText();
                String password = passwordField.getText();
                Account updated = new Account(id, name, username, password);
                boolean success = frame.getApp().updateAccount(updated);

                Account.Header[] accounts = frame.getApp().getAccounts();
                frame.setPanel(new ShowAccountsPanel(accounts));
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
        add(passwordLabel);
        add(passwordField);
        add(save);
        add(new JPanel());
        setLayout(new GridLayout(5, 2));
    }
}
