package main;

import view.PortalAccConnect;
import view.Login;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    Main(){
        add(new Login());
//        add(new PortalAccConnect());
        setSize(500,400);
        setVisible(true);
        setLocationRelativeTo(null); // 화면 중앙에 위치하게 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public static void main(String[] args) {
        new Main();
    }
}
