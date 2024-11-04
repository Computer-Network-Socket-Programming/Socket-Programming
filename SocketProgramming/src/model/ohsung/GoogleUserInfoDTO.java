package model.ohsung;

public class GoogleUserInfoDTO {

    private static GoogleUserInfoDTO instance;
    private String username;
    private String password;

    // private 생성자로 외부에서 인스턴스를 생성하지 못하게 설정
    private GoogleUserInfoDTO() {}

    // getInstance() 메서드를 통해 유일한 인스턴스를 가져올 수 있음
    public static GoogleUserInfoDTO getInstance() {
        if (instance == null) {
            instance = new GoogleUserInfoDTO();
        }
        return instance;
    }

    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }
}