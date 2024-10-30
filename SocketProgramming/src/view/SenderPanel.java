package view;

import controller.SenderController;
import model.MailDTO;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class SenderPanel extends JPanel {

    private final SenderController senderController;
    private final JTextField senderField, receiverField, subjectField;
    private final JTextArea messageArea;

    public SenderPanel() {
        this.senderController = new SenderController();
        this.senderField = new JTextField();
        this.receiverField = new JTextField();
        this.subjectField = new JTextField();
        this.messageArea = new JTextArea();

        initPanel();
    }

    private void initPanel()
    {
        setLayout(new BorderLayout());
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButton(), BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(4, 1));
        JPanel senderField = createTextField("보낸 사람 이메일:", this.senderField);
        JPanel receiverField = createTextField("받는 사람 이메일:", this.receiverField);
        JPanel subjectField = createTextField("제목:", this.subjectField);
        JPanel messageField = createTextArea("메시지:", this.messageArea);

        formPanel.add(senderField);
        formPanel.add(receiverField);
        formPanel.add(subjectField);
        formPanel.add(messageField);
        return formPanel;
    }

    private JPanel createTextField(String text, JTextField textField) {
        JPanel textFieldPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(text);

        textFieldPanel.add(label, BorderLayout.WEST);
        textFieldPanel.add(textField, BorderLayout.CENTER);
        return textFieldPanel;
    }

    private JPanel createTextArea(String text, JTextArea textField) {
        JPanel textAreaPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(textField);
        JLabel label = new JLabel(text);

        textAreaPanel.add(label, BorderLayout.WEST);
        textAreaPanel.add(scrollPane, BorderLayout.CENTER);
        return textAreaPanel;
    }

    private JPanel createButton() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        JButton senderButton = new JButton("보내기");
        JButton cancelButton = new JButton("취소");

        senderButton.addActionListener(e -> sendEmail());
        buttonPanel.add(senderButton);
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }

    private void sendEmail() {
        String sender = senderField.getText();
        String recipient = receiverField.getText();
        String subject = subjectField.getText();
        String message = messageArea.getText();

        senderController.sendMail(new MailDTO(sender, recipient, subject, message, LocalDateTime.now()));
        JOptionPane.showMessageDialog(this, "메일이 전송되었습니다!");
    }
}