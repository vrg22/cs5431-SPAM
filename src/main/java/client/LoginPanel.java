import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class LoginPanel extends JPanel {
    public LoginPanel() {
        JLabel emailLabel = new JLabel();
        emailLabel.setText("Email:");
        JTextField emailField = new JTextField();

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("Master Password:");
        JPasswordField passwordField = new JPasswordField(10);

        JButton login = new JButton();
        login.setText("Login");

        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = passwordField.getText();
                boolean success = ClientApplication.login(email, password);

                if (success) {
                    ClientFrame frame = ClientFrame.getFrameForComponent(login);
                    frame.setPanel(new VaultPanel());
                } else {
                    JLabel errorLabel = new JLabel();
                    errorLabel.setText("Invalid email and/or password.");
                    add(errorLabel);
                }
            }
        });

        JButton register = new JButton();
        register.setText("Register");

        register.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                ClientFrame frame = ClientFrame.getFrameForComponent(register);
                frame.setPanel(new RegisterPanel());
            }
        });

        add(emailLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(register);
        add(login);
        setLayout(new GridLayout(3, 2));
    }
}
