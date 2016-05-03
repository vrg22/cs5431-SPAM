
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ShowAdminsPanel extends JPanel {
    public ShowAdminsPanel(Admin.Header[] admins) {
        JButton back = new JButton();
        back.setText("Back");
        add(back);
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AdminFrame frame = AdminFrame.getFrameForComponent(back);
                frame.getApp().endAdminManagement();
                frame.setPanel(new AdminLoginPanel());
            }
        });
        add(new JPanel());

        for (Admin.Header admin : admins) {
            JLabel nameLabel = new JLabel();
            nameLabel.setText(admin.getUsername());
            add(nameLabel);

            JButton view = new JButton();
            view.setText("View/Edit");
            add(view);

            view.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                	//UserFrame frame = UserFrame.getFrameForComponent(view);
                    //Account fullAccount = frame.getApp().getAccount(account.getId());
                    //frame.setPanel(new ShowAccountPanel(fullAccount));
                }
            });
        }
        if (admins.length == 0) {
            JLabel emptyLabel = new JLabel();
            emptyLabel.setText("The system has no admins.");
            add(emptyLabel);
            add(new JPanel());
        }
        
        JButton add = new JButton();
        add.setText("Add Admin");
        add(add);

        //TODO: Handle case where max admins already WHERE??
        add.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
            	AdminFrame frame = AdminFrame.getFrameForComponent(add);
                frame.setPanel(new AddAdminPanel());
            }
        });
        
        //setLayout(new GridLayout(admins.length + 1, 2));
        //IF logic?
        setLayout(new GridLayout(admins.length + 2, 2));
    }
}
