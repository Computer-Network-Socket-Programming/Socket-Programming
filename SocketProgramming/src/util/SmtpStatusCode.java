package util;

public enum SmtpStatusCode {
    SERVICE_CLOSING(221, "<domain> Service closing transmission channel"),  // 서비스 종료
    SERVICE_NOT_AVAILABLE(421, "<domain> Service not available, closing transmission channel"), // timeout
    SYNTAX_ERROR(500, "Syntax error, command unrecognized");    // 문법적 오류

    private final int code;
    private final String description;

    SmtpStatusCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
