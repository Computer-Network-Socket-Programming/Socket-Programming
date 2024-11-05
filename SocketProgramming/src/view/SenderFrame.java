package view;

import controller.SmtpController;
import model.DeliverMailDTO;
import model.SendMailDTO;
import model.ReplyMailDTO;
import util.enums.SmtpStatusCode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

public class SenderFrame extends JFrame {

    private final SmtpController senderController;
    private final JTextField receiverField, subjectField;  // 받는사람, 제목 field
    private final JTextArea messageArea;    // 본문 field
    private final ArrayList<File> attachedFiles;

    // 새로운 메일 쓰기 버튼을 눌렀을 때 호출 되는 생성자
    public SenderFrame(String senderAddress, String password) {
        this.receiverField = new JTextField();
        this.subjectField = new JTextField();
        this.messageArea = new JTextArea();
        this.senderController = new SmtpController(senderAddress, password);
        this.attachedFiles = new ArrayList<>();

        initFrame();

        setTitle("Send Mail");
        setSize(400, 400);
        setVisible(true);
    }

    // 전달 버튼을 눌렀을 때 호출 되는 생성자
    public SenderFrame(String senderAddress, String password, DeliverMailDTO deliverMailDTO) {
        this(senderAddress, password);

        if (deliverMailDTO.subject() != null && !deliverMailDTO.subject().isEmpty()) {
            this.subjectField.setText(deliverMailDTO.subject());
        }
        if (deliverMailDTO.content() != null && !deliverMailDTO.content().isEmpty()) {
            this.messageArea.setText(deliverMailDTO.content());
        }
    }

    // 답장 버튼을 눌렀을 때 호출 되는 생성자
    public SenderFrame(String senderAddress, String password, ReplyMailDTO replyMailDTO) {
        this(senderAddress, password);

        if (replyMailDTO.recipient() != null && !replyMailDTO.recipient().isEmpty()) {
            this.receiverField.setText(replyMailDTO.recipient());
            this.receiverField.setEditable(false);
        }

        if (replyMailDTO.message() != null && !replyMailDTO.message().isEmpty()) {
            this.messageArea.setText(replyMailDTO.message());
        }
    }

    // Frame Class 의 Layout 을 설정하고 form panel 과 button panel 을 추가하는 메소드
    private void initFrame() {
        setLayout(new BorderLayout());
        add(createFormPanel(), BorderLayout.CENTER);
        add(createCheckButton(), BorderLayout.SOUTH);
    }

    /*
     * form panel 을 생성하는 메소드
     * form panel 은 4개의 text field 로 구성되어 있음
     */
    private JPanel createFormPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(2, 1));
        JPanel formPanel = new JPanel(new GridLayout(3, 1));

        JPanel subjectField = createTextField("                제목:", this.subjectField);
        JPanel receiverField = createTextField("받는 사람 이메일:", this.receiverField);
        JPanel fileSelectionPanel = createFileSelectionPanel();
        JPanel messageField = createTextArea("             메시지:", this.messageArea);

        formPanel.add(receiverField);
        formPanel.add(subjectField);
        formPanel.add(fileSelectionPanel);
        mainPanel.add(formPanel);
        mainPanel.add(messageField);
        return mainPanel;
    }

    /*
     * button panel 을 생성하는 메소드
     * button panel 은 보내기 버튼과 취소 버튼으로 구성되어 있음
     */
    private JPanel createCheckButton() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        JButton senderButton = new JButton("보내기");
        JButton reservationSenderButton = new JButton("10초 후 보내기");
        JButton cancelButton = new JButton("취소");

        senderButton.addActionListener(e -> sendButtonOnClick(e));
        reservationSenderButton.addActionListener(e -> sendButtonOnClick(e));
        cancelButton.addActionListener(e -> this.setVisible(false));

        buttonPanel.add(senderButton);
        buttonPanel.add(reservationSenderButton);
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

        filePathArea.setEditable(false);
        filePathArea.setLineWrap(true);
        filePathArea.setBackground(Color.LIGHT_GRAY);

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
        StringBuilder fileNames = new StringBuilder();

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            this.attachedFiles.addAll(Arrays.asList(fileChooser.getSelectedFiles()));
        }

        for (File file : attachedFiles) {
            fileNames.append(file.getName()).append("\n");
        }
        filePathArea.append(fileNames.toString());
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

    private void sendButtonOnClick(ActionEvent e) {
        ArrayList<SendMailDTO> sendMailDTOs = createSendMailDTOs();

        if (e.getActionCommand().equals("10초 후 보내기")) {
            JOptionPane.showMessageDialog(this, "10초 후에 메일이 전송됩니다.");

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    // 10초 대기
                    Thread.sleep(10000);
                    sendEmails(sendMailDTOs);
                    dispose();
                    return null;
                }
            }.execute();
        } else {
            sendEmails(sendMailDTOs);
            dispose();
        }
    }

    /*
     * 보내기 버튼을 클릭했을 때 호출되는 메소드
     * receiverField, subjectField, messageArea 에 입력된 값을 가져와서
     * controller 의 sendMail method 를 호출하여 메일을 전송함
     * 메일 전송 후 메일이 전송되었다는 팝업 메시지를 띄움
     */
    private void sendEmails(ArrayList<SendMailDTO> sendMailDTOs) {
        SmtpStatusCode statusCode = null;

        try {
            for (SendMailDTO sendMailDTO : sendMailDTOs) {
                statusCode = senderController.sendMail(sendMailDTO);
            }
        } catch (UnknownHostException exception) {
            exception.printStackTrace();
            JOptionPane.showMessageDialog(this, "서버 연결에 실패했습니다.");
            return;
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
            JOptionPane.showMessageDialog(this, "발신자 또는 파일 인증에 실패했습니다.");
            return;
        }

        if (statusCode != SmtpStatusCode.SERVICE_CLOSING) {
            if (statusCode == null) {
                JOptionPane.showMessageDialog(this, "메일 전송에 실패했습니다.");
            } else {
                JOptionPane.showMessageDialog(this, statusCode.getDescription());
            }
        } else {
            JOptionPane.showMessageDialog(this, "메일이 전송되었습니다!");
        }
    }

    private ArrayList<SendMailDTO> createSendMailDTOs() {
        ArrayList<SendMailDTO> sendMailDTOs = new ArrayList<>();

        if (this.receiverField.getText().contains(",")) {
            String[] recipients = this.receiverField.getText().split(",");

            for (String recipient : recipients) {
                String r = recipient.trim();

                if (r.startsWith(":")) {
                    r = r.substring(1);
                }

                r = r.trim();

                if (r.startsWith("<")) {
                    int startIndex = r.indexOf("<");
                    int lastIndex = r.lastIndexOf(">");
                    r = r.substring(startIndex + 1, lastIndex);
                }

                sendMailDTOs.add(new SendMailDTO(r, this.subjectField.getText(), this.messageArea.getText(), this.attachedFiles));
            }
        } else {
            String r = this.receiverField.getText().trim();

            if (r.startsWith(":")) {
                r = r.substring(1);
            }

            r = r.trim();

            if (r.startsWith("<")) {
                int startIndex = r.indexOf("<");
                int lastIndex = r.lastIndexOf(">");
                r = r.substring(startIndex + 1, lastIndex);
            }

            sendMailDTOs.add(new SendMailDTO(r, this.subjectField.getText(), this.messageArea.getText(), this.attachedFiles));
        }

        return sendMailDTOs;
    }
}