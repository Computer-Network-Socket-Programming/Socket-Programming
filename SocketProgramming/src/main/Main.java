package main;

import view.MailDetailPanel;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    public Main() {
        BorderLayout borderLayout = new BorderLayout();
        setSize(600, 600);

        MailDetailPanel mailDetailPanel = new MailDetailPanel();
        add(mailDetailPanel, borderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setVisible(true);
    }


    public static void main(String[] args) {
        new Main();
    }
}
