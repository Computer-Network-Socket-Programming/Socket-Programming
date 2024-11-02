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
            if (i == 2 || i == 3)
                outToServer.writeBytes(Base64.getEncoder().encodeToString(command[i].getBytes()));
            else
                outToServer.writeBytes(command[i]);

            Thread.sleep(1000);
            System.out.println("i = " + i);
            System.out.println("Command: " + command[i]);
            Thread.sleep(1000);
            String responseValue = inFromServer.readLine();
            System.out.println("Response: " + responseValue);
            Thread.sleep(1000);
            if (responseValue.startsWith("5")) {
                System.out.println(responseValue);
                return SmtpStatusCode.SYNTAX_ERROR;
            }
            else if (responseValue.startsWith("4")) {
                System.out.println(responseValue);
                return SmtpStatusCode.SERVICE_NOT_AVAILABLE;
            }

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
                "HELO " + this.senderAddress,
                "AUTH LOGIN",
                this.senderAddress,
                this.password,
                "MAIL FROM:<" + this.senderAddress + ">",
                "RCPT TO:<" + mailDTO.recipient() + ">",
                "DATA",
                "Subject: " + mailDTO.subject() + "\r\n" + "To: " + mailDTO.recipient() + "\r\n" + "From: " + this.senderAddress + "\r\n" + "\r\n" + mailDTO.message() + "\r\n.\r\n"
        };
    }
}
