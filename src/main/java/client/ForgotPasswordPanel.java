import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import javax.swing.event.*;

public class ForgotPasswordPanel extends JPanel {
    public ForgotPasswordPanel() {
        JLabel emailLabel = new JLabel();
        emailLabel.setText("Email:");
        JTextField emailField = new JTextField();

        JLabel recoveryQuestion1 = new JLabel();
        recoveryQuestion1.setText("What is the name of your first pet?");
        JPasswordField recoveryAnswer1 = new JPasswordField(10);

        JLabel recoveryQuestion2 = new JLabel();
        recoveryQuestion2.setText("What is the hospital you were born in?");
        JPasswordField recoveryAnswer2 = new JPasswordField(10);

        JLabel recoveryQuestion3 = new JLabel();
        recoveryQuestion3.setText("What is the last name of your first grade teacher?");
        JPasswordField recoveryAnswer3 = new JPasswordField(10);

        JLabel twoFactorLabel = new JLabel();
        twoFactorLabel.setText("6-digit Two-Factor Code:");
        JPasswordField twoFactorField = new JPasswordField(6);

        JLabel newPasswordLabel1 = new JLabel();
        newPasswordLabel1.setText("New Master Password:");
		newPasswordLabel1.setForeground(Color.red);
        JPasswordField newPasswordField1 = new JPasswordField(10);

		newPasswordField1.addCaretListener(new CaretListener() {
		  public void caretUpdate(CaretEvent ce) {
			if (PasswordStrength.check(new String(newPasswordField1.getPassword()))) {
			  newPasswordLabel1.setForeground(Color.green);
			} else {
			  newPasswordLabel1.setForeground(Color.red);
			}
		  }
		});

        JLabel newPasswordLabel2 = new JLabel();
        newPasswordLabel2.setText("Confirm New Master Password:");
		newPasswordLabel2.setForeground(Color.red);
        JPasswordField newPasswordField2 = new JPasswordField(10);

        JButton recover = new JButton();
        recover.setText("Recover Password");
        recover.setEnabled(false);

        JLabel errorLabel = new JLabel();
        recover.addActionListener(new ActionListener() {
		  public void actionPerformed(ActionEvent e) {
			String email = emailField.getText();
			String recovery1 = recoveryAnswer1.getText();
			String recovery2 = recoveryAnswer2.getText();
			String recovery3 = recoveryAnswer3.getText();
			String recovery = recovery1 + recovery2 + recovery3;
			String newPassword1 = newPasswordField1.getText();
			String newPassword2 = newPasswordField2.getText();
            String twoFactorCode = twoFactorField.getText();
			if (newPassword1.equals(newPassword2)) {

			  UserFrame frame = UserFrame.getFrameForComponent(recover);
			  boolean success = frame.getApp().recoverPass(email, recovery,
                twoFactorCode, newPassword1);

			  if (success) {
				frame.setPanel(new VaultPanel());
				errorLabel.setText("Successfully reset password");
			  } else {
				errorLabel.setText("Invalid email and/or password.");
			  }
			}
		  }
        });

        newPasswordField2.addCaretListener(new CaretListener() {
          public void caretUpdate(CaretEvent ce) {
            String rpwd = new String(newPasswordField2.getPassword());
            String pwd = new String(newPasswordField1.getPassword());
            if (pwd.equals(rpwd)) {
              newPasswordLabel2.setForeground(Color.green);
              recover.setEnabled(true);
            } else {
              newPasswordLabel2.setForeground(Color.red);
              recover.setEnabled(false);
            }
          }
        });

        JButton back = new JButton();
        back.setText("Back");

        back.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                UserFrame frame = UserFrame.getFrameForComponent(back);
                frame.setPanel(new UserLoginPanel(false));
            }
        });

        add(emailLabel);
        add(emailField);
        add(recoveryQuestion1);
        add(recoveryAnswer1);
        add(recoveryQuestion2);
        add(recoveryAnswer2);
        add(recoveryQuestion3);
        add(recoveryAnswer3);
        add(twoFactorLabel);
        add(twoFactorField);
        add(newPasswordLabel1);
        add(newPasswordField1);
        add(newPasswordLabel2);
        add(newPasswordField2);
        add(recover);
        add(back);
        add(errorLabel);
        add(new JPanel());
        setLayout(new GridLayout(10, 2));
    }
}
