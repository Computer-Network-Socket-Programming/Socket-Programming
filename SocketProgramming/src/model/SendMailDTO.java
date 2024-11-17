package model;

import java.io.File;
import java.util.ArrayList;

/*
    * 이메일 전송 정보를 담은 DTO
    * @param recipient 수신자 이메일 주소
    * @param subject 이메일 제목
    * @param message 이메일 내용
    * @param attachedFiles 첨부 파일 목록
 */
public record SendMailDTO(String recipient, String subject, String message, ArrayList<File> attachedFiles) {
}
