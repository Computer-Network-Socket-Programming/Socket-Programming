package controller;

import model.SendMailDTO;
import util.commands.SmtpCommand;
import util.enums.MailPlatform;
import util.enums.SmtpStatusCode;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.List;

/*
 * SMTP 서버와 통신하는 클래스
 * @param senderAddress 발신자 이메일 주소
 * @param password 발신자 이메일 비밀번호
 * @param mailPlatform 은 senderAddress 도메인에 따라 SMTP 서버를 결정
 */
public class SmtpController {

    private final String senderAddress, password;
    private final MailPlatform mailPlatform;

    public SmtpController(String senderAddress, String password) {
        this.password = password;
        this.senderAddress = senderAddress;

        // 이메일 주소의 도메인에 따라 SMTP 서버를 결정
        switch (senderAddress.split("@")[1]) {
            case "naver.com" -> this.mailPlatform = MailPlatform.NAVER;
            case "gmail.com" -> this.mailPlatform = MailPlatform.GMAIL;
            default -> this.mailPlatform = null;
        }
    }

    /*
     * SMTP 서버와 인증하는 메소드
     * @return 인증 결과를 나타내는 SmtpStatusCode
     * @throws IOException
     */
    public SmtpStatusCode authenticate() throws IOException {
        List<String> commands = SmtpCommand.createAuthCommands(this.senderAddress, this.password, this.mailPlatform);   // 인증 명령어 생성
        SSLSocket sslSocket = createSSLSocket();    // SSL 소켓 생성
        DataOutputStream outToServer = new DataOutputStream(sslSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
        String responseValue = inFromServer.readLine(); // 서버 응답 확인

        System.out.println("Response: " + responseValue);

        for (String command : commands) {
            // 명령어 전송
            outToServer.writeBytes(command);
            outToServer.flush(); // 명령 전송 후 플러시하여 보냄
            System.out.println("SmtpCommand: " + command);

            // 서버 응답 확인
            responseValue = inFromServer.readLine();
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
        List<String> commands = SmtpCommand.createCommands(this.senderAddress, this.password, sendMailDTO, this.mailPlatform);
        SSLSocket sslSocket = createSSLSocket();
        DataOutputStream outToServer = new DataOutputStream(sslSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
        boolean isData = false; // DATA 명령어인 경우 서버의 응답을 받을 필요 없어서 boolean 변수로 처리
        String responseValue = inFromServer.readLine(); // 서버 응답 확인

        System.out.println("Response: " + responseValue);

        // 메일 전송
        for (int i = 0; i < commands.size(); i++) {
            // 명령어 전송
            outToServer.writeBytes(commands.get(i));
            outToServer.flush(); // 명령 전송 후 플러시하여 보냄
            System.out.println("SmtpCommand: " + commands.get(i));

            // DATA 명령어인 경우 서버의 응답을 받을 필요 없이 본문과 파일을 전송하기 때문에 isData 를 true 로 변경
            if (i > 0 && commands.get(i - 1).contains("DATA\r\n")) {
                isData = true;
            }
            // 본문과 파일 전송이 끝난 경우 다시 서버의 응답을 받기 위해 isData 를 false 로 변경
            else if (commands.get(i).contains("\r\n.\r\n")) {
                isData = false;
            }

            // DATA 명령어인 경우 서버의 응답을 받을 필요 없이 본문과 파일을 전송하기 때문에 continue
            if (isData)
                continue;

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
                return SmtpStatusCode.RECIPIENT_NOT_FOUND;
            } else if (responseValue.startsWith("4")) {
                return SmtpStatusCode.SERVICE_NOT_AVAILABLE;
            }

        }

        // 스트림과 소켓 닫기
        inFromServer.close();
        outToServer.close();
        sslSocket.close();

        // 메일 전송 완료
        return SmtpStatusCode.SERVICE_CLOSING;
    }

    /*
     * SSL 소켓을 생성하는 메소드
     * SSL port = 465
     * @return 생성된 SSL 소켓
     */
    private SSLSocket createSSLSocket() throws IOException {
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        // 유효하지 않은 SMTP 서버인 경우 예외 처리
        if (this.mailPlatform == null) {
            throw new IllegalArgumentException("Invalid email address");
        }
        // SMTP 서버에 맞는 SSL 소켓 생성
        return (SSLSocket) sslSocketFactory.createSocket(this.mailPlatform.getSmtpServer(), 465);
    }
}
