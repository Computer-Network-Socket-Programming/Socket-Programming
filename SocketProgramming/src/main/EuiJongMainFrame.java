package main;

import view.SenderFrame;

import javax.swing.*;
import java.util.Map;

public class EuiJongMainFrame extends JFrame {
//    CardLayout cardLayout;
//
//    public EuiJongMainFrame() {
//        setTitle("MailContent");
//        setSize(600, 600);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
//
//        cardLayout = new CardLayout();
//        JPanel mainPanel = new JPanel(cardLayout);
//
//        ContentMailPanel contentMailPanel = new ContentMailPanel(cardLayout, mainPanel);
//        mainPanel.add(contentMailPanel, "ContentMailPanel");
//
//        MailListTestPanel mailListTestPanel = new MailListTestPanel(cardLayout, mainPanel);
//        mainPanel.add(mailListTestPanel, "MailListPanel");
//
//        add(mainPanel);
//
//
//        cardLayout.show(mainPanel, "ContentMailPanel");
//
//        setVisible(true);
//    }

    public static void main(String[] args) {
        Map<String, String> env = System.getenv();
        new SenderFrame(env.get("USER_NAME"), env.get("PASSWORD"));
    }
}
