package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    private static String nickname;
    private GridBagLayout layout;

    public LoginView() {
        super("시작 페이지");
        //컴포넌트 생성
        JLabel nicknameLabel = new JLabel("nickname");
        JTextField nicknameField = new JTextField(10);
        JButton btn1 = new JButton("start!");

        //레이아웃 생성, 배치
        layout = new GridBagLayout();
        setLayout(layout);
        gbinsert(nicknameLabel, 0, 0, 1, 1);
        gbinsert(nicknameField, 3, 0, 1, 1);
        gbinsert(btn1, 3, 3, 1, 1);
        setSize(400, 400); //창 크기 설정

        //화면 중앙 배치
        Dimension frameSize = getSize();
        Dimension windowSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((windowSize.width - frameSize.width) / 2,
                (windowSize.height - frameSize.height) / 2);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        //버튼 눌렀을 때
        btn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nickname = nicknameField.getText();// nickname 전달
                MainView mainView = new MainView(nickname);
                mainView.createMainFrame();
                dispose();
            }
        });
    }

    public void gbinsert(Component c, int x, int y, int w, int h) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        layout.setConstraints(c, gbc);
        this.add(c);
    }

}