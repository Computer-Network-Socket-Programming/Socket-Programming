package view;

public class MailInfo {
    private final String sender;
    private final String receiver;
    private final String date;
    private final String subject;
    private final String content;

    public MailInfo(String sender, String receiver, String date, String subject, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.subject = subject;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getDate() {
        return date;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }
}
