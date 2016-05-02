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

        JLabel recoveryQuestion1 = new JLabel();
        recoveryQuestion1.setText("Recovery Question1:");
        JPasswordField recoveryAnswer1 = new JPasswordField(10);

        JLabel recoveryQuestion2 = new JLabel();
        recoveryQuestion2.setText("Recovery Question2:");
        JPasswordField recoveryAnswer2 = new JPasswordField(10);

        JLabel recoveryQuestion3 = new JLabel();
        recoveryQuestion3.setText("Recovery Question3:");
        JPasswordField recoveryAnswer3 = new JPasswordField(10);

        JButton register = new JButton();
        register.setText("Register");

        JLabel errorLabel = new JLabel();
        register.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String email = emailField.getText();
                String password = passwordField.getText();
				String recovery1 = recoveryAnswer1.getText();
				String recovery2 = recoveryAnswer2.getText();
				String recovery3 = recoveryAnswer3.getText();
				String recovery = recovery1 + recovery2 + recovery3;
                ClientFrame frame = ClientFrame.getFrameForComponent(register);
                boolean success = frame.getApp().register(email, password, recovery);
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
                frame.setPanel(new LoginPanel(false));
            }
        });

        add(emailLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(recoveryQuestion1);
        add(recoveryAnswer1);
        add(recoveryQuestion2);
        add(recoveryAnswer2);
        add(recoveryQuestion3);
        add(recoveryAnswer3);
        add(register);
        add(back);
        add(new JPanel());
        add(errorLabel);
        add(new JPanel());
        setLayout(new GridLayout(8, 2));
    }
}
