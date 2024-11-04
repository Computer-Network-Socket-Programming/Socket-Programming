package model;

import java.io.File;
import java.util.ArrayList;

public record DeliverMailDTO(String subject, String content, ArrayList<File> attatchedFiles) {
}

