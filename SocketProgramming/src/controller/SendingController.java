package controller;

import model.MailDTO;
import util.MailPlatform;
import util.SmtpStatusCode;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.Base64;

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
        String[] command = createCommand(mailDTO);
        SSLSocket sslSocket = createSSLSocket();
        DataOutputStream outToServer = new DataOutputStream(sslSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));

        // 메일 전송
        for (int i = 0; i < command.length; i++) {
            String responseValue = inFromServer.readLine(); // 서버 응답 확인
//            System.out.println("Response: " + responseValue);

            // 예외 처리
            if (responseValue.startsWith("5")) {
                return SmtpStatusCode.SYNTAX_ERROR;
            } else if (responseValue.startsWith("4")) {
                return SmtpStatusCode.SERVICE_NOT_AVAILABLE;
            }

            outToServer.writeBytes(command[i]); // 명령 전송
            outToServer.flush(); // 명령 전송 후 플러시하여 보냄

//            System.out.println("i = " + i);
//            System.out.println("Command: " + command[i]);
        }

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
    private String[] createCommand(MailDTO mailDTO) {
        return new String[]{
                "HELO " + this.senderAddress + "\r\n",
                "AUTH LOGIN" + "\r\n",
                Base64.getEncoder().encodeToString(this.senderAddress.getBytes()) + "\r\n",
                Base64.getEncoder().encodeToString(this.password.getBytes()) + "\r\n",
                "MAIL FROM:<" + this.senderAddress + ">" + "\r\n",
                "RCPT TO:<" + mailDTO.recipient() + ">" + "\r\n",
                "DATA" + "\r\n",
                "Subject: " + mailDTO.subject() + "\r\n" + "To: " + mailDTO.recipient() + "\r\n" + "From: " + this.senderAddress + "\r\n" + "\r\n" + mailDTO.message() + "\r\n.\r\n",
                "QUIT" + "\r\n"
        };
    }
}
