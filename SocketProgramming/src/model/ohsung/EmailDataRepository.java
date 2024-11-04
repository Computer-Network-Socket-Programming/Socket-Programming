package model.ohsung;

import java.util.ArrayList;
import java.util.List;

public class EmailDataRepository {
    // 싱글톤 인스턴스
    private static final EmailDataRepository INSTANCE = new EmailDataRepository();

    // 메일 데이터를 저장할 리스트
    private List<String[]> mailData;

    // private 생성자
    private EmailDataRepository() {
        mailData = new ArrayList<>();
    }

    // 싱글톤 인스턴스를 반환하는 메서드
    public static EmailDataRepository getInstance() {
        return INSTANCE;
    }

    // 메일 데이터를 추가하는 메서드
    public void setMailData(List<String[]> mailData) {
        this.mailData = mailData;
    }

    // 메일 데이터를 가져오는 메서드
    public List<String[]> getMailData() {
        return new ArrayList<>(mailData);  // 외부에서 리스트를 수정하지 못하도록 복사본 반환
    }
}