package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MailDetailPanel extends JPanel {
    ArrayList<String> dummydatas = new ArrayList<>();
    MailInfo mailInfo;
    JLabel senderLabel, receiverLabel, dateLabel, subjectLabel, contentLabel;
    JTextField senderAddress, receiverAddress, mailDate, subject;
    JTextArea content;
    GridBagLayout gridBagLayout;

    public MailDetailPanel() {

        dummydatas.add("sender@naver.com");
        dummydatas.add("receiver@naver.com");
        dummydatas.add("2024년 10월 30일 00:00");
        dummydatas.add("메일 제목");
        dummydatas.add("자니? 자는구나... 잘자.. 자니? 자는구나... 잘자.. 자니? 자는구나... 잘자.. 자니? 자는구나... 잘자.. 자니? 자는구나... 잘자..자니? 자는구나... 잘자.. 자니? 자는구나... 잘자.. 자니? 자는구나... 잘자.. 자니? 자는구나... 잘자.. 자니? 자는구나... 잘자..자니? 자는구나... 잘자.. 자니? 자는구나... 잘자.. 자니? 자는구나... 잘자.. 자니? 자는구나... 잘자.. 자니? 자는구나... 잘자..");
        mailInfo = new MailInfo(dummydatas.get(0), dummydatas.get(1), dummydatas.get(2), dummydatas.get(3), dummydatas.get(4));

        // GridBagLayout 초기화 및 설정
        gridBagLayout = new GridBagLayout();
        setLayout(gridBagLayout);

        // 컴포넌트 초기화
        subjectLabel = new JLabel("메일 제목:", JLabel.RIGHT);
        senderLabel = new JLabel("보낸 사람:", JLabel.RIGHT);
        receiverLabel = new JLabel("받는 사람:", JLabel.RIGHT);
        dateLabel = new JLabel("날짜:", JLabel.RIGHT);
        contentLabel = new JLabel("내용:", JLabel.RIGHT);

        subject = new JTextField(20);
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

        content = new JTextArea(5, 20);
        content.setEditable(false);
        content.setLineWrap(true);
        content.setWrapStyleWord(true);
        content.setText(mailInfo.getContent());

        // content에만 스크롤 추가
        JScrollPane contentScroll = new JScrollPane(content);
        contentScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // GridBagLayout에 컴포넌트 추가
        insertGridBagLayout(subjectLabel, 0, 0, 1, 1);
        insertGridBagLayout(subject, 1, 0, 10, 1);

        insertGridBagLayout(senderLabel, 0, 3, 1, 1);
        insertGridBagLayout(senderAddress, 1, 3, 10, 1);

        insertGridBagLayout(receiverLabel, 0, 5, 1, 1);
        insertGridBagLayout(receiverAddress, 1, 5, 10, 1);

        insertGridBagLayout(dateLabel, 0, 7, 1, 1);
        insertGridBagLayout(mailDate, 1, 7, 10, 1);

        insertGridBagLayout(contentLabel, 0, 9, 1, 1);
        insertGridBagLayout(contentScroll, 1, 10, 10, 5); // content에만 스크롤 추가
    }

    // GridBagLayout에 컴포넌트를 추가하는 메서드
    public void insertGridBagLayout(JComponent c, int x, int y, int w, int h) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = x;
        gridBagConstraints.gridy = y;
        gridBagConstraints.gridwidth = w;
        gridBagConstraints.gridheight = h;
        gridBagLayout.setConstraints(c, gridBagConstraints);
        add(c);
    }
}
