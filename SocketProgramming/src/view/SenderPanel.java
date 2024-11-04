package view;

import controller.SendingController;
import model.MailDTO;
import util.enums.SmtpStatusCode;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SenderPanel extends JPanel {

    private final SendingController senderController;
    private final JTextField receiverField, subjectField;  // 받는사람, 제목 field
    private final JTextArea messageArea;    // 본문 field
    private final ArrayList<File> attachedFiles;

    public SenderPanel(String senderAddress, String password) {
        this.receiverField = new JTextField();
        this.subjectField = new JTextField();
        this.messageArea = new JTextArea();
        this.senderController = new SendingController(senderAddress, password);
        this.attachedFiles = new ArrayList<>();

        initPanel();
    }

    // Class Panel 의 Layout 을 설정하고 form panel 과 button panel 을 추가하는 메소드
    private void initPanel() {
        setLayout(new BorderLayout());
        add(createFormPanel(), BorderLayout.CENTER);
        add(createCheckButton(), BorderLayout.SOUTH);
    }

    /*
     * form panel 을 생성하는 메소드
     * form panel 은 4개의 text field 로 구성되어 있음
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(4, 1));
        JPanel receiverField = createTextField("받는 사람 이메일:", this.receiverField);
        JPanel subjectField = createTextField("제목:", this.subjectField);
        JPanel messageField = createTextArea("메시지:", this.messageArea);
        JPanel fileSelectionPanel = createFileSelectionPanel();

        formPanel.add(receiverField);
        formPanel.add(subjectField);
        formPanel.add(messageField);
        formPanel.add(fileSelectionPanel);
        return formPanel;
    }

    /*
     * button panel 을 생성하는 메소드
     * button panel 은 보내기 버튼과 취소 버튼으로 구성되어 있음
     */
    private JPanel createCheckButton() {
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

    private JPanel createFileSelectionPanel() {
        JPanel result = new JPanel(new BorderLayout());
        JButton fileSelectionButton = new JButton("파일 선택");
        JButton fileDeleteButton = new JButton("파일 삭제");
        JTextArea filePathArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(filePathArea);

        fileSelectionButton.addActionListener(e -> selectFile(filePathArea));
        fileDeleteButton.addActionListener(e -> deleteFile(filePathArea));

        result.add(fileSelectionButton, BorderLayout.WEST);
        result.add(fileDeleteButton, BorderLayout.EAST);
        result.add(scrollPane, BorderLayout.CENTER);
        return result;
    }

    /*
     * 파일 선택 버튼을 클릭했을 때 호출되는 메소드
     * JFileChooser 를 이용하여 파일을 선택하고 선택된 파일을 attachedFiles 에 추가함
     * 선택된 파일의 이름을 filePathArea 에 추가함
     */
    private void selectFile(JTextArea filePathArea) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            StringBuilder fileNames = new StringBuilder();

            for (File file : fileChooser.getSelectedFiles()) {
                this.attachedFiles.add(file);
                fileNames.append(file.getName()).append("\n");
            }

            filePathArea.append(fileNames.toString());
        }
    }

    /*
        * 파일 삭제 버튼을 클릭했을 때 호출되는 메소드
        * attachedFiles 에 저장된 파일을 모두 삭제함
        * filePathArea 의 내용을 삭제함
     */
    private void deleteFile(JTextArea filePathArea) {
        if (this.attachedFiles.isEmpty()) return;

        this.attachedFiles.clear();
        filePathArea.setText("");
    }

    /*
     * 보내기 버튼을 클릭했을 때 호출되는 메소드
     * receiverField, subjectField, messageArea 에 입력된 값을 가져와서
     * controller 의 sendMail method 를 호출하여 메일을 전송함
     * 메일 전송 후 메일이 전송되었다는 팝업 메시지를 띄움
     */
    private void sendEmail() {
        MailDTO mailDTO = new MailDTO(this.receiverField.getText(), this.subjectField.getText(), this.messageArea.getText(), this.attachedFiles, LocalDateTime.now());
        SmtpStatusCode statusCode;

        try {
            statusCode = senderController.sendMail(mailDTO);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "서버 연결에 실패했습니다.");
            return;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "발신자 또는 파일 인증에 실패했습니다.");
            return;
        }

        if (statusCode != SmtpStatusCode.SERVICE_CLOSING) {
            JOptionPane.showMessageDialog(this, statusCode.getDescription());
            return;
        }

        JOptionPane.showMessageDialog(this, "메일이 전송되었습니다!");
    }
}