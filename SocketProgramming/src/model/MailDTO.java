package model;

import java.time.LocalDateTime;

public class MailDTO {

    private final String sender;
    private final String receiver;
    private final String subject;
    private final String message;
    private final LocalDateTime dateTime;

    public MailDTO(String sender, String receiver, String subject, String message, LocalDateTime dateTime) {
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.message = message;
        this.dateTime = dateTime;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
