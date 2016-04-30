import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class UserRegisterPanel extends JPanel {
	public UserRegisterPanel() {
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
                UserFrame frame = UserFrame.getFrameForComponent(register);
                boolean success = frame.getApp().register(email, password);
                if (success) {
                    frame.setPanel(new VaultPanel());
                } else {
                    errorLabel.setText("Sorry there was a problem registering.");
                }
            }
        });

        add(emailLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(register);
        add(new JPanel());
        add(errorLabel);
        add(new JPanel());
        setLayout(new GridLayout(4, 2));
    }
}
