package main;

import controller.ohsung.NaverConnector;
import model.ohsung.EmailDataRepository;
import view.LoginView;
import view.ohsung.MainView;

public class Main {

    public static void main(String[] args) {
//        String nickname = "오성";
//        MainView mainView = new MainView(nickname);
//        mainView.createMainFrame();

        new LoginView();
    }
}