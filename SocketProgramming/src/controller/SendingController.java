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
        this.port = 465;
        this.password = password;
        this.senderAddress = senderAddress;

        switch (senderAddress.split("@")[1]) {
            case "naver.com" -> this.smtpServer = MailPlatform.NAVER;
            case "google.com" -> this.smtpServer = MailPlatform.GMAIL;
            default -> this.smtpServer = null;
        }
    }

    public SmtpStatusCode sendMail(MailDTO mailDTO) throws IOException, InterruptedException {
        String[] command = createCommand(mailDTO);
        SSLSocket sslSocket = createSSLSocket();
        DataOutputStream outToServer = new DataOutputStream(sslSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));

        for (int i = 0; i < command.length; i++) {
            String responseValue = inFromServer.readLine();
//            System.out.println("Response: " + responseValue);

            if (responseValue.startsWith("5")) {
                return SmtpStatusCode.SYNTAX_ERROR;
            } else if (responseValue.startsWith("4")) {
                return SmtpStatusCode.SERVICE_NOT_AVAILABLE;
            }

            outToServer.writeBytes(command[i]);
            outToServer.flush(); // 명령 전송 후 플러시하여 보냄

//            System.out.println("i = " + i);
//            System.out.println("Command: " + command[i]);
        }

        inFromServer.close();
        outToServer.close();
        sslSocket.close();
        return SmtpStatusCode.SERVICE_CLOSING;
    }

    private SSLSocket createSSLSocket() throws IOException {
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        return (SSLSocket) sslSocketFactory.createSocket(this.smtpServer.getSmtpServer(), this.port);
    }

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
