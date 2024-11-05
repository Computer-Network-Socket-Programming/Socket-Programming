package view;

import model.ohsung.GoogleUserInfoDTO;
import model.ohsung.NaverUserInfoDTO;
import util.enums.SmtpStatusCode;
import controller.SmtpController;
import view.ohsung.MainView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Objects;

public class AccConnectView {
    private String nickname;
    private JComboBox<String> portalComboBox;
    private JTextField idField;
    private JPasswordField passwordField;

    public boolean isValidate(String userId, String userPassword){
        SmtpController stmpCon = new SmtpController(userId, userPassword);
        SmtpStatusCode checkValidate = null;
        try {
            checkValidate = stmpCon.authenticate(userId, userPassword);
            if ( checkValidate.getCode() == 221 ) return true;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return false;
    }

    public AccConnectView(String nickname){
        this.nickname = nickname;
    }
    public void createAccConnectView(){
        JFrame accFrame = new JFrame("반갑습니다!" + nickname + "님!!");
//        accFrame.setBackground(Color.getHSBColor(0.316f, 0.26f, 0.94f));
        accFrame.setSize(500,400);
        accFrame.setLocationRelativeTo(null); // 화면 중앙에 위치하게 설정
//        accFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        accFrame.setLayout(null);

        JLabel ml = new JLabel("연동할 계정의 아이디와 비밀번호를 입력해주세요\n");
        String[] portals = { "Naver", "Gmail" };
        portalComboBox = new JComboBox<>(portals);
        JLabel idLabel = new JLabel("ID :");
        JTextField idField = new JTextField();
        JLabel passwordLabel = new JLabel("PW :");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("CONNECT!");

        //컴포넌트 크기 및 위치 조정
        ml.setSize(300,30);
        ml.setLocation(130,70);
        idLabel.setSize(100,30);
        idLabel.setLocation(190,120);
        idField.setSize(100,30);
        idField.setLocation(220,120);
        passwordLabel.setSize(100,30);
        passwordLabel.setLocation(190,150);
        passwordField.setSize(100,30);
        passwordField.setLocation(220,150);
        portalComboBox.setSize(100,30);
        portalComboBox.setLocation(140,200);
        loginButton.setSize(100,30);
        loginButton.setLocation(270,200);

        // 연동버튼 ActionListener
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 선택한 포털, 입력한 아이디와 비밀번호 가져오기
                String selectedPortal = (String) portalComboBox.getSelectedItem();
                String userId = idField.getText();
                String userPassword = new String(passwordField.getPassword());

                // 정보 확인용 알림창
                JLabel messageLabel = new JLabel(
                        "<html><div style='text-align: left;'>" +
                                "해당 정보로 연동 하시겠습니까?<br>" +
                                "<br>포털: " + selectedPortal +
                                "<br>아이디: " + userId +
                                "<br>비밀번호: " + userPassword + "</div></html>"
                );

                // 알림창 설정
                int result = JOptionPane.showConfirmDialog(null,
                        messageLabel, // 중앙 정렬된 JLabel
                        "로그인 정보 확인", // 대화 상자 제목
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE // 아이콘 없음
                );

                // 확인 버튼이 눌렸을 때 수행할 동작
                if (result == JOptionPane.OK_OPTION) {

                    // userId 정보 처리
                    switch(Objects.requireNonNull(selectedPortal)){
                        case "Naver" :
                            userId = ( userId.contains("@naver.com") ) ? userId : userId + "@naver.com";

                            if ( isValidate(userId, userPassword) ){
                                NaverUserInfoDTO naverUserInfoDTO = NaverUserInfoDTO.getInstance();
                                naverUserInfoDTO.setUsername(userId);
                                naverUserInfoDTO.setPassword(userPassword);

                                accFrame.dispose();
                                MainView mainView = new MainView(nickname);
                                mainView.createMainFrame();
                            }

                            break;

                        case "Google" :
                            userId = ( userId.contains("@google.com") ) ? userId : userId + "@google.com";

                            if ( isValidate(userId, userPassword) ){
                                GoogleUserInfoDTO googleInfo = GoogleUserInfoDTO.getInstance();
                                googleInfo.setUsername(userId);
                                googleInfo.setPassword(userPassword);

                                accFrame.dispose();
                                MainView mainView = new MainView(nickname);
                                mainView.createMainFrame();
                            }
                            break;

                        default :
                            // 아이디와 비밀번호를 다시 입력해주세요 ! 알림 뜨게 하기
                            break;

                    }
                } else {
                    System.out.println("로그인 취소됨.");
                }


            }
        });

        // 컴포넌트들을 프레임에 추가
        accFrame.add(ml);
        accFrame.add(portalComboBox);
        accFrame.add(idLabel);
        accFrame.add(idField);
        accFrame.add(passwordLabel);
        accFrame.add(passwordField);
        accFrame.add(loginButton);

        accFrame.setVisible(true);
    }
}