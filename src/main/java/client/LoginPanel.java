import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class LoginPanel extends JPanel {
    public LoginPanel(boolean expired) {
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
                ClientFrame frame = ClientFrame.getFrameForComponent(login);
                boolean success = frame.getApp().login(email, password);

                if (success) {
                    frame.setPanel(new VaultPanel());
                } else {
                    errorLabel.setText("Invalid email and/or password.");
                }
            }
        });

        JButton register = new JButton();
        register.setText("New User?");

        register.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                ClientFrame frame = ClientFrame.getFrameForComponent(register);
                frame.setPanel(new RegisterPanel());
            }
        });

        if (expired) {
            System.out.println("expired");
            JLabel expiredLabel = new JLabel();
            expiredLabel.setText("Your session has expired. Please log in again.");
            add(expiredLabel);
            add(new JPanel());
        }

        JButton forgot = new JButton();
        forgot.setText("Forgot password");

        forgot.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                ClientFrame frame = ClientFrame.getFrameForComponent(forgot);
                frame.setPanel(new ForgotPasswordPanel());
            }
        });

        add(emailLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(register);
        add(login);
        add(forgot);
        add(errorLabel);
        add(new JPanel());
        setLayout(new GridLayout(5 + (expired ? 1 : 0), 2));
    }
}
