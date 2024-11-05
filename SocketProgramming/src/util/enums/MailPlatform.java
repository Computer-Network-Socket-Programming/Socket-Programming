package util.enums;

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
