package model.ohsung;

public class EmailInfoDTO {
    private String sender;
    private String subject;
    private String date;

    public EmailInfoDTO(String sender, String subject, String date){
        this.sender = sender;
        this.subject = subject;
        this.date = date;
    }

    public String getSender(){
        return sender;
    }
    public String getSubject(){
        return subject;
    }
    public String getDate(){
        return date;
    }
}
