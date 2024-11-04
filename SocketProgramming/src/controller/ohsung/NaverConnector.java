package controller.ohsung;

import model.ohsung.EmailDataRepository;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NaverConnector {
    private static final String HOST = "imap.naver.com";
    private static final int PORT = 993;

    private SSLSocket socket; //SSL 소켓, 서버와의 SSL 연결 담당
    private BufferedReader reader; //서버 응답을 읽어오는 스트림
    private PrintWriter writer; // 서버로 명령어를 보내는 스트림

    public NaverConnector(String username, String password) throws Exception{
        connect(username, password);
    }

    //네이버 IMAP 서버에 SSL로 연결하고 로그인하는 메서드
    private void connect(String username, String password) throws Exception{

        //SSL 소켓을 생성하여 네이버 IMAP 서버에 연결
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        /**
         * 위: 기본 소켓 팩토리를 얻는 코드, 아래 : 소켓 인스턴스 생성
         */
        socket = (SSLSocket) factory.createSocket(HOST, PORT);

        //서버 응답 읽기, 명령어 보내기 위한 스트림 설정
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);

        //서버 응답 확인
        System.out.println(readResponse());

        //로그인 명령어 전송: "a001"은 명령 태그, 이후 명령어 결과를 구분하는 식별자 역할
        sendCommand("a001 LOGIN " + username + " " + password);
        System.out.println(readResponse());

        //받은 메일함 작업 대상으로 설정
        sendCommand("a002 SELECT INBOX");
        System.out.println(readResponse());
    }

    // 메일 목록을 가져오는 메서드
    public void fetchMails() throws Exception {
        List<String[]> mails = new ArrayList<>();

        // 첫 20개의 메일 헤더 정보를 요청
        sendCommand("a003 FETCH 980:1000 (BODY[HEADER.FIELDS (FROM SUBJECT DATE)] BODY[TEXT])");

        String line;
        String from = "", subject = "", date = "", body = "", mailId = "";
        boolean isReadingMail = false;
        boolean isReadingBody = false;
        StringBuilder bodyBuilder = new StringBuilder();
        long lastNoopTime = System.currentTimeMillis();

        while ((line = reader.readLine()) != null) {
            System.out.println("서버 응답: " + line);  // 서버 응답을 디버깅 목적으로 모두 출력

            // 주기적으로 NOOP 명령 전송
            if (System.currentTimeMillis() - lastNoopTime > 30000) { // 30초마다 NOOP
                sendCommand("NOOP");
                lastNoopTime = System.currentTimeMillis();
                System.out.println("NOOP 명령어 전송하여 타임아웃 방지");
            }

            if (line.startsWith("*") && line.contains("FETCH")) {
                // FETCH 응답에서 메일 ID 추출
                String[] parts = line.split(" ");
                if (parts.length > 1) {
                    mailId = parts[1];
                    isReadingMail = true; // 메일 항목을 읽기 시작
                }
            } else if (isReadingMail && line.toUpperCase().startsWith("FROM:")) {
                from = line.substring(line.indexOf("From:") + 5).trim();
                from = MimeDecoder.getInstance().decodeMimeText(from);
                System.out.println("보낸 사람 파싱: " + from);
            } else if (isReadingMail && line.toUpperCase().startsWith("SUBJECT:")) {
                subject = line.substring(line.indexOf("Subject:") + 8).trim();
                subject = MimeDecoder.getInstance().decodeMimeText(subject);
                System.out.println("제목 파싱: " + subject);
            } else if (isReadingMail && line.toUpperCase().startsWith("DATE:")) {
                date = line.substring(line.indexOf("Date:") + 5).trim();
                date = MimeDecoder.getInstance().decodeMimeText(date);
                System.out.println("날짜 파싱: " + date);
            } else if (isReadingMail && line.contains("BODY[TEXT]")) {
                isReadingBody = true;
                bodyBuilder = new StringBuilder();
                // 본문 시작 부분이 같은 줄에 있을 경우
                String bodyStart = MimeDecoder.getInstance().extractBodyFromFetch(line);
                if (!bodyStart.isEmpty()) {
                    bodyBuilder.append(bodyStart);
                }
            } else if (isReadingBody) {
                if (line.equals(")")) {
                    body = MimeDecoder.getInstance().parseEmailBody(bodyBuilder.toString());
                    isReadingBody = false;

                    // 메일 데이터 저장
                    mails.add(new String[]{from, subject, date, body});
                    from = subject = date = body = mailId = "";
                    isReadingMail = false;
                } else {
                    bodyBuilder.append(line).append("\n");
                }
            }

            if (line.contains("OK FETCH completed")) { // 모든 메일 데이터 수신 완료
                break;
            }
        }
        EmailDataRepository.getInstance().setMailData(mails);
    }

    // 명령어를 서버로 보내는 메서드
    private void sendCommand(String command) throws Exception {
        System.out.println("명령어: " + command);  // 전송 명령어 디버깅 출력
        writer.println(command);
    }

    // 서버로부터 응답을 읽어오는 메서드
    private String readResponse() throws Exception {
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            System.out.println("서버 응답: " + line); // 서버의 모든 응답을 디버깅 출력
            response.append(line).append("\n");

            // 응답이 완료되었는지 확인
            if (line.contains("OK") || line.contains("NO") || line.contains("BAD")) {
                break;
            }
        }
        return response.toString();
    }

    public void disconnect() throws Exception {
        // LOGOUT 명령어를 보내 로그아웃 요청
        sendCommand("a004 LOGOUT");

        // 로그아웃 응답 확인
        System.out.println(readResponse());

        // 연결 종료를 위해 스트림과 소켓을 닫음
        reader.close();
        writer.close();
        socket.close();
    }
}
