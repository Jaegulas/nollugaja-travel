package api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import config.AppConfigListener;

// =========================================================
// 주제: 파일 역할
// 설명: CommonGetApiKey 외부 API 연동에 필요한 요청/응답 처리를 담당
// =========================================================


public class CommonGetApiKey {

	
	public static String get(String sysKey, String envKey, String propKey) {

		// 1순위 : java -Dfile.repo.path=... 로 준 값
		String v = System.getProperty(sysKey);

		// 2순위 : export FILE_REPO_PATH=... 로 준 값
		if (isBlank(v)) {
			try {
				v = System.getenv(envKey);
			} catch (SecurityException e) {
				v = null;   // 보안 설정으로 환경변수를 못 읽는 경우 그냥 넘어간다
			}
		}

		// 3순위 : Properties 객체이용 properties 파일에서 들고옴
		if (isBlank(v)) {
			v = AppConfigListener.getProperty(propKey);
		}

		return (v == null) ? null : v.trim();
	}
	
	
	// =========================================================
	// 주제: isBlank 처리
	// 설명: 입력값 또는 현재 상태가 조건에 맞는지 검사
	// =========================================================
	private static boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}


}
