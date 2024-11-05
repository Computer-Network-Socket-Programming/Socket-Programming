package model.ohsung;

import java.util.ArrayList;
import java.util.List;

public class EmailDataRepository {
    // 싱글톤 인스턴스
    private static final EmailDataRepository INSTANCE = new EmailDataRepository();



    private List<String[]> googleInBoxMailData;
    private List<String[]> googleSentMailData;
    private List<String[]> googleTrashMailData;
    private List<String[]> googleDraftMailData;
    // 메일 데이터를 저장할 리스트
    private List<String[]> naverInBoxMailData;
    private List<String[]> naverSentMailData;
    private List<String[]> naverTrashMailData;
    private List<String[]> naverDraftMailData;

    // private 생성자
    private EmailDataRepository() {
    }

    // 싱글톤 인스턴스를 반환하는 메서드
    public static EmailDataRepository getInstance() {
        return INSTANCE;
    }

    public List<String[]> getGoogleInBoxMailData() {
        return googleInBoxMailData;
    }

    public void setGoogleInBoxMailData(List<String[]> googleInBoxMailData) {
        this.googleInBoxMailData = googleInBoxMailData;
    }

    public List<String[]> getGoogleSentMailData() {
        return googleSentMailData;
    }

    public void setGoogleSentMailData(List<String[]> googleSentMailData) {
        this.googleSentMailData = googleSentMailData;
    }

    public List<String[]> getGoogleTrashMailData() {
        return googleTrashMailData;
    }

    public void setGoogleTrashMailData(List<String[]> googleTrashMailData) {
        this.googleTrashMailData = googleTrashMailData;
    }

    public List<String[]> getGoogleDraftMailData() {
        return googleDraftMailData;
    }

    public void setGoogleDraftMailData(List<String[]> googleDraftMailData) {
        this.googleDraftMailData = googleDraftMailData;
    }

    // 메일 데이터를 가져오는 메서드
    public List<String[]> getNaverInBoxMailData() {
        return new ArrayList<>(naverInBoxMailData);  // 외부에서 리스트를 수정하지 못하도록 복사본 반환
    }

    // 메일 데이터를 추가하는 메서드
    public void setNaverInBoxMailData(List<String[]> naverInBoxMailData) {
        this.naverInBoxMailData = naverInBoxMailData;
    }

    public List<String[]> getNaverSentMailData() {
        return naverSentMailData;
    }

    public void setNaverSentMailData(List<String[]> naverSentMailData) {
        this.naverSentMailData = naverSentMailData;
    }

    public List<String[]> getNaverTrashMailData() {
        return naverTrashMailData;
    }

    public void setNaverTrashMailData(List<String[]> naverTrashMailData) {
        this.naverTrashMailData = naverTrashMailData;
    }

    public List<String[]> getNaverDraftMailData() {
        return naverDraftMailData;
    }

    public void setNaverDraftMailData(List<String[]> naverDraftMailData) {
        this.naverDraftMailData = naverDraftMailData;
    }
}