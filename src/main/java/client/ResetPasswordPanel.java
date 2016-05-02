import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class ResetPasswordPanel extends JPanel {
  public ResetPasswordPanel() {
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
	JPasswordField newPasswordField1 = new JPasswordField(10);

	JLabel newPasswordLabel2 = new JLabel();
	newPasswordLabel2.setText("Confirm New Master Password:");
	JPasswordField newPasswordField2 = new JPasswordField(10);

	JButton recover = new JButton();
	recover.setText("Reset Password");

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
	back.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
		ClientFrame frame = ClientFrame.getFrameForComponent(back);
		frame.setPanel(new VaultPanel());
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
