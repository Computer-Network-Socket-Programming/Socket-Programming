package model;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;

public record MailDTO(String recipient, String subject, String message, ArrayList<File> attachedFiles, LocalDateTime dateTime) {
}