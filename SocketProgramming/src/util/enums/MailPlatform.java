package util.enums;

/*
    * 이메일 주소의 도메인에 따라 SMTP 서버를 결정
    * @param smtpServer SMTP 서버 주소
 */
public enum MailPlatform {
    NAVER("smtp.naver.com"),
    GMAIL("smtp.gmail.com");

    private final String smtpServer;

    MailPlatform(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    public String getSmtpServer() {
        return smtpServer;
    }
}
