import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.io.*;
import java.util.regex.*;

public class UserRegisterPanel extends JPanel {
	public UserRegisterPanel() {
        JLabel emailLabel = new JLabel();
        emailLabel.setText("Email:");
        JTextField emailField = new JTextField();

        emailField.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent ce) {
                if (isEmailValid(emailField.getText())) {
                    emailLabel.setForeground(Color.green);
                } else {
                    emailLabel.setForeground(Color.red);
                }
            }
        });

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

        JLabel recoveryQuestion1 = new JLabel();
        recoveryQuestion1.setText("What is the name of your first pet?");
        JPasswordField recoveryAnswer1 = new JPasswordField(10);

        JLabel recoveryQuestion2 = new JLabel();
        recoveryQuestion2.setText("What is the hospital you were born in?");
        JPasswordField recoveryAnswer2 = new JPasswordField(10);

        JLabel recoveryQuestion3 = new JLabel();
        recoveryQuestion3.setText("What is the last name of your first grade teacher?");
        JPasswordField recoveryAnswer3 = new JPasswordField(10);

        JButton register = new JButton();
        register.setText("Register");
        register.setEnabled(false);

        JLabel errorLabel = new JLabel();
        register.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String email = emailField.getText();
                String password = passwordField.getText();
                String rpassword = rpasswordField.getText();
				String recovery1 = recoveryAnswer1.getText();
				String recovery2 = recoveryAnswer2.getText();
				String recovery3 = recoveryAnswer3.getText();
				String recovery = recovery1 + recovery2 + recovery3;

                // Check if all the fields are filled out as required
                if (email.equals("") || password.equals("") || rpassword.equals("")
                        || recovery1.equals("") || recovery2.equals("") || recovery3.equals("")) {
                    errorLabel.setText("Please fill out all the fields");
                    return;
                } else if (!isEmailValid(email)) {
                    errorLabel.setText("Please enter a valid email address");
                    return;
                }

                String twoFactorSecret = CryptoServiceProvider.getNewTwoFactorSecretKey();
                String qrUrl = getQRBarcodeURL(email, twoFactorSecret);

                UserFrame frame = UserFrame.getFrameForComponent(register);
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
                    frame.setPanel(new UserLoginPanel(false));
                } else {
                    errorLabel.setText("Sorry there was a problem registering.");
                }
            }
        });

		rpasswordField.addCaretListener(new CaretListener() {
		  public void caretUpdate(CaretEvent ce) {
			String rpwd = new String(rpasswordField.getPassword());
			String pwd = new String(passwordField.getPassword());
            if (pwd.equals(rpwd)) {
                rpasswordLabel.setForeground(Color.green);
                register.setEnabled(true);
            } else {
                rpasswordLabel.setForeground(Color.red);
                register.setEnabled(false);
            }
		  }
		});


        JButton back = new JButton();
        back.setText("Back");

        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UserFrame frame = UserFrame.getFrameForComponent(back);
                frame.setPanel(new UserLoginPanel(false));
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

    // Check that `email` is a valid email address (e.g., some@email.com)
    private static boolean isEmailValid(String email) {
        Pattern emailPattern = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");
        return email != null && emailPattern.matcher(email).matches();
    }
}
