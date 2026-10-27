
package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dto.FoodDto;

// =========================================================
// 주제: 메인 페이지 랜덤 음식점 조회
// 설명: 기존 FoodService를 활용하여 여러 지역의 음식점 조회
// =========================================================
public class MainFoodService {

    // 음식점 목록 캐시 유지 시간: 1시간
    private static final long CACHE_TIME = 60 * 60 * 1000L;

    private static List<FoodDto> cachedFoods = new ArrayList<>();
    private static long lastLoadedTime = 0;

    private final FoodService foodService = new FoodService();

    // =====================================================
    // 지역별 기준 좌표
    // 설명: 기존 위치 기반 음식점 API를 활용
    // =====================================================
    private static final double[][] REGIONS = {
        {37.5665, 126.9780}, // 서울
        {35.1796, 129.0756}, // 부산
        {33.4996, 126.5312}, // 제주
        {37.4563, 126.7052}, // 인천
        {35.1595, 126.8526}, // 광주
        {35.8714, 128.6014}, // 대구
        {36.3504, 127.3845}, // 대전
        {37.7519, 128.8761}  // 강릉
    };

    // =====================================================
    // 메인 페이지 랜덤 음식점 4개 반환
    // =====================================================
    public List<FoodDto> getRandomFoods() {

        List<FoodDto> foods = getCachedFoods();

        List<FoodDto> shuffled = new ArrayList<>(foods);

        Collections.shuffle(shuffled);

        int count = Math.min(4, shuffled.size());

        return new ArrayList<>(shuffled.subList(0, count));
    }

    // =====================================================
    // 캐시 조회
    // =====================================================
    private static synchronized List<FoodDto> getCacheIfValid() {

        long now = System.currentTimeMillis();

        if (!cachedFoods.isEmpty()
                && now - lastLoadedTime < CACHE_TIME) {

            return new ArrayList<>(cachedFoods);
        }

        return null;
    }

    // =====================================================
    // API 조회 및 캐시 갱신
    // =====================================================
    private List<FoodDto> getCachedFoods() {

        List<FoodDto> cache = getCacheIfValid();

        if (cache != null) {
            return cache;
        }

        List<FoodDto> newFoods = loadFoods();

        synchronized (MainFoodService.class) {

            if (!newFoods.isEmpty()) {
                cachedFoods = new ArrayList<>(newFoods);
                lastLoadedTime = System.currentTimeMillis();
            }

            return new ArrayList<>(cachedFoods);
        }
    }

    // =====================================================
    // 기존 FoodService로 여러 지역 음식점 조회
    // =====================================================
    private List<FoodDto> loadFoods() {

        Map<String, FoodDto> uniqueFoods =
                new LinkedHashMap<>();

        for (double[] region : REGIONS) {

            try {

                double latitude = region[0];
                double longitude = region[1];

                List<FoodDto> foods =
                        foodService.getNearbyFoods(
                            latitude,
                            longitude
                        );

                if (foods == null) {
                    continue;
                }

                for (FoodDto food : foods) {

                    if (food == null) {
                        continue;
                    }

                    String contentId = food.getContent_id();
                    String name = food.getF_name();
                    String image = food.getImage();

                    // 필수 데이터가 없는 음식점 제외
                    if (contentId == null
                            || contentId.trim().isEmpty()) {
                        continue;
                    }

                    if (name == null
                            || name.trim().isEmpty()) {
                        continue;
                    }

                    // 실제 사진이 있는 음식점만 선택
                    if (image == null
                            || image.trim().isEmpty()) {
                        continue;
                    }

                    // contentId 기준 중복 제거
                    uniqueFoods.putIfAbsent(contentId, food);
                }

            } catch (Exception e) {

                System.err.println(
                    "[MainFood] 지역 음식점 조회 실패: "
                    + e.getMessage()
                );
            }
        }

        List<FoodDto> result =
                new ArrayList<>(uniqueFoods.values());

        System.out.println(
            "[MainFood] 음식점 후보: "
            + result.size() + "개"
        );

        return result;
    }
}
