

// =========================================================
// 주제: 파일 역할
// 설명: LoginVo 관련 값을 보관하고 전달하는 VO
// =========================================================

package vo;

public class LoginVo {

    private String id;
    private String pass;
    private String name;
    private String role; // 관리자 권한

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPasswd() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // 관리자 권한 조회
    public String getRole() {
        return role;
    }

    // 관리자 권한 저장
    public void setRole(String role) {
        this.role = role;
    }
}
