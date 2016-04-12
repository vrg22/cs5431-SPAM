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
                // TODO: store new account

                ClientApplication app = ClientApplication.getFrameForComponent(save);
                app.setPanel(new ShowAccountsPanel());
            }
        });

        JButton back = new JButton();
        back.setText("Back");
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ClientApplication app = ClientApplication.getFrameForComponent(back);
                app.setPanel(new ShowAccountsPanel());
            }
        });

        add(back);
        add(new JPanel());
        add(nameLabel);
        add(name);
        add(emailLabel);
        add(email);
        add(passwordLabel);
        add(password);
        add(save);
        add(new JPanel());
        setLayout(new GridLayout(5, 2));
    }
}
