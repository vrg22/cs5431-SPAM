import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

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
                String loginUrl = ClientApplication.HTTPS_ROOT + "/login";
                Map<String, String> loginParams = new HashMap<>();
                loginParams.put("email", email.getText());
                loginParams.put("password", password.getText());
                String response = SendHttpsRequest.post(loginUrl, loginParams);

                ClientApplication app = ClientApplication.getFrameForComponent(login);
                app.setPanel(new VaultPanel());
            }
        });

        JButton register = new JButton();
        register.setText("Register");

        register.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                ClientApplication app = ClientApplication.getFrameForComponent(register);
                app.setPanel(new RegisterPanel());
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
