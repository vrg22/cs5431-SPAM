import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

//Allow to escalate to admin-management mode, or log in as an admin to view logs
public class AdminLoginPanel extends JPanel {
    public AdminLoginPanel() {
        JLabel nameLabel = new JLabel();
        nameLabel.setText("Username:");
        JTextField nameField = new JTextField();

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("Master Password:");
        JPasswordField passwordField = new JPasswordField(10);

        JButton login = new JButton();
        login.setText("Login");

        JLabel errorLabel = new JLabel();
        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = nameField.getText();
                String password = passwordField.getText();
                AdminFrame frame = AdminFrame.getFrameForComponent(login);
                boolean success = frame.getApp().login(email, password);

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
        
        add(superAdminLabel);
        add(nameLabel);
        add(adminField);
        add(nameField);
        add(superAdminMode);
        add(passwordLabel);
        add(new JPanel());
        add(passwordField);
        add(new JPanel());
        add(login);
        add(errorLabel);
        add(new JPanel());
        
        GridLayout origLayout = new GridLayout(6, 2);
        origLayout.setHgap(20);
        setLayout(origLayout);
    }
}
