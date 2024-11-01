package main;

import view.MailDetailPanel;
import view.MailListTestPanel;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    public Main() {

        setTitle("Mail Application");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);


        MailDetailPanel mailDetailPanel = new MailDetailPanel(cardLayout, mainPanel);
        mainPanel.add(mailDetailPanel, "MailDetailPanel");


        MailListTestPanel mailListTestPanel = new MailListTestPanel();
        mainPanel.add(mailListTestPanel, "MailListPanel");

        cardLayout.show(mainPanel, "MailDetailPanel");

        add(mainPanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        new Main();
    }
}
