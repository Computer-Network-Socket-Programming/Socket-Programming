package model.ohsung;

import java.util.List;

public class EmailInfoDTO {
    private String from;
    private String to;
    private String subject;
    private String date;
    private String body;
    private List<byte[]> attachments;

    public EmailInfoDTO(String from, String to , String subject, String date, String body, List<byte[]> attachments){
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.date = date;
        this.body = body;
        this.attachments = attachments;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<byte[]> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<byte[]> attachments) {
        this.attachments = attachments;
    }

    public String getSubject(){
        return subject;
    }
    public String getDate(){
        return date;
    }
}
