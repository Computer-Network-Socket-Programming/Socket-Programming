package model;

public class MailInfo {
    private String sender;
    private String receiver;
    private String date;
    private String subject;
    private String content;

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
