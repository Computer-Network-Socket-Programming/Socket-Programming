package view;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PortalAccConnect extends JPanel {
    private JComboBox<String> portalComboBox;
    private JTextField idField;
    private JPasswordField passwordField;

    public PortalAccConnect() {

        // 기본 프레임 설정
        setLayout(null);
        setBackground(Color.getHSBColor(0.316f, 0.26f, 0.94f));

        JLabel ml1 = new JLabel("반갑습니다! @#!#@@s님!\n");
        JLabel ml2 = new JLabel("연동할 계정의 아이디와 비밀번호를 입력해주세요\n");
        String[] portals = { "Naver", "Gmail", "Daum" };
        portalComboBox = new JComboBox<>(portals);
        JLabel idLabel = new JLabel("ID :");
        JTextField idField = new JTextField();
        JLabel passwordLabel = new JLabel("PW :");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("CONNECT!");

        //컴포넌트 크기 및 위치 조정
        ml1.setSize(300,30);
        ml1.setLocation(180,50);
        ml2.setSize(300,30);
        ml2.setLocation(130,70);
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

                // 중앙 정렬된 JLabel 생성
                JLabel messageLabel = new JLabel(
                        "<html><div style='text-align: left;'>" +
                                "해당 정보로 연동 하시겠습니까?<br>" +
                                "<br>포털: " + selectedPortal +
                                "<br>아이디: " + userId +
                                "<br>비밀번호: " + userPassword + "</div></html>"
                );

                // 아이콘 없이 메시지 대화상자 표시
                JOptionPane.showMessageDialog(null,
                        messageLabel, // 중앙 정렬된 JLabel
                        "로그인 정보", // 대화 상자 제목
                        JOptionPane.PLAIN_MESSAGE // 아이콘 없음
                );
            }
        });

        // 컴포넌트들을 프레임에 추가
        add(ml1);
        add(ml2);
        add(portalComboBox);
        add(idLabel);
        add(idField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);


        setVisible(true);
    }

    public static void main(String[] args) {
        new PortalAccConnect();
    }
}


