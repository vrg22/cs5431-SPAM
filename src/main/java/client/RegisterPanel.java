import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class RegisterPanel extends JPanel {
    public RegisterPanel() {
        JLabel emailLabel = new JLabel();
        emailLabel.setText("Email:");
        JTextField email = new JTextField();

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("Master Password:");
        JPasswordField password=new JPasswordField(10);

        JButton register = new JButton();
        register.setText("Register");

        register.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // TODO: register new user

                ClientApplication app = ClientApplication.getFrameForComponent(register);
                app.setPanel(new VaultPanel());
            }
        });

        add(emailLabel);
        add(email);
        add(passwordLabel);
        add(password);
        add(register);
        setLayout(new GridLayout(3, 2));
    }
}
