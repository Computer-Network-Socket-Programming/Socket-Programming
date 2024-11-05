package view;

import controller.ohsung.NaverConnector;
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
    private NaverUserInfoDTO naverUserInfoDTO;
    private GoogleUserInfoDTO googleUserInfoDTO;

    public boolean isValidate(String userId, String userPassword) {
        SmtpController stmpCon = new SmtpController(userId, userPassword);
        SmtpStatusCode checkValidate = null;
        try {
            checkValidate = stmpCon.authenticate(userId, userPassword);
            if (checkValidate.getCode() == 221) return true;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return false;
    }

    public void showTryAgainBtn() {
        JOptionPane.showMessageDialog(null,
                "존재하지 않는 계정입니다!", // 중앙 정렬된 JLabel
                "UNAVAILABLE ACCOUNT!", // 대화 상자 제목
                JOptionPane.PLAIN_MESSAGE // 아이콘 없음
        );
    }

    public void showAlreadyConnectedBtn() {
        JOptionPane.showMessageDialog(null,
                "이미 연동된 계정입니다!", // 중앙 정렬된 JLabel
                "ALREADY CONNECTED ACCOUNT!", // 대화 상자 제목
                JOptionPane.PLAIN_MESSAGE // 아이콘 없음
        );
    }

    public boolean askChangeAcc() {
        int result = JOptionPane.showConfirmDialog(null,
                "이미 계정이 연동되어 있습니다. \n 계정을 변경하시겠습니까?", // 중앙 정렬된 JLabel
                "CHANGE ACCOUNT?", // 대화 상자 제목
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE // 아이콘 없음
        );
        return result == JOptionPane.OK_OPTION;
    }

    public boolean isNotNull(String selectedPortal, String userId, String userPassword) {

        switch (selectedPortal) {
            case "Naver":
                if (this.naverUserInfoDTO.getUsername() != null) return true;
                break;
            case "Gmail":
                if (this.googleUserInfoDTO.getUsername() != null) return true;
                break;
        }

        return false;

    }

    public AccConnectView(String nickname, NaverUserInfoDTO naverUserInfoDTO, GoogleUserInfoDTO googleUserInfoDTO) {
        this.nickname = nickname;
        this.naverUserInfoDTO = naverUserInfoDTO;
        this.googleUserInfoDTO = googleUserInfoDTO;
    }

    public void createAccConnectView() {
        JFrame accFrame = new JFrame("반갑습니다!" + nickname + "님!!");
        accFrame.setSize(500, 400);
        accFrame.setLocationRelativeTo(null); // 화면 중앙에 위치하게 설정
        accFrame.setLayout(null);

        JLabel ml = new JLabel("연동할 계정의 아이디와 비밀번호를 입력해주세요\n");
        String[] portals = {"Naver", "Gmail"};
        portalComboBox = new JComboBox<>(portals);
        JLabel idLabel = new JLabel("ID :");
        JTextField idField = new JTextField();
        JLabel passwordLabel = new JLabel("PW :");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("CONNECT!");

        //컴포넌트 크기 및 위치 조정
        ml.setSize(300, 30);
        ml.setLocation(130, 70);
        idLabel.setSize(100, 30);
        idLabel.setLocation(190, 120);
        idField.setSize(100, 30);
        idField.setLocation(220, 120);
        passwordLabel.setSize(100, 30);
        passwordLabel.setLocation(190, 150);
        passwordField.setSize(100, 30);
        passwordField.setLocation(220, 150);
        portalComboBox.setSize(100, 30);
        portalComboBox.setLocation(140, 200);
        loginButton.setSize(100, 30);
        loginButton.setLocation(270, 200);

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

                // 확인 버튼이 눌렸을 때 수행할 동작 (연동시도)
                if (result == JOptionPane.OK_OPTION) {

                    // 골뱅이 여러개인 경우 tryagain()
                    int atCnt = 0;
                    for (char c : userId.toCharArray()) {
                        if (c == '@') atCnt++;
                    }
                    if (atCnt >= 2) {
                        showTryAgainBtn();
                        return;
                    }


                    switch (Objects.requireNonNull(selectedPortal)) {
                        case "Naver":
                            userId = (userId.contains("@naver.com")) ? userId : userId + "@naver.com";
                            boolean notNull = isNotNull(selectedPortal, userId, userPassword);

                            // 덮어쓰기 시도
                            if (notNull) {
                                if (askChangeAcc()) {
                                    // 이미 연동된 계정인 경우
                                    if (userId.equals(naverUserInfoDTO.getUsername()) && userPassword.equals(naverUserInfoDTO.getUsername())) {
                                        showAlreadyConnectedBtn();
                                    }
                                    // 새 계정을 덮어쓰는 경우
                                    else {
                                        if (isValidate(userId, userPassword)) {
                                            naverUserInfoDTO.setUsername(userId);
                                            naverUserInfoDTO.setPassword(userPassword);

                                            accFrame.dispose();
                                            showSuccessMessage();
                                        } else showTryAgainBtn();
                                    }
                                }

                            }

                            // 아예 새 연동
                            else {
                                if (isValidate(userId, userPassword)) {
                                    naverUserInfoDTO.setUsername(userId);
                                    naverUserInfoDTO.setPassword(userPassword);

                                    accFrame.dispose();
                                    showSuccessMessage();
                                } else showTryAgainBtn();
                            }

                            break;

                        case "Google":
                            userId = (userId.contains("@gmail.com")) ? userId : userId + "@gmail.com";
                            notNull = isNotNull(selectedPortal, userId, userPassword);

                            // 덮어쓰기 시도
                            if (notNull) {
                                if (askChangeAcc()) {
                                    // 이미 연동된 계정인 경우
                                    if (userId.equals(googleUserInfoDTO.getUsername()) && userPassword.equals(googleUserInfoDTO.getUsername())) {
                                        showAlreadyConnectedBtn();
                                    }
                                    // 새 계정을 덮어쓰는 경우
                                    else {
                                        if (isValidate(userId, userPassword)) {
                                            googleUserInfoDTO.setUsername(userId);
                                            googleUserInfoDTO.setPassword(userPassword);

                                            accFrame.dispose();
                                            showSuccessMessage();
                                        } else showTryAgainBtn();
                                    }
                                }

                            }

                            // 아예 새 연동
                            else {
                                if (isValidate(userId, userPassword)) {
                                    googleUserInfoDTO.setUsername(userId);
                                    googleUserInfoDTO.setPassword(userPassword);

                                    accFrame.dispose();
                                    showSuccessMessage();
                                } else showTryAgainBtn();
                            }

                            break;

                        default:
                            showTryAgainBtn();
                            break;

                    }
                } else System.out.println("로그인 취소됨.");
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

    private void showSuccessMessage() {
        JOptionPane.showMessageDialog(null,
                "연동 성공! \n 새로고침을 눌러주떼욤!", // 중앙 정렬된 JLabel
                "양쿤 러버", // 대화 상자 제목
                JOptionPane.PLAIN_MESSAGE // 아이콘 없음
        );
    }
}