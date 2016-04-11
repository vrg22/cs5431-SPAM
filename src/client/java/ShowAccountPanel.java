import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ShowAccountPanel extends JPanel {
    public ShowAccountPanel(Account account) {
        JLabel nameLabel = new JLabel();
        nameLabel.setText("Name:");
        JTextField name = new JTextField();
        name.setText(account.getName());

        JLabel emailLabel = new JLabel();
        emailLabel.setText("Email:");
        JTextField email = new JTextField();
        email.setText(account.getUsername());

        JLabel passwordLabel = new JLabel();
        passwordLabel.setText("Master Password:");
        JPasswordField password=new JPasswordField(10);
        password.setText(account.getPassword());

        JButton save = new JButton();
        save.setText("Save Changes");

        save.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

                // TODO: save changes
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
