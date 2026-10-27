package util;

import java.util.Map;

// =========================================================
// 주제: 파일 역할
// 설명: AccommodationTypeConverter에서 공통으로 사용하는 변환·API·DB 보조 기능을 제공하는 유틸리티
// =========================================================


public class AccommodationTypeConverter {
	// 변경되지 않는 매핑 관계를 상수로 선언
    private static final Map<String, String> CATEGORY_MAP = Map.of(
        "AC01", "호텔",
        "AC02", "콘도",
        "AC03", "펜션민박",
        "AC04", "모텔",
        "AC05", "캠핑",
        "AC06", "호스텔"
    );

    /**
     * 공공 API 코드값을 받아 DB 입력용 한글 카테고리로 변환
     */
    public static String convertCategory(String apiCode) {
        if (apiCode == null) return "기타";
        
        // 매핑된 값이 없으면 기본값("기타" 또는 입력받은 원본값) 반환
        return CATEGORY_MAP.getOrDefault(apiCode, "기타");
    }
}
