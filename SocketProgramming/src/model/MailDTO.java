package model;

import java.time.LocalDateTime;

public record MailDTO(String recipient, String subject, String message, LocalDateTime dateTime) {
}
