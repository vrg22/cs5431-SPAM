import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class VaultPanel extends JPanel {
    public VaultPanel() {
        JButton view = new JButton();
        view.setText("View/Edit my stored accounts");

        view.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

                // TODO: go to view/edit accounts page
            }
        });

        JButton create = new JButton();
        create.setText("Register");

        create.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

                // TODO: go to store new account panel
            }
        });

        add(view);
        add(create);
        setLayout(new FlowLayout());
    }
}
