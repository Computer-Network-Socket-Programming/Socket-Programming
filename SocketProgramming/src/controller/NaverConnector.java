package controller;

import model.EmailDataRepository;

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
    private static final String HOST = "imap.naver.com"; //네이버 IMAP 서버 주소
    private static final int PORT = 993; //IMAP 서버 포트 번호

    private SSLSocket socket; //SSL 소켓, 서버와의 SSL 연결 담당
    private BufferedReader reader; //서버 응답을 읽어오는 스트림
    private PrintWriter writer; // 서버로 명령어를 보내는 스트림

    //생성자 : 사용자 아이디와 비밀번호 받아와서 네이버 IMAP 서버에 연결
    public NaverConnector(String username, String password) throws Exception{
        connect(username, password); //SSL 연결 및 로그인 수행
    }

    //네이버 IMAP 서버에 SSL로 연결하고 로그인하는 메서드
    private void connect(String username, String password) throws Exception{

        //SSL 소켓을 생성하여 기본 SSL 소켓 팩토리 객체를 얻음(소켓 생성 자체가 캡슐화 되어 있기 때문에 소켓 프레임 워크 규칙에 의해 SSL 소켓 인스턴스를 얻으려면 소켓 팩토리 생성 필수)
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        //ssl 소켓 생성 및 네이버 IMAP 서버 연결
        socket = (SSLSocket) factory.createSocket(HOST, PORT);

        //서버 응답 읽기, 명령어 보내기 위한 스트림 설정
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); //socket.getInputStream() : 소켓에서 데이터 들어오는 스트림을 가져옴 -> InputStreamReader: 바이트 스트림을 문자 스트림으로 변환
        writer = new PrintWriter(socket.getOutputStream(), true);//socket.getOutputStream: 서버로 데이터를 보내기 위해 사용되는 출력 스트림, autoFlush: 출력 버퍼 자동으로 지우기

        //서버 응답 확인
        System.out.println(readResponse());

        //로그인 명령어 전송: "a001"은 명령 태그, 이후 명령어 결과를 구분하는 식별자 역할
        sendCommand("a001 LOGIN " + username + " " + password);
        System.out.println(readResponse());

        //메일 폴더함 명령어 확인 코드
        //sendCommand("a002 LIST \"\" \"*\"");
        //System.out.println(readResponse());
    }

    //특정 폴더의 메일 개수 가져오는 함수
    private int getMailCount(String folderName) throws Exception {

        //STATUS 명령어를 통해 폴더의 메일 개수 요청
        sendCommand("a002 STATUS \"" + folderName + "\" (MESSAGES)");
        String line;
        int mailCount = 0;

        //숫자 패턴 정규 표현식 생성
        Pattern pattern = Pattern.compile("\\d+");

        while ((line = reader.readLine()) != null) {
            System.out.println("서버 응답: " + line);
            if (line.startsWith("*") && line.contains("MESSAGES")) { // 서버 응답 : * STATUS "INBOX" (MESSAGES 123) \n a002 OK STATUS completed -> 002가 반환되는 경우가 있어 조건 중요
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    mailCount = Integer.parseInt(matcher.group());  // 첫 번째 매칭된 숫자 추출 -> 가끔 (MESSAGES 123) UNSEEN 10 이런식으로 반환될 때가 있어서
                }
                break;
            }
            if (line.contains("OK") || line.contains("NO") || line.contains("BAD")) {
                break;
            }
        }

        return mailCount;
    }

    // 메일 목록을 가져오는 메서드
    private List<String[]> fetchMailData(String folderName) throws Exception {
        int mailCount = getMailCount(folderName);
        String range;

        // range 설정 조건
        if (mailCount <= 1000) {
            range = (mailCount - 10 > 0 ? mailCount - 10 : 1) + ":" + mailCount;
        } else {
            range = "980:1000";
        }
        sendCommand("a003 SELECT \"" + folderName + "\""); //작업할 메일함 설정(보낸 메일함, 받은 메일함 등)
        System.out.println(readResponse());

        List<String[]> mails = new ArrayList<>();
        sendCommand("a004 FETCH " + range + " (BODY[HEADER.FIELDS (FROM TO SUBJECT DATE)] BODY[TEXT])"); // HEADER와 본문에서 정보 받아오기

        String line;
        String from = "", to="", subject = "", date = "", body = "", mailId = "";
        boolean isReadingMail = false;
        boolean isReadingBody = false;
        StringBuilder bodyBuilder = new StringBuilder();//본문 데이터 누적 저장

        while ((line = reader.readLine()) != null) {
            System.out.println("서버 응답: " + line);  // 서버 응답을 디버깅 목적으로 모두 출력

            if (line.startsWith("*") && line.contains("FETCH")) {
                // FETCH 응답에서 메일 ID 추출 -> 원래는 사용하려 했는데 코드 상 딱히 필요 없어서 안씀 ㅠ 메일 순서 받아오는 건데 그냥 코드 상으로 자체 Index 설정해서 씀
                String[] parts = line.split(" ");
                if (parts.length > 1) {
                    mailId = parts[1];
                    isReadingMail = true; // 메일 항목을 읽기 시작
                }
            } else if (isReadingMail && line.toUpperCase().startsWith("FROM:")) {
                from = line.substring(line.indexOf("From:") + 5).trim();
                from = MimeDecoder.getInstance().decodeMimeText(from);
                System.out.println("보낸 사람 파싱: " + from);

            } else if (isReadingMail && line.toUpperCase().startsWith("TO:")) {
                to = line.substring(line.indexOf("To:") + 5).trim(); // 얘는 3으로 했어야 했는데 왜 5로 했죠..?
                to = MimeDecoder.getInstance().decodeMimeText(to); // 인코딩해서 날아오는 데이터 디코딩 해주는 클래스의 함수 호출
                System.out.println("보낸 사람 파싱: " + to);
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
                bodyBuilder = new StringBuilder();//본문 데이터 초기화 -> .setLength(0)했으면 더 좋았을듯
                // 본문 시작 부분이 같은 줄에 있을 경우
                String bodyStart = MimeDecoder.getInstance().extractBodyFromFetch(line);
                if (!bodyStart.isEmpty()) {
                    bodyBuilder.append(bodyStart);
                }
            } else if (isReadingBody) {
                if (line.equals(")")) {
                    body = MimeDecoder.getInstance().parseEmailBody(bodyBuilder.toString()); //최종 본문 완성
                    isReadingBody = false;

                    // 메일 데이터 저장

                    mails.add(new String[]{from, to, subject, date, body});
                    from = to = subject = date = body = mailId = "";
                    isReadingMail = false;
                } else {
                    bodyBuilder.append(line).append("\n");
                }
            }

            if (line.toUpperCase().contains("OK") && line.toUpperCase().contains("FETCH")) {
                break;
            }
        }
        return mails;
    }

    //받은 메일함, 보낸 메일함, 임시 보관함, 휴지통 데이터를 각각 가져와 저장
    public void fetchAllMailFolders() throws Exception{
        EmailDataRepository repository = EmailDataRepository.getInstance();

        //받은 메일함
        repository.setNaverInBoxMailData(fetchMailData("INBOX"));

        //보낸 메일함
        repository.setNaverSentMailData(fetchMailData("Sent Messages"));

        //임시 보관함
        repository.setNaverDraftMailData(fetchMailData("Drafts"));

        //휴지통
        repository.setNaverTrashMailData(fetchMailData("Deleted Messages"));
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

        sendCommand("a005 LOGOUT");

        // 로그아웃 응답 확인
        System.out.println(readResponse());

        // 연결 종료를 위해 스트림과 소켓을 닫음
        reader.close();
        writer.close();
        socket.close();
    }
}
