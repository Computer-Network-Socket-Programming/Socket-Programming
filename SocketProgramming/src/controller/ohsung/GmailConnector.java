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

public class GmailConnector {
    private static final String HOST = "imap.gmail.com";
    private static final int PORT = 993;

    private SSLSocket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public GmailConnector(String username, String password) throws Exception {
        connect(username, password);
    }

    private void connect(String username, String password) throws Exception {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = (SSLSocket) factory.createSocket(HOST, PORT);

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);

        System.out.println(readResponse());

        sendCommand("a001 LOGIN \"" + username + "\" \"" + password + "\"");
        System.out.println(readResponse());
    }

    private int getMailCount(String folderName) throws Exception {
        sendCommand("a002 STATUS \"" + folderName + "\" (MESSAGES)");
        String line;
        int mailCount = 0;

        Pattern pattern = Pattern.compile("\\d+");

        while ((line = reader.readLine()) != null) {
            System.out.println("서버 응답: " + line);
            if (line.startsWith("*") && line.contains("MESSAGES")) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    mailCount = Integer.parseInt(matcher.group());
                }
                break;
            }
            if (line.contains("OK") || line.contains("NO") || line.contains("BAD")) {
                break;
            }
        }

        return mailCount;
    }

    private List<String[]> fetchMailData(String folderName) throws Exception {
        int mailCount = getMailCount(folderName);
        String range = (mailCount <= 1000) ? ((mailCount - 10 > 0 ? mailCount - 10 : 1) + ":" + mailCount) : "980:1000";
        System.out.println(folderName + mailCount);

        sendCommand("a003 SELECT \"" + folderName + "\"");
        readResponse();  // `SELECT` 응답

        List<String[]> mails = new ArrayList<>();
        sendCommand("a004 FETCH " + range + " (BODY[HEADER.FIELDS (FROM TO SUBJECT DATE)] BODY[TEXT])");

        String line;
        String from = "", to = "", subject = "", date = "", body = "";
        boolean isReadingBody = false;
        StringBuilder bodyBuilder = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            System.out.println("서버 응답: " + line);

            // 시작 부분 - FETCH 응답 감지
            if (line.startsWith("*") && line.contains("FETCH")) {
                if (!from.isEmpty() || !to.isEmpty() || !subject.isEmpty() || !date.isEmpty() || bodyBuilder.length() > 0) {
                    body = MimeDecoder.getInstance().parseEmailBody(bodyBuilder.toString());
                    mails.add(new String[]{from, to, subject, date, body});
                    from = to = subject = date = body = "";
                    bodyBuilder.setLength(0);
                }
            }
            // 데이터 수집 구문
            else if (line.toUpperCase().startsWith("FROM:")) {
                from = MimeDecoder.getInstance().decodeMimeText(line.substring(line.indexOf("From:") + 5).trim());
            } else if (line.toUpperCase().startsWith("TO:")) {
                to = MimeDecoder.getInstance().decodeMimeText(line.substring(line.indexOf("To:") + 3).trim());
            } else if (line.toUpperCase().startsWith("SUBJECT:")) {
                subject = MimeDecoder.getInstance().decodeMimeText(line.substring(line.indexOf("Subject:") + 8).trim());
            } else if (line.toUpperCase().startsWith("DATE:")) {
                date = line.substring(line.indexOf("Date:") + 5).trim();
            } else if (line.contains("BODY[TEXT]")) {
                isReadingBody = true;
                bodyBuilder.setLength(0);
            } else if (isReadingBody) {
                if (line.equals(")")) {
                    body = MimeDecoder.getInstance().parseEmailBody(bodyBuilder.toString());
                    mails.add(new String[]{from, to, subject, date, body});
                    from = to = subject = date = body = "";
                    isReadingBody = false;
                } else {
                    bodyBuilder.append(line).append("\n");
                }
            }

            if (line.toUpperCase().contains("OK") && line.toUpperCase().contains("SUCCESS")) {
                break;
            }
        }

        return mails;
    }

    public void fetchAllMailFolders() throws Exception {
        EmailDataRepository repository = EmailDataRepository.getInstance();

        repository.setGoogleInBoxMailData(fetchMailData("INBOX"));                         // Inbox
        repository.setGoogleSentMailData(fetchMailData("[Gmail]/&vPSwuNO4ycDVaA-"));      // Sent Mail
        repository.setGoogleDraftMailData(fetchMailData("[Gmail]/&x4TC3Lz0rQDVaA-"));     // Drafts
        repository.setGoogleTrashMailData(fetchMailData("[Gmail]/&1zTJwNG1-"));        // Trash
    }

    private void sendCommand(String command) throws Exception {
        System.out.println("명령어: " + command);
        writer.println(command);
    }

    private String readResponse() throws Exception {
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            System.out.println("서버 응답: " + line);
            response.append(line).append("\n");

            if (line.contains("OK") || line.contains("NO") || line.contains("BAD")) {
                break;
            }
        }
        return response.toString();
    }

    public void disconnect() throws Exception {
        sendCommand("a005 LOGOUT");
        System.out.println(readResponse());
        reader.close();
        writer.close();
        socket.close();
    }
}
