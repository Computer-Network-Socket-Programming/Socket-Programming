package main;

import view.SenderPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class MainFrame extends JFrame {

    public MainFrame() {
        Map<String, String> env = System.getenv();

        setTitle("Sender");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 1));

        add(new SenderPanel(env.get("USER_NAME"), env.get("PASSWORD")), BorderLayout.CENTER);

        setSize(800, 600);
        setVisible(true);
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
