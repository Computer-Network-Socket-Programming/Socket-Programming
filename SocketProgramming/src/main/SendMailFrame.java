package main;

import model.ReplyMailDTO;
import view.SenderPanel;

import javax.swing.*;


public class SendMailFrame extends JFrame {

    public SendMailFrame(ReplyMailDTO replyMailDTO) {
        setTitle("메일 작성");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //실제 로그인 값 들어가야 함
        SenderPanel senderPanel = new SenderPanel("dmlwhd010@naver.com", "yuneui1523",replyMailDTO.recipient(),replyMailDTO.message());

        SwingUtilities.invokeLater(() -> senderPanel.requestFocusInWindow());


        add(senderPanel);
        setVisible(true);
    }
}



