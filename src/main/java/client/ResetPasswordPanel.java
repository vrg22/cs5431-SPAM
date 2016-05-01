import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.security.cert.Certificate;
import java.io.*;

public class ResetPasswordPanel extends JPanel {
    public ResetPasswordPanel() {
        JLabel emailLabel = new JLabel();
        emailLabel.setText("Email:");
        JTextField emailField = new JTextField();

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("Current Master Password:");
        JPasswordField passwordField = new JPasswordField(10);

        JLabel newPasswordLabel1 = new JLabel();
        newPasswordLabel1.setText("New Master Password:");
        JPasswordField newPasswordField1 = new JPasswordField(10);

        JLabel newPasswordLabel2 = new JLabel();
        newPasswordLabel2.setText("Confirm New Master Password:");
        JPasswordField newPasswordField2 = new JPasswordField(10);

        JButton reset = new JButton();
        reset.setText("Reset");

        JLabel errorLabel = new JLabel();
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = passwordField.getText();
                String newPassword1 = newPasswordField1.getText();
                String newPassword2 = newPasswordField2.getText();
                if (newPassword1.equals(newPassword2)) {
                  ClientFrame frame = ClientFrame.getFrameForComponent(reset);
                  boolean success = frame.getApp().resetPass(email, password, newPassword1);

                  if (success) {
                    //frame.setPanel(new VaultPanel());
                    errorLabel.setText("Successfully reset password");
                  } else {
                    errorLabel.setText("Invalid email and/or password.");
                  }
                }
            }
        });

        add(emailLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(newPasswordLabel1);
        add(newPasswordField1);
        add(newPasswordLabel2);
        add(newPasswordField2);
        add(reset);
        add(errorLabel);
        add(new JPanel());
        setLayout(new GridLayout(6, 2));
    }
}
