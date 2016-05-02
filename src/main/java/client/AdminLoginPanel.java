import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

//Allow to escalate to admin-management mode, or log in as an admin to view logs
public class AdminLoginPanel extends JPanel {
    public AdminLoginPanel(boolean expired) {
        JLabel emailLabel = new JLabel();
        emailLabel.setText("Email:");
        JTextField emailField = new JTextField();

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("Master Password:");
        JPasswordField passwordField = new JPasswordField(10);

        JLabel twoFactorLabel = new JLabel();
        twoFactorLabel.setText("Two-Factor Code:");
        JPasswordField twoFactorField = new JPasswordField(6);

        JButton login = new JButton();
        login.setText("Login");

        JLabel errorLabel = new JLabel();
        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = passwordField.getText();
                String twoFactorCode = twoFactorField.getText();
                AdminFrame frame = AdminFrame.getFrameForComponent(login);
                boolean success = frame.getApp().login(email, password, twoFactorCode);

                if (success) {
                	frame.setPanel(new LogPanel());
                } else {
                    errorLabel.setText("Invalid email and/or password.");
                }
            }
        });

        JLabel superAdminLabel = new JLabel();
        superAdminLabel.setText("Admin Management Passphrase:");
        JTextField adminField = new JTextField();

        JButton superAdminMode = new JButton();
        superAdminMode.setText("Authenticate");

        superAdminMode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                String adminPassphrase = adminField.getText();
            	AdminFrame frame = AdminFrame.getFrameForComponent(superAdminMode);
                boolean success = frame.getApp().authManageAdmin(adminPassphrase);

                if (success) {
                	Admin.Header[] admins = frame.getApp().getAdmins();
                	frame.setPanel(new ShowAdminsPanel(admins));
                } else {
                    //TODO: Implement some sort of timeout for failure?
                    errorLabel.setText("Invalid admin passphrase.");
                }
            }
        });

        if (expired) {
            System.out.println("expired");
            JLabel expiredLabel = new JLabel();
            expiredLabel.setText("Your session has expired. Please log in again.");
            add(expiredLabel);
            add(new JPanel());
        }

        add(emailLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(twoFactorLabel);
        add(twoFactorField);
        add(superAdminLabel);
        add(adminField);
        add(superAdminMode);
        add(login);
        add(errorLabel);
        add(new JPanel());
        setLayout(new GridLayout(5 + (expired ? 1 : 0), 2));
    }
}
