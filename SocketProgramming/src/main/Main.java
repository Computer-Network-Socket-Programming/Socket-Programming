package main;

import controller.ohsung.NaverConnector;
import model.ohsung.EmailDataRepository;
import view.ohsung.MainView;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try{
            String username = "본인 아이디";
            String password = "본인 비밀번호";

            NaverConnector naverConnector = new NaverConnector(username, password);
            naverConnector.fetchMails();
            naverConnector.disconnect();

        } catch (Exception e){
            e.printStackTrace();
        }
        String userId = "오성";
        MainView mainView = new MainView(userId);
        mainView.createMainFrame();
    }
}