package util;

public enum MailPlatform {
    NAVER("smtp.naver.com"),
    GOOGLE("smtp.google.com");

    private final String smtpServer;

    MailPlatform(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    public String getSmtpServer() {
        return smtpServer;
    }
}