import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

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

        JLabel newMaster = new JLabel();
        newMaster.setText("Enter New Master password");
        JPasswordField newMasterPass = new JPasswordField(10);

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
			String newPass = newMasterPass.getText();
			ClientFrame frame = ClientFrame.getFrameForComponent(recover);
			boolean success = frame.getApp().recoverPass(email, recovery, newPass);

			if (success) {
			  frame.setPanel(new VaultPanel());
			  errorLabel.setText("Successfully reset password");
			} else {
			  errorLabel.setText("Invalid email and/or password.");
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
        add(newMaster);
        add(newMasterPass);
        add(recover);
        add(back);
        add(errorLabel);
        add(new JPanel());
        setLayout(new GridLayout(10, 2));
    }
}
