package main;

import controller.ohsung.NaverConnector;
import model.ohsung.EmailDataRepository;
import view.ohsung.MainView;

public class Main {

    public static void main(String[] args) {
        String userId = "오성";
        MainView mainView = new MainView(userId);
        mainView.createMainFrame();
    }
}