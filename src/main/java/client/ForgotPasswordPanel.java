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
        recoveryQuestion1.setText("Recovery Question1:");
        JPasswordField recoveryAnswer1 = new JPasswordField(10);

        JLabel recoveryQuestion2 = new JLabel();
        recoveryQuestion2.setText("Recovery Question2:");
        JPasswordField recoveryAnswer2 = new JPasswordField(10);

        JLabel recoveryQuestion3 = new JLabel();
        recoveryQuestion3.setText("Recovery Question3:");
        JPasswordField recoveryAnswer3 = new JPasswordField(10);

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

		newPasswordField2.addCaretListener(new CaretListener() {
		  public void caretUpdate(CaretEvent ce) {
			String rpwd = new String(newPasswordField2.getPassword());
			String pwd = new String(newPasswordField1.getPassword());
			if (pwd.equals(rpwd)) {
			  newPasswordLabel2.setForeground(Color.green);
			} else {
			  newPasswordLabel2.setForeground(Color.red);
			}
		  }
		});

        JButton recover = new JButton();
        recover.setText("Recover Password");

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
			if (newPassword1.equals(newPassword2)) {

			  ClientFrame frame = ClientFrame.getFrameForComponent(recover);
			  boolean success = frame.getApp().recoverPass(email, recovery, newPassword1);

			  if (success) {
				frame.setPanel(new VaultPanel());
				errorLabel.setText("Successfully reset password");
			  } else {
				errorLabel.setText("Invalid email and/or password.");
			  }
			}
		  }
        });

        JButton back = new JButton();
        back.setText("Back");

        back.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                ClientFrame frame = ClientFrame.getFrameForComponent(back);
                frame.setPanel(new LoginPanel(false));
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
