package view;

import controller.SenderController;
import model.MailDTO;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class SenderPanel extends JPanel {

    private final SenderController senderController;
    private final JTextField senderField, receiverField, subjectField;  // form 에 들어갈 text field
    private final JTextArea messageArea;    // form 에 들어갈 text area

    public SenderPanel() {
        this.senderController = new SenderController();
        this.senderField = new JTextField();
        this.receiverField = new JTextField();
        this.subjectField = new JTextField();
        this.messageArea = new JTextArea();

        initPanel();
    }

    /*
        * Class Panel 의 Layout 을 설정하고 form panel 과 button panel 을 추가하는 메소드
     */
    private void initPanel()
    {
        setLayout(new BorderLayout());
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButton(), BorderLayout.SOUTH);
    }

    /*
        * form panel 을 생성하는 메소드
        * form panel 은 4개의 text field 로 구성되어 있음
     */
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

    /*
        * button panel 을 생성하는 메소드
        * button panel 은 보내기 버튼과 취소 버튼으로 구성되어 있음
     */
    private JPanel createButton() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        JButton senderButton = new JButton("보내기");
        JButton cancelButton = new JButton("취소");

        senderButton.addActionListener(e -> sendEmail());
        buttonPanel.add(senderButton);
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }

    /*
        * text field 를 생성하는 메소드
        * text field 는 label 과 text field 로 구성되어 있음
     */
    private JPanel createTextField(String text, JTextField textField) {
        JPanel textFieldPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(text);

        textFieldPanel.add(label, BorderLayout.WEST);
        textFieldPanel.add(textField, BorderLayout.CENTER);
        return textFieldPanel;
    }

    /*
        * text area 를 생성하는 메소드
        * text area 는 label 과 text area 로 구성되어 있음
     */
    private JPanel createTextArea(String text, JTextArea textField) {
        JPanel textAreaPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(textField);
        JLabel label = new JLabel(text);

        textAreaPanel.add(label, BorderLayout.WEST);
        textAreaPanel.add(scrollPane, BorderLayout.CENTER);
        return textAreaPanel;
    }

    /*
        * 보내기 버튼을 클릭했을 때 호출되는 메소드
        * senderField, receiverField, subjectField, messageArea 에 입력된 값을 가져와서
        * controller 의 sendMail method 를 호출하여 메일을 전송함
        * 메일 전송 후 메일이 전송되었다는 팝업 메시지를 띄움
     */
    private void sendEmail() {
        String sender = senderField.getText();
        String recipient = receiverField.getText();
        String subject = subjectField.getText();
        String message = messageArea.getText();

        senderController.sendMail(new MailDTO(sender, recipient, subject, message, LocalDateTime.now()));
        JOptionPane.showMessageDialog(this, "메일이 전송되었습니다!");
    }
}