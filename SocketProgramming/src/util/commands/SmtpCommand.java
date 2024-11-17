package util.commands;

import model.SendMailDTO;
import util.enums.MailPlatform;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

public class SmtpCommand {

    // SMTP 인증 명령어 생성
    public static ArrayList<String> createAuthCommands(String senderAddress, String password, MailPlatform mailPlatform) throws IOException {
        ArrayList<String> commands = new ArrayList<>();

        commands.add("HELO " + mailPlatform.getSmtpServer() + "\r\n");
        commands.add("AUTH LOGIN" + "\r\n");
        commands.add(encodeText(senderAddress.getBytes()) + "\r\n");
        commands.add(encodeText(password.getBytes()) + "\r\n");
        commands.add("QUIT\r\n");
        return commands;
    }

    /*
     * 이메일 전송 명령어 생성
     * @param senderAddress 발신자 이메일 주소
     * @param password 발신자 이메일 비밀번호
     * @param sendMailDTO 메일 정보를 담은 DTO
     * @return SMTP 명령어 목록
     * createAuthCommands 메소드를 통해 SMTP 인증 명령어 생성
     */
    public static ArrayList<String> createCommands(String senderAddress, String password, SendMailDTO sendMailDTO, MailPlatform mailPlatform) throws IOException {
        String boundary = "----=_NextPart_" + System.currentTimeMillis();   // multipart boundary
        ArrayList<String> commands = new ArrayList<>(createAuthCommands(senderAddress, password, mailPlatform));

        commands.remove(commands.size() - 1); // QUIT 명령어 제거
        commands.add("MAIL FROM:<" + senderAddress + ">\r\n");  // 발신자 이메일 주소 설정
        commands.add("RCPT TO:<" + sendMailDTO.recipient() + ">\r\n");  // 수신자 이메일 주소 설정
        commands.add("DATA\r\n");   // 데이터 전송 명령어

        // 이메일 헤더 설정
        commands.add("Subject: =?utf-8?B?" + encodeText(sendMailDTO.subject().getBytes(StandardCharsets.UTF_8)) + "?=\r\n");    // 제목
        commands.add("To: " + sendMailDTO.recipient() + "\r\n");    // 수신자 이메일 주소
        commands.add("From: " + senderAddress + "\r\n");    // 발신자 이메일 주소
        commands.add("Content-Type: multipart/mixed; boundary=\"" + boundary + "\"\r\n");   // 본문 타입 설정
        commands.add("\r\n");  // 헤더와 본문 구분

        // 본문 시작
        commands.add("--" + boundary + "\r\n");   // boundary 시작 설정
        commands.add("Content-Type: text/plain; charset=\"UTF-8\"\r\n");    // 본문 타입 설정
        commands.add("Content-Transfer-Encoding: base64\r\n");  // 인코딩 설정
        commands.add("\r\n" + encodeText(sendMailDTO.message().getBytes(StandardCharsets.UTF_8)) + "\r\n");   // 본문 추가

        // 첨부 파일 추가
        for (File file : sendMailDTO.attachedFiles()) {
            String encodedFileName = "=?utf-8?B?" + encodeText(file.getName().getBytes(StandardCharsets.UTF_8)) + "?=";   // 파일 이름 인코딩
            commands.add("--" + boundary + "\r\n");  // boundary 시작 설정
            commands.add("Content-Type: " + getMimeType(file) + "; name=\"" + encodedFileName + "\"\r\n");  // 파일 타입 설정
            commands.add("Content-Transfer-Encoding: base64\r\n");  // 인코딩 설정
            commands.add("Content-Disposition: attachment; filename=\"" + encodedFileName + "\"\r\n");  // 파일 이름 설정
            commands.add("\r\n" + encodeFile(file) + "\r\n");   // 파일 추가
        }


        commands.add("--" + boundary + "--\r\n");   // 종료 boundary 추가
        commands.add("\r\n.\r\n");  // 데이터 전송 종료
        commands.add("QUIT\r\n");   // QUIT 명령어 추가
        return commands;
    }

    // 파일 확장자 별로 MIME 타입을 반환
    private static String getMimeType(File file) {
        if (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (file.getName().endsWith(".png")) {
            return "image/png";
        } else if (file.getName().endsWith(".pdf")) {
            return "application/pdf";
        }
        return "application/octet-stream"; // 기본값은 바이너리 파일로 가정
    }

    /*
        * 한글 깨지는 것을 방지하기 위해 Base64로 인코딩
        * 텍스트를 Base64로 인코딩
        * @param text 인코딩할 텍스트
        * @return Base64로 인코딩된 텍스트
     */
    private static String encodeText(byte[] text) {
        return Base64.getEncoder().encodeToString(text);
    }

    // 파일을 Base64로 인코딩
    private static String encodeFile(File file) throws IOException {
        byte[] fileContent = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);

        fileInputStream.read(fileContent);
        return Base64.getEncoder().encodeToString(fileContent);
    }
}
