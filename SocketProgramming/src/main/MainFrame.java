package main;

import view.SenderPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Sender");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 1));

        add(new SenderPanel(), BorderLayout.CENTER);

        setSize(800, 600);
        setVisible(true);
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
