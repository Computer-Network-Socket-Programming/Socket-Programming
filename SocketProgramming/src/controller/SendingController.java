package controller;

import model.MailDTO;
import util.SmtpStatusCode;

import java.io.*;
import java.net.Socket;

public class SendingController {

    private final int port;
    private final String username, password;
    private final String smtpServer;

    public SendingController(String username, String password) {
        this.port = 465;
        this.username = username;
        this.password = password;

        switch (username.split("@")[1]) {
            case "naver.com" -> this.smtpServer = "smtp.naver.com";
            case "google.com" -> this.smtpServer = "smtp.google.com";
            case "yahoo.com" -> this.smtpServer = "smtp.yahoo.com";
            default -> this.smtpServer = null;
        }
    }

    public SmtpStatusCode SendMailByNaverSMTP(MailDTO mailDTO) throws IOException {
        Socket socket = new Socket(smtpServer, port);
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.println(reader.readLine());

        reader.close();
        writer.close();
        socket.close();

        return SmtpStatusCode.SERVICE_CLOSING;
    }
}
