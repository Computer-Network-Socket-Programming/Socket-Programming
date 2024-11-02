package util;

public enum SmtpStatusCode {
    INITIALIZE(0, "Initialize"),

    // 성공적인 응답
    SERVICE_READY(220, "<domain> Service ready"),
    SERVICE_CLOSING(221, "<domain> Service closing transmission channel"),
    AUTH_SUCCESS(235, "Authentication succeeded"),
    REQUEST_ACTION_OK(250, "Requested mail action okay, completed"),
    CANNOT_VERIFY_USER(252, "Cannot VRFY user, but will accept message and attempt delivery"),

    // 추가 입력이 필요한 응답
    START_MAIL_INPUT(354, "Start mail input; end with <CRLF>.<CRLF>"),

    // 영구적인 실패 (요청이 거부됨)
    COMMAND_UNRECOGNIZED(500, "Syntax error, command unrecognized"),    // 구문 오류
    PARAMETER_NOT_IMPLEMENTED(504, "Command parameter not implemented"),    // 구문 오류
    PARAMETER_NOT_IMPLEMENTED_FOR_MAILBOX(550, "Requested action not taken: mailbox unavailable"),  // 사용자의 메일함이 존재하지 않음
    EXCEEDED_STORAGE_ALLOCATION(552, "Requested mail action aborted: exceeded storage allocation"),   // 메일함의 용량 초과
    MAILBOX_NAME_NOT_ALLOWED(553, "Requested action not taken: mailbox name not allowed"),  // 사용자의 메일함이 존재하지 않음
    TRANSACTION_FAILED(554, "Transaction failed");  // transaction 실패

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
