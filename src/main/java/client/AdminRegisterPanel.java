import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.io.*;

public class AdminRegisterPanel extends JPanel {
	public AdminRegisterPanel() {
        JLabel emailLabel = new JLabel();
        emailLabel.setText("Email:");
        JTextField emailField = new JTextField();

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("Master Password:");
		passwordLabel.setForeground(Color.red);
        JPasswordField passwordField = new JPasswordField(10);

		passwordField.addCaretListener(new CaretListener() {
		  public void caretUpdate(CaretEvent ce) {
			if (PasswordStrength.check(new String(passwordField.getPassword()))) {
			  passwordLabel.setForeground(Color.green);
			} else {
			  passwordLabel.setForeground(Color.red);
			}
		  }
		});

		JLabel rpasswordLabel = new JLabel();
        rpasswordLabel.setText("Re-enter Master Password:");
		rpasswordLabel.setForeground(Color.red);
        JPasswordField rpasswordField = new JPasswordField(10);

		rpasswordField.addCaretListener(new CaretListener() {
		  public void caretUpdate(CaretEvent ce) {
			String rpwd = new String(rpasswordField.getPassword());
			String pwd = new String(passwordField.getPassword());
			if (pwd.equals(rpwd)) {
			  rpasswordLabel.setForeground(Color.green);
			} else {
			  rpasswordLabel.setForeground(Color.red);
			}
		  }
		});

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

                String twoFactorSecret = CryptoServiceProvider.getNewTwoFactorSecretKey();
                String qrUrl = getQRBarcodeURL(email, twoFactorSecret);

                AdminFrame frame = AdminFrame.getFrameForComponent(register);
                try {
                    URI uri = new URL(qrUrl).toURI();
                    Object[] choices = {"Get QR Code"};
                    Object defaultChoice = choices[0];
                    JOptionPane.showOptionDialog(frame,
                        "Please scan the following QR code in your Google Authenticator app.",
                        "Two-Factor Auth Setup", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, choices, defaultChoice);
                    Desktop.getDesktop().browse(uri);
                } catch (IOException | URISyntaxException e1) {
                    Object[] choices = {"OK"};
                    Object defaultChoice = choices[0];
                    JOptionPane.showOptionDialog(frame,
                        "Please check the command line to get a URL to your QR code. Then scan the QR code in your Google Authenticator app.",
                        "Two-Factor Auth Setup", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, choices, defaultChoice);
                    System.out.println(qrUrl);
                }

                boolean success = frame.getApp().register(email, password, recovery, twoFactorSecret);
                if (success) {
                    frame.setPanel(new AdminLoginPanel(false));
                } else {
                    errorLabel.setText("Sorry there was a problem registering.");
                }
            }
        });

        JButton back = new JButton();
        back.setText("Back");

        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AdminFrame frame = AdminFrame.getFrameForComponent(back);
                frame.setPanel(new AdminLoginPanel(false));
            }
        });

        add(emailLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(rpasswordLabel);
        add(rpasswordField);
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
        setLayout(new GridLayout(10, 4));
    }

    private static String getQRBarcodeURL(String email, String secret) {
      String format = "https://www.google.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=otpauth://totp/%s%%3Fsecret%%3D%s%%26issuer%%3DSPAM";
      return String.format(format, email, secret);
    }
}
