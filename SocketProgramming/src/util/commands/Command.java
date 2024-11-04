package util.commands;

import model.MailDTO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

public class Command {

    // SMTP 인증 명령어 생성
    public static ArrayList<String> createAuthCommands(String senderAddress, String password) throws IOException {
        ArrayList<String> commands = new ArrayList<>();

        commands.add("HELO " + senderAddress + "\r\n");
        commands.add("AUTH LOGIN" + "\r\n");
        commands.add(encodeText(senderAddress.getBytes()) + "\r\n");
        commands.add(encodeText(password.getBytes()) + "\r\n");
        return commands;
    }

    /*
     * 이메일 전송 명령어 생성
     * @param senderAddress 발신자 이메일 주소
     * @param password 발신자 이메일 비밀번호
     * @param mailDTO 메일 정보를 담은 DTO
     * @return SMTP 명령어 목록
     * createAuthCommands 메소드를 통해 SMTP 인증 명령어 생성
     */
    public static ArrayList<String> createCommands(String senderAddress, String password, MailDTO mailDTO) throws IOException {
        String boundary = "----=_NextPart_" + System.currentTimeMillis();
        ArrayList<String> commands = new ArrayList<>(createAuthCommands(senderAddress, password));
        commands.add("MAIL FROM:<" + senderAddress + ">\r\n");
        commands.add("RCPT TO:<" + mailDTO.recipient() + ">\r\n");
        commands.add("DATA\r\n");

        // 이메일 헤더 설정
        commands.add("Subject: =?utf-8?B?" + encodeText(mailDTO.subject().getBytes(StandardCharsets.UTF_8)) + "?=\r\n");
        commands.add("To: " + mailDTO.recipient() + "\r\n");
        commands.add("From: " + senderAddress + "\r\n");
        commands.add("Content-Type: multipart/mixed; boundary=\"" + boundary + "\"\r\n");
        commands.add("\r\n");

        // 본문 시작
        commands.add("--" + boundary + "\r\n");
        commands.add("Content-Type: text/plain; charset=\"UTF-8\"\r\n");
        commands.add("Content-Transfer-Encoding: base64\r\n");
        commands.add("\r\n" + encodeText(mailDTO.message().getBytes(StandardCharsets.UTF_8)) + "\r\n");

        // 첨부 파일 추가
        for (File file : mailDTO.attachedFiles()) {
            String encodedFileName = "=?utf-8?B?" + encodeText(file.getName().getBytes(StandardCharsets.UTF_8)) + "?=";
            commands.add("--" + boundary + "\r\n");
            commands.add("Content-Type: " + getMimeType(file) + "; name=\"" + encodedFileName + "\"\r\n");
            commands.add("Content-Transfer-Encoding: base64\r\n");
            commands.add("Content-Disposition: attachment; filename=\"" + encodedFileName + "\"\r\n");
            commands.add("\r\n" + encodeFile(file) + "\r\n");
        }

        // 종료 boundary 추가
        commands.add("--" + boundary + "--\r\n");
        commands.add("\r\n.\r\n");
        commands.add("QUIT\r\n");
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
