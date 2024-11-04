package model;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record SendMailDTO(List<String> recipient, String subject, String message, ArrayList<File> attachedFiles, LocalDateTime dateTime) {
}