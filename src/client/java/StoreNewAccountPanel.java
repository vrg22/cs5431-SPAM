import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class StoreNewAccountPanel extends JPanel {
    public StoreNewAccountPanel() {
        JLabel nameLabel = new JLabel();
        nameLabel.setText("Name:");
        JTextField name = new JTextField();

        JLabel emailLabel = new JLabel();
        emailLabel.setText("Email:");
        JTextField email = new JTextField();

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("Master Password:");
        JPasswordField password=new JPasswordField(10);

        JButton save = new JButton();
        save.setText("Save");

        save.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

                // TODO: store new account, then go to show accounts panel
            }
        });

        add(nameLabel);
        add(name);
        add(emailLabel);
        add(email);
        add(passwordLabel);
        add(password);
        add(save);
        setLayout(new GridLayout(4, 2));
    }
}
