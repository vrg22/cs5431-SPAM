import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import javax.swing.event.*;

//Modified to handle both user and admin login attempts
public class UserLoginPanel extends JPanel {
    public UserLoginPanel(boolean expired) {
        setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel();
        titleLabel.setText("Welcome to SPAM");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 18));
        GridBagConstraints titleLabelConstraints = new GridBagConstraints(0, 0, 3, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        add(titleLabel, titleLabelConstraints);

        JLabel emailLabel = new JLabel();
        emailLabel.setText("Email:");
        GridBagConstraints emailLabelConstraints = new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        add(emailLabel, emailLabelConstraints);


        JTextField emailField = new JTextField();
        GridBagConstraints emailFieldConstraints = new GridBagConstraints(1, 3, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        add(emailField, emailFieldConstraints);

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("Master Password:");
        GridBagConstraints passwordLabelConstraints = new GridBagConstraints(0, 4, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        add(passwordLabel, passwordLabelConstraints);

        JPasswordField passwordField = new JPasswordField(10);
        GridBagConstraints passwordFieldConstraints = new GridBagConstraints(1, 4, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        add(passwordField, passwordFieldConstraints);

        JLabel twoFactorLabel = new JLabel();
        twoFactorLabel.setText("6-digit Two-Factor Code:");
        GridBagConstraints twoFactorLabelConstraints = new GridBagConstraints(0, 5, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        add(twoFactorLabel, twoFactorLabelConstraints);

        JPasswordField twoFactorField = new JPasswordField(6);
        GridBagConstraints twoFactorFieldConstraints = new GridBagConstraints(1, 5, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        add(twoFactorField, twoFactorFieldConstraints);

        JButton login = new JButton();
        login.setText("Login");
        login.setEnabled(false);
        GridBagConstraints loginConstraints = new GridBagConstraints(1, 6, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        add(login, loginConstraints);

        JLabel errorLabel = new JLabel();
        GridBagConstraints errorLabelConstraints = new GridBagConstraints(0, 7, 3, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        add(errorLabel, errorLabelConstraints);
        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = passwordField.getText();
                String twoFactorCode = twoFactorField.getText();

                // Check if form has all fields filled up
                if (email.equals("") || password.equals("") || twoFactorCode.equals("")) {
                    errorLabel.setText("Please fill all login information");
                    return;
                }

                UserFrame frame = UserFrame.getFrameForComponent(login);
                boolean success = frame.getApp().login(email, password, twoFactorCode);

                if (success) {
                	frame.setPanel(new VaultPanel());
                } else {
                    errorLabel.setText("Invalid email and/or password.");
                }
            }
        });

        JButton register = new JButton();
        register.setText("New User? Go Here");
        GridBagConstraints registerConstraints = new GridBagConstraints(0, 2, 3, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        add(register, registerConstraints);

        register.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
            	UserFrame frame = UserFrame.getFrameForComponent(register);
                frame.setPanel(new UserRegisterPanel());
            }
        });

        if (expired) {
            System.out.println("expired");
            JLabel expiredLabel = new JLabel();
            expiredLabel.setText("Your session has expired. Please log in again.");
            GridBagConstraints expiredLabelConstraints = new GridBagConstraints(0, 8, 3, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
            add(expiredLabel, expiredLabelConstraints);
        }

        JButton forgot = new JButton();
        forgot.setText("Forgot Password?");
        GridBagConstraints forgotConstraints = new GridBagConstraints(0, 8, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        add(forgot, forgotConstraints);

        forgot.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                UserFrame frame = UserFrame.getFrameForComponent(forgot);
                frame.setPanel(new ForgotPasswordPanel());
            }
        });

        twoFactorField.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent ce) {
                String email = emailField.getText();
                String password = passwordField.getText();
                String twoFactorCode = twoFactorField.getText();
                if (email.equals("") || password.equals("") || twoFactorCode.equals("")) {
                    login.setEnabled(false);
                } else {
                    login.setEnabled(true);
                }
            }
        });
    }
}
