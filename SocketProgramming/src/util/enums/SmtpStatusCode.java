package util.enums;

public enum SmtpStatusCode {
    SERVICE_CLOSING(221, "서버와의 연결이 종료되었습니다"),
    SERVICE_NOT_AVAILABLE(421, "SMTP 통신 중 time out 이 발생하였습니다.\n 다음에 이용해 주세요."), // timeout
    SYNTAX_ERROR(500, "문법 오류입니다."),    // 문법적 오류
    RECIPIENT_NOT_FOUND(553, "발신자 주소가 올바르지 않습니다"),
    NOT_ACCEPTED(535, "사용자 인증에 실패하였습니다\n 아이디, 비밀번호, 보안 수준를 확인하여 주세요."),;

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
