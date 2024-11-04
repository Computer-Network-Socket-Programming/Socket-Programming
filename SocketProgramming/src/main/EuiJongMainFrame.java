package main;

import view.ContentMailPanel;

import javax.swing.*;
import java.awt.*;

public class EuiJongMainFrame extends JFrame {
    CardLayout cardLayout;

    public EuiJongMainFrame() {
        setTitle("MailContent");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        ContentMailPanel contentMailPanel = new ContentMailPanel(cardLayout, mainPanel);
        mainPanel.add(contentMailPanel, "ContentMailPanel");


        add(mainPanel);


        cardLayout.show(mainPanel, "ContentMailPanel");

        setVisible(true);
    }

    public static void main(String[] args) {
        new EuiJongMainFrame();
    }
}