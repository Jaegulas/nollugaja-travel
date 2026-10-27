

// =========================================================
// 주제: 파일 역할
// 설명: RegisterVo 관련 값을 보관하고 전달하는 VO
// =========================================================

package vo;

/**
 * 놀러가자 - 회원가입 정보 VO
 *
 * 회원가입 화면에서 입력받은 정보를 Controller → Service → DAO 사이에서 전달하는 객체
 *
 * 실제 DB 테이블 : member
 *
 * DB 컬럼 id, passwd, name, reg_date, age, gender, email, address, tel, hp
 *
 * JSP의 name="pass"는 Java의 pass 변수로 받고, DAO에서 DB의 passwd 컬럼에 저장한다.
 */
public class RegisterVo {

	// ==========================================
	// 회원 정보 변수
	// ==========================================

	private String id; // 아이디
	private String pass; // 비밀번호
	private String name; // 이름

	private int age; // 나이

	private String gender; // 성별
	private String address; // 주소
	private String email; // 이메일
	private String tel; // 일반 전화번호
	private String hp; // 휴대폰 번호

	// ==========================================
	// 기본 생성자
	// ==========================================

	public RegisterVo() {

	}

	// ==========================================
	// 전체 정보를 받는 생성자
	// ==========================================

	public RegisterVo(String id, String pass, String name, int age, String gender, String address,
			String email, String tel, String hp) {

		this.id = id;
		this.pass = pass;
		this.name = name;
		this.age = age;
		this.gender = gender;
		this.address = address;
		this.email = email;
		this.tel = tel;
		this.hp = hp;
	}

	// ==========================================
	// Getter / Setter
	// ==========================================

	// 아이디
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	// 비밀번호
	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	// 이름
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// 나이
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	// 성별
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	// 주소
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	// 이메일
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	// 일반 전화번호
	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	// 휴대폰 번호
	public String getHp() {
		return hp;
	}

	public void setHp(String hp) {
		this.hp = hp;
	}

}
