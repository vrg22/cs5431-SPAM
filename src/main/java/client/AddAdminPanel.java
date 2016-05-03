
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class AddAdminPanel extends JPanel {
    public AddAdminPanel() {
        JLabel nameLabel = new JLabel();
        nameLabel.setText("Admin Username:");
        JTextField nameField = new JTextField();

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("Admin Master Password:");
        JPasswordField passwordField = new JPasswordField(10);

        JButton save = new JButton();
        save.setText("Create Admin");

        JLabel errorLabel = new JLabel();
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = nameField.getText();
                String password = passwordField.getText();
                AdminFrame frame = AdminFrame.getFrameForComponent(save);
                boolean success = frame.getApp().register(username, password);
                //TODO: Handle case where max admins already WHERE??

                if (success) {
                    Admin.Header[] admins = frame.getApp().getAdmins();
                    frame.setPanel(new ShowAdminsPanel(admins));
                } else {
                    errorLabel.setText("Sorry there was a problem saving.");
                }
            }
        });

        JButton back = new JButton();
        back.setText("Back");
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	AdminFrame frame = AdminFrame.getFrameForComponent(back);
                Admin.Header[] admins = frame.getApp().getAdmins();
                frame.setPanel(new ShowAdminsPanel(admins));
            }
        });

        add(back);
        add(new JPanel());
        add(nameLabel);
        add(nameField);
        add(passwordLabel);
        add(passwordField);
        add(save);
        add(new JPanel());
        add(errorLabel);
        add(new JPanel());
        setLayout(new GridLayout(5, 2));
    }
}
