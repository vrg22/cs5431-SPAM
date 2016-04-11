import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    public LoginPanel() {
        JLabel emailLabel = new JLabel();
        emailLabel.setText("Email:");
        JTextField email = new JTextField();

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("Master Password:");
        JPasswordField password=new JPasswordField(10);

        JButton login = new JButton();
        login.setText("Login");

        login.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // TODO: log in user
            }
        });

        JButton register = new JButton();
        register.setText("Register");

        register.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // TODO: go to register new user panel
            }
        });

        add(emailLabel);
        add(email);
        add(passwordLabel);
        add(password);
        add(register);
        add(login);
        setLayout(new GridLayout(3, 2));
    }
}
