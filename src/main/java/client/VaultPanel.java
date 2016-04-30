import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.security.cert.Certificate;
import java.io.*;

public class VaultPanel extends JPanel {
    public VaultPanel() {
        JButton view = new JButton();
        view.setText("View/Edit My Stored Accounts");

        view.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                UserFrame frame = UserFrame.getFrameForComponent(view);
                Account.Header[] accounts = frame.getApp().getAccounts();
                frame.setPanel(new ShowAccountsPanel(accounts));
            }
        });

        JButton create = new JButton();
        create.setText("Store New Account");

        create.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
            	UserFrame frame = UserFrame.getFrameForComponent(create);
                frame.setPanel(new StoreNewAccountPanel());
            }
        });

        JButton showCert = new JButton();
        showCert.setText("Show SSL Certificate");

        // JLabel certLabel = new JLabel();
        // certLabel.setMaximumSize(new Dimension(300, 300));
        showCert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Certificate[] certs = SendHttpsRequest.getServerCertificates(
                        ClientApplication.HTTPS_ROOT);
                    if (certs == null) return;
                    StringBuilder builder = new StringBuilder();
                    for (Certificate cert : certs) {
                        builder.append(cert.toString());
                    }
                    JLabel certLabel = new JLabel();
                    certLabel.setText("See Terminal output for certificate info.");
                    JOptionPane.showMessageDialog(null, certLabel);

                    System.out.println(builder.toString());
                } catch (IOException e2) {

                }
            }
        });

        add(view);
        add(create);
        add(showCert);
        // add(certLabel);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }
}
