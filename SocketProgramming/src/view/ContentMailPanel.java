package view;

import main.SendMailFrame;
import model.ReplyMailDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ContentMailPanel extends JPanel {
    ArrayList<String> dummydatas = new ArrayList<>();
    MailInfo mailInfo;
    JLabel senderLabel, receiverLabel, dateLabel, subjectLabel, contentLabel;
    JTextField senderAddress, receiverAddress, mailDate, subject;
    JTextArea content;
    JScrollPane contentScroll;
    JButton mailListButton;
    JButton replyMailButton;
    BorderLayout borderLayout;
    GridLayout gridLayout;
    SenderPanel senderPanel;
    SendMailFrame mainFrame;

    public ContentMailPanel(CardLayout cardLayout, JPanel parentPanel) {


        this.senderPanel = senderPanel;

        borderLayout = new BorderLayout();
        gridLayout = new GridLayout(0,2);

        dummydatas.add("sender@naver.com");
        dummydatas.add("receiver@naver.com");
        dummydatas.add("2024년 10월 30일 00:00");
        dummydatas.add("메일 제목");
        dummydatas.add("자니? 자는구나... 잘자.. 자니? 자는구나... 잘자.. 자니? 자는구나... 잘자.. 자니? 자는구나... 잘자.. 자니? 자는구나... 잘자.. 자니? 자는구나... 잘자.. 자니? 자는구나... 잘자..");

        mailInfo = new MailInfo(dummydatas.get(0), dummydatas.get(1), dummydatas.get(2), dummydatas.get(3), dummydatas.get(4));
        setLayout(borderLayout);


        JPanel mailDetailPanel = new JPanel();
        mailDetailPanel.setLayout(gridLayout);
        add(mailDetailPanel,BorderLayout.NORTH);

        subjectLabel = new JLabel("메일 제목:", JLabel.RIGHT);
        senderLabel = new JLabel("보낸 사람:", JLabel.RIGHT);
        receiverLabel = new JLabel("받는 사람:", JLabel.RIGHT);
        dateLabel = new JLabel("날짜:", JLabel.RIGHT);
        contentLabel = new JLabel("내용", JLabel.LEFT);



        subject = new JTextField(50);
        subject.setEditable(false);
        subject.setText(mailInfo.getSubject());

        senderAddress = new JTextField(20);
        senderAddress.setEditable(false);
        senderAddress.setText(mailInfo.getSender());

        receiverAddress = new JTextField(20);
        receiverAddress.setEditable(false);
        receiverAddress.setText(mailInfo.getReceiver());

        mailDate = new JTextField(20);
        mailDate.setEditable(false);
        mailDate.setText(mailInfo.getDate());

        mailDetailPanel.add(subjectLabel);
        mailDetailPanel.add(subject);
        mailDetailPanel.add(senderLabel);
        mailDetailPanel.add(senderAddress);
        mailDetailPanel.add(receiverLabel);
        mailDetailPanel.add(receiverAddress);
        mailDetailPanel.add(dateLabel);
        mailDetailPanel.add(mailDate);
        mailDetailPanel.add(contentLabel);


        content = new JTextArea(5, 20);
        content.setEditable(false);
        content.setLineWrap(true);
        content.setWrapStyleWord(true);
        content.setText(mailInfo.getContent());

        contentScroll = new JScrollPane(content);
        contentScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(contentScroll,BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(gridLayout);
        add(buttonPanel,BorderLayout.SOUTH);

        mailListButton = new JButton("목록으로 돌아가기");
        buttonPanel.add(mailListButton,gridLayout);
        mailListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == mailListButton) {
                    cardLayout.show(parentPanel, "MailListPanel");
                }
            }
        });
        replyMailButton = new JButton("답장");
        buttonPanel.add(replyMailButton,gridLayout);
        replyMailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == replyMailButton) {
                    String sender = mailInfo.getSender();
                    String receiver = mailInfo.getReceiver();
                    String content = mailInfo.getContent();
                    if (sender == null || sender.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "보낸 사람 주소가 유효하지 않습니다.");
                        return;
                    }
                    if (receiver == null || receiver.isEmpty()) {
                        JOptionPane.showMessageDialog(null,"받는 사람 주소가 유효하지 않습니다.");
                        return;
                    }
                    if (content == null || content.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "내용이 존재하지 않습니다.");
                    }

                    ReplyMailDTO replyMailDTO = new ReplyMailDTO("Re:   "+ receiver, "-----Original Message-----\n\n" + content +"\n\n");
                    new SendMailFrame(replyMailDTO);
                }
            }
        });
    }


}
