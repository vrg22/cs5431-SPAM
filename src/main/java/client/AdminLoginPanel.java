import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

//Modified to handle both user and admin login attempts
public class AdminLoginPanel extends JPanel {
    public AdminLoginPanel() {
        JLabel emailLabel = new JLabel();
        emailLabel.setText("Email:");
        JTextField emailField = new JTextField();

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("Master Password:");
        JPasswordField passwordField = new JPasswordField(10);

        JButton login = new JButton();
        login.setText("Login");

        JLabel errorLabel = new JLabel();
        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
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

        JButton register = new JButton();
        register.setText("Register");

        register.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
            	AdminFrame frame = AdminFrame.getFrameForComponent(register);
                frame.setPanel(new AdminRegisterPanel());
            }
        });

        add(emailLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(register);
        add(login);
        add(errorLabel);
        add(new JPanel());
        setLayout(new GridLayout(4, 2));
    }
}
