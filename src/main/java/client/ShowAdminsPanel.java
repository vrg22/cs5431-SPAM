
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ShowAdminsPanel extends JPanel {
    public ShowAdminsPanel(Admin.Header[] admins) {
        JButton back = new JButton();
        back.setText("Logout");
        add(back);
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AdminFrame frame = AdminFrame.getFrameForComponent(back);
                frame.getApp().endAdminManagement();
                //frame.setPanel(new AdminLoginPanel(false));
                frame.setPanel(new AdminLoginPanel());
            }
        });
        add(new JPanel());

        JLabel errorLabel = new JLabel();

        for (Admin.Header admin : admins) {
            JLabel nameLabel = new JLabel();
            nameLabel.setText(admin.getUsername());
            add(nameLabel);

            JButton delete = new JButton();
            delete.setText("Delete");
            add(delete);

            delete.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                	AdminFrame frame = AdminFrame.getFrameForComponent(back);
                    //boolean success = frame.getApp().obliterateAdmin(admin.getUsername(), admin.getId());
                    boolean success = frame.getApp().obliterateAdmin(admin.getUsername());
                    if (success) {
                    	Admin.Header[] admins = frame.getApp().getAdmins();
                    	frame.setPanel(new ShowAdminsPanel(admins));
                    } else {
                        //TODO: Implement some sort of timeout for failure?
                        errorLabel.setText("Invalid admin passphrase.");
                    }
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

        add.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	AdminFrame frame = AdminFrame.getFrameForComponent(add);
            	if (frame.getApp().getAdmins().length == Admin.MAX_ADMINS) {
            		errorLabel.setText("Maximum number of admins reached.");
            	}
            	else {
                    frame.setPanel(new AddAdminPanel());
            	}
            }
        });

        add(errorLabel);
        setLayout(new GridLayout(admins.length + 2, 2));
    }
}
