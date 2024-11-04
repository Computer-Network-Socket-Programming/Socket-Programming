package controller;

import model.SendMailDTO;
import util.commands.SmtpCommand;
import util.enums.MailPlatform;
import util.enums.SmtpStatusCode;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.List;

public class SmtpController {

    private final String senderAddress, password;
    private final MailPlatform mailPlatform;

    public SmtpController(String senderAddress, String password) {
        this.password = password;
        this.senderAddress = senderAddress;

        // 이메일 주소의 도메인에 따라 SMTP 서버를 결정
        switch (senderAddress.split("@")[1]) {
            case "naver.com" -> this.mailPlatform = MailPlatform.NAVER;
            case "google.com" -> this.mailPlatform = MailPlatform.GMAIL;
            default -> this.mailPlatform = null;
        }
    }

    public SmtpStatusCode scheduleEmailSend(SendMailDTO sendMailDTO) throws IOException, InterruptedException {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return sendMail(sendMailDTO);
    }

    public SmtpStatusCode authenticate(String username, String password) throws IOException {
        List<String> commands = SmtpCommand.createAuthCommands(username, password);
        SSLSocket sslSocket = createSSLSocket();
        DataOutputStream outToServer = new DataOutputStream(sslSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
        String responseValue = inFromServer.readLine(); // 서버 응답 확인

        System.out.println("Response: " + responseValue);

        for(String command : commands) {
            // 명령어 전송
            outToServer.writeBytes(command);
            outToServer.flush(); // 명령 전송 후 플러시하여 보냄
            System.out.println("SmtpCommand: " + command);

            responseValue = inFromServer.readLine(); // 서버 응답 확인
            String statusCode = responseValue.split(" ")[0];
            System.out.println("Response: " + responseValue);

            // 예외 처리
            if (statusCode.equals("535")) {
                return SmtpStatusCode.NOT_ACCEPTED;
            }

            // 그 이외의 예외 처리
            if (responseValue.startsWith("5")) {
                return SmtpStatusCode.NOT_ACCEPTED;
            }
        }

        return SmtpStatusCode.SERVICE_CLOSING;
    }

    /*
     * 메일을 전송하는 메소드
     * @param sendMailDTO 메일 정보를 담은 DTO
     * @return 전송 결과를 나타내는 SmtpStatusCode
     */
    public SmtpStatusCode sendMail(SendMailDTO sendMailDTO) throws IOException, InterruptedException {
        List<String> commands = SmtpCommand.createCommands(this.senderAddress, this.password, sendMailDTO);
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
            System.out.println("SmtpCommand: " + commands.get(i));

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
                String statusCode = responseValue.split(" ")[0];
                System.out.println("Response: " + responseValue);

                // 예외 처리
                switch (statusCode) {
                    case "535":
                        return SmtpStatusCode.NOT_ACCEPTED;
                    case "553":
                        return SmtpStatusCode.RECIPIENT_NOT_FOUND;
                    case "221":
                        return SmtpStatusCode.SERVICE_CLOSING;
                    case "421":
                        return SmtpStatusCode.SERVICE_NOT_AVAILABLE;
                }

                // 그 이외의 예외 처리
                if (responseValue.startsWith("5")) {
                    return SmtpStatusCode.SYNTAX_ERROR;
                } else if (responseValue.startsWith("4")) {
                    return SmtpStatusCode.SERVICE_NOT_AVAILABLE;
                }
            }
        }

        inFromServer.close();
        outToServer.close();
        sslSocket.close();
        return SmtpStatusCode.SERVICE_CLOSING;
    }

    /*
        * SSL 소켓을 생성하는 메소드
        * SSL port = 465
        * @return 생성된 SSL 소켓
     */
    private SSLSocket createSSLSocket() throws IOException {
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        return (SSLSocket) sslSocketFactory.createSocket(this.mailPlatform.getSmtpServer(), 465);
    }
}
