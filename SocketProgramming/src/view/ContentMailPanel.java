package view;

import model.MailInfoDTO;
import model.ReplyMailDTO;
import model.DeliverMailDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ContentMailPanel extends JPanel {

    JLabel senderLabel, receiverLabel, dateLabel, subjectLabel, contentLabel;
    JTextField senderAddress, receiverAddress, mailDate, subject;
    JTextArea content;
    JScrollPane contentScroll;
    JButton mailListButton;
    JButton replyMailButton;
    JButton mailDeliverButton;
    BorderLayout borderLayout;
    GridLayout gridLayout;
    private int categoryIndex;
    private MailInfoDTO mailInfoDTO;
    private String username;
    private String password;

    public ContentMailPanel(CardLayout cardLayout, JPanel parentPanel) {
        borderLayout = new BorderLayout();
        gridLayout = new GridLayout(0, 2);
        setLayout(borderLayout);

        JPanel mailDetailPanel = new JPanel();
        mailDetailPanel.setLayout(gridLayout);
        add(mailDetailPanel, BorderLayout.NORTH);

        subjectLabel = new JLabel("메일 제목:", JLabel.RIGHT);
        senderLabel = new JLabel("보낸 사람:", JLabel.RIGHT);
        receiverLabel = new JLabel("받는 사람:", JLabel.RIGHT);
        dateLabel = new JLabel("날짜:", JLabel.RIGHT);
        contentLabel = new JLabel("내용", JLabel.LEFT);

        subject = new JTextField(50);
        subject.setEditable(false);

        senderAddress = new JTextField(20);
        senderAddress.setEditable(false);

        receiverAddress = new JTextField(20);
        receiverAddress.setEditable(false);

        mailDate = new JTextField(20);
        mailDate.setEditable(false);

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

        contentScroll = new JScrollPane(content);
        contentScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(contentScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(gridLayout);
        add(buttonPanel, BorderLayout.SOUTH);

        mailListButton = new JButton("목록으로 돌아가기");
        buttonPanel.add(mailListButton);
        mailListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == mailListButton) {
                    cardLayout.show(parentPanel, "infoPanel");
                }
            }
        });

        switch (categoryIndex) {
            case 0: // 받은메일함일 때

                replyMailButton = new JButton("답장");
                buttonPanel.add(replyMailButton);
                replyMailButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (e.getSource() == replyMailButton) {
                            ReplyMailDTO replyMailDTO = new ReplyMailDTO(
                                    mailInfoDTO.receiver(),
                                    "-----Original Message-----\n\n" + mailInfoDTO.content() + "\n\n"
                            );
                            new SenderFrame(username, password, replyMailDTO);
                        }
                    }
                });


            case 1: // 보낸메일함일 때 전달 버튼만 표시
                mailDeliverButton = new JButton("전달");
                buttonPanel.add(mailDeliverButton);
                mailDeliverButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (e.getSource() == mailDeliverButton) {
                            if (mailInfoDTO.subject() == null || mailInfoDTO.subject().isEmpty()) {
                                JOptionPane.showMessageDialog(null, "제목이 유효하지 않습니다.");
                                return;
                            }
                            if (mailInfoDTO.content() == null || mailInfoDTO.content().isEmpty()) {
                                JOptionPane.showMessageDialog(null, "내용이 존재하지 않습니다.");
                                return;
                            }
                            DeliverMailDTO deliverMailDTO = new DeliverMailDTO(
                                    mailInfoDTO.subject(),
                                    mailInfoDTO.content()
                            );
                            new SenderFrame(username, password, deliverMailDTO);
                        }
                    }
                });
                break;

            default:
                break;
        }
    }

    public void updateValue(String[] value, int index, String username, String password) {
        this.mailInfoDTO = new MailInfoDTO(value[0], value[1], value[2], value[3], value[4]);
        this.categoryIndex = index;
        this.password = password;
        this.username = username;

        senderAddress.setText(mailInfoDTO.sender());
        receiverAddress.setText(mailInfoDTO.receiver());
        subject.setText(mailInfoDTO.subject());
        mailDate.setText(mailInfoDTO.date());
        content.setText(mailInfoDTO.content());
    }

}
