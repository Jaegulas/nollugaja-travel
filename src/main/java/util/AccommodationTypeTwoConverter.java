package util;

import java.util.Map;
import static java.util.Map.entry;

// =========================================================
// 주제: 파일 역할
// 설명: AccommodationTypeTwoConverter에서 공통으로 사용하는 변환·API·DB 보조 기능을 제공하는 유틸리티
// =========================================================



public class AccommodationTypeTwoConverter {
	// 변경되지 않는 매핑 관계를 상수로 선언
    private static final Map<String, String> CATEGORY_MAP = Map.ofEntries( //10개초과시사용
		entry("AC010100", "호텔"),
        entry("AC020100", "콘도"),
        entry("AC020200", "레지던스"),
        entry("AC030100", "팬션"),
        entry("AC030200", "한옥스테이"),
        entry("AC030300", "농어촌민박"),
        entry("AC030400", "홈스테이"),
        entry("AC040100", "모텔"),
        entry("AC050100", "일반야영장"),
        entry("AC050200", "오토캠핑장"),
        entry("AC050300", "카라반"),
        entry("AC050400", "글램핑장"),
        entry("AC060100", "유스호스텔"),
        entry("AC060200", "게스트하우스")
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
