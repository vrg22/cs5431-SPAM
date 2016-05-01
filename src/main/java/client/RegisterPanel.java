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

        JLabel errorLabel = new JLabel();
        register.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String email = emailField.getText();
                String password = passwordField.getText();
                ClientFrame frame = ClientFrame.getFrameForComponent(register);
                boolean success = frame.getApp().register(email, password);
                if (success) {
                    frame.setPanel(new VaultPanel());
                } else {
                    errorLabel.setText("Sorry there was a problem registering.");
                }
            }
        });

		JButton back = new JButton();
        back.setText("Back");

        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ClientFrame frame = ClientFrame.getFrameForComponent(back);
                frame.setPanel(new LoginPanel());
            }
        });

        add(emailLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(register);
        add(back);
        add(new JPanel());
        add(errorLabel);
        add(new JPanel());
        setLayout(new GridLayout(6, 2));
    }
}
