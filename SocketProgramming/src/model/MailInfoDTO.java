package model;

public record MailInfoDTO(String sender, String receiver, String subject, String date, String content) {
}
