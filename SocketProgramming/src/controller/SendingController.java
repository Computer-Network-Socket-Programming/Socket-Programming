package controller;

import model.MailDTO;
import util.MailPlatform;
import util.SmtpStatusCode;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class SendingController {

    private final int port;
    private final String senderAddress, password;
    private final MailPlatform smtpServer;

    public SendingController(String senderAddress, String password) {
        this.port = 465;    // SSL 포트
        this.password = password;
        this.senderAddress = senderAddress;

        // 이메일 주소의 도메인에 따라 SMTP 서버를 결정
        switch (senderAddress.split("@")[1]) {
            case "naver.com" -> this.smtpServer = MailPlatform.NAVER;
            case "google.com" -> this.smtpServer = MailPlatform.GMAIL;
            default -> this.smtpServer = null;
        }
    }

    /*
     * 메일을 전송하는 메소드
     * @param mailDTO 메일 정보를 담은 DTO
     * @return 전송 결과를 나타내는 SmtpStatusCode
     */
    public SmtpStatusCode sendMail(MailDTO mailDTO) throws IOException, InterruptedException {
        List<String> commands = createCommands(mailDTO);
        SSLSocket sslSocket = createSSLSocket();
        DataOutputStream outToServer = new DataOutputStream(sslSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
        boolean isData = false;
        String responseValue = inFromServer.readLine(); // 서버 응답 확인

        System.out.println("Response: " + responseValue);

        // 메일 전송
        for (int i = 0; i < commands.size(); i++) {
            // 명령어 전송
            outToServer.writeBytes(commands.get(i));
            outToServer.flush(); // 명령 전송 후 플러시하여 보냄
            System.out.println("Command: " + commands.get(i));

            // DATA 명령어인 경우 데이터 전송 상태로 변경
            if (i > 1 && commands.get(i - 1).contains("DATA\r\n")) {
                isData = true;
            }
            // 데이터 전송이 끝난 경우
            else if (commands.get(i).contains("\r\n.\r\n")) {
                isData = false;
            }

            if (!isData) {
                responseValue = inFromServer.readLine(); // 서버 응답 확인
                System.out.println("Response: " + responseValue);

                // 예외 처리
                if (responseValue.startsWith("5")) {
                    return SmtpStatusCode.SYNTAX_ERROR;
                } else if (responseValue.startsWith("4")) {
                    return SmtpStatusCode.SERVICE_NOT_AVAILABLE;
                }
            }
        }

        responseValue = inFromServer.readLine(); // 서버 응답 확인
        System.out.println("Response: " + responseValue);

        inFromServer.close();
        outToServer.close();
        sslSocket.close();
        return SmtpStatusCode.SERVICE_CLOSING;
    }

    // SSL 소켓 생성
    private SSLSocket createSSLSocket() throws IOException {
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        return (SSLSocket) sslSocketFactory.createSocket(this.smtpServer.getSmtpServer(), this.port);
    }

    // SMTP 명령어 생성
    private ArrayList<String> createCommands(MailDTO mailDTO) throws IOException {
        String boundary = "----=_NextPart_" + System.currentTimeMillis();
        ArrayList<String> commands = new ArrayList<>();

        // SMTP 명령어 생성
        commands.add("HELO " + this.senderAddress + "\r\n");
        commands.add("AUTH LOGIN" + "\r\n");
        commands.add(Base64.getEncoder().encodeToString(this.senderAddress.getBytes()) + "\r\n");
        commands.add(Base64.getEncoder().encodeToString(this.password.getBytes()) + "\r\n");
        commands.add("MAIL FROM:<" + this.senderAddress + ">" + "\r\n");
        commands.add("RCPT TO:<" + mailDTO.recipient() + ">" + "\r\n");
        commands.add("DATA" + "\r\n");

        // 첨부 파일이 없고 본문만 있는 경우
        if (mailDTO.attachedFiles() == null || mailDTO.attachedFiles().isEmpty()) {
            commands.add("Subject: " + mailDTO.subject() + "\r\n");
            commands.add("To: " + mailDTO.recipient() + "\r\n");
            commands.add("From: " + this.senderAddress + "\r\n");
            commands.add("\r\n");
            commands.add(mailDTO.message());
        }
        // 첨부파일이 있는 경우 multipart/mixed 로 생성
        else {
            // 헤더와 본문
            commands.add("Content-Type: multipart/mixed; boundary=\"" + boundary + "\"\r\n");
            commands.add("\r\n--" + boundary + "\r\n");
            commands.add("Content-Type: text/plain; charset=\"UTF-8\"\r\n");
            commands.add("Content-Transfer-Encoding: 7bit\r\n");
            commands.add("\r\n" + mailDTO.message() + "\r\n");

            // 파일 첨부
            for (File file : mailDTO.attachedFiles()) {
                commands.add("--" + boundary + "\r\n");
                commands.add("Content-Type: " + getMimeType(file) + "; name=\"" + file.getName() + "\"\r\n");
                commands.add("Content-Transfer-Encoding: base64\r\n");
                commands.add("Content-Disposition: attachment; filename=\"" + file.getName() + "\"\r\n");
                commands.add("\r\n" + encodeFile(file) + "\r\n");
            }

            // 종료 부분
            commands.add("--" + boundary + "--\r\n");
        }

        commands.add("\r\n.\r\n");
        commands.add("QUIT" + "\r\n");
        return commands;
    }

    // 파일 확장자 별로 MIME 타입을 반환
    private String getMimeType(File file) {
        if (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (file.getName().endsWith(".png")) {
            return "image/png";
        } else if (file.getName().endsWith(".pdf")) {
            return "application/pdf";
        }
        return "application/octet-stream"; // 기본값은 바이너리 파일로 가정
    }

    private String encodeFile(File file) throws IOException {
        byte[] fileContent = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);

        fileInputStream.read(fileContent);
        return Base64.getEncoder().encodeToString(fileContent);
    }
}
