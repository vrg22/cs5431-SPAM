import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class RegisterPanel extends JPanel {
    public RegisterPanel() {
        JLabel emailLabel = new JLabel();
        emailLabel.setText("Email:");
        JTextField emailField = new JTextField();

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("Master Password:");
        JPasswordField passwordField = new JPasswordField(10);

        JButton register = new JButton();
        register.setText("Register");

        register.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String email = emailField.getText();
                String password = passwordField.getText();
                ClientFrame frame = ClientFrame.getFrameForComponent(register);
                boolean success = frame.getApp().register(email, password);
                if (success) {
                    frame.setPanel(new VaultPanel());
                } else {
                    JLabel errorLabel = new JLabel();
                    errorLabel.setText("Sorry there was a problem.");
                    add(errorLabel);
                }
            }
        });

        add(emailLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(register);
        setLayout(new GridLayout(3, 2));
    }
}
