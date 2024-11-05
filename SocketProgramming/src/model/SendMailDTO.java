package model;

import java.io.File;
import java.util.ArrayList;

public record SendMailDTO(String recipient, String subject, String message, ArrayList<File> attachedFiles) {
}
