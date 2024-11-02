package util;

public enum SmtpStatusCode {
    INITIALIZE(0, "Initialize"),

    // 성공적인 응답
    SERVICE_READY(220, "<domain> Service ready"),
    SERVICE_CLOSING(221, "<domain> Service closing transmission channel"),  // 서비스 종료
    AUTH_SUCCESS(235, "Authentication succeeded"),
    REQUEST_ACTION_OK(250, "Requested mail action okay, completed"),    // 요청된 메일 작업이 완료됨
    CANNOT_VERIFY_USER(252, "Cannot VRFY user, but will accept message and attempt delivery"),  // 사용자 확인 불가

    // 추가 입력이 필요한 응답
    START_MAIL_INPUT(354, "Start mail input; end with <CRLF>.<CRLF>"),

    // timeout
    SERVICE_NOT_AVAILABLE(421, "<domain> Service not available, closing transmission channel"),

    // 문법적 오류
    SYNTAX_ERROR(500, "Syntax error, command unrecognized");

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
