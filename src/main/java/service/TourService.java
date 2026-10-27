package service;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dto.TourDto;
import util.TourApiUtil;

// =========================================================
// 주제: 파일 역할
// 설명: TourService의 핵심 비즈니스 로직과 데이터 가공을 담당하는 서비스
// =========================================================


public class TourService {

    // =========================================================
    // 주제: 현재 위치 기준 주변 관광지 목록 가져오기
    // 설명:
    // 1. TourApiUtil에서 API 원본 데이터를 가져옴
    // 2. Json 데이터를 TourDTO로 변환
    // 3. JSP에서 사용할 List<TourDTO>로 반환
    // =========================================================

    public List<TourDto> getNearbyTours(double latitude, double longitude) {

        List<TourDto> tourList =
                new ArrayList<>();

        JsonElement itemElement =
                TourApiUtil.getNearbyTours(
                        latitude,
                        longitude
                );

        if (itemElement == null || itemElement.isJsonNull()) {
            return tourList;
        }


        // ---------------------------------------------------------
        // 관광지가 여러 개일 때
        // 설명: item이 배열 형태로 오는 경우
        // ---------------------------------------------------------

        if (itemElement.isJsonArray()) {

            for (JsonElement element : itemElement.getAsJsonArray()) {

                if (element != null && element.isJsonObject()) {

                    TourDto tour =
                            convertToTourDTO(
                                    element.getAsJsonObject(),
                                    latitude,
                                    longitude
                            );

                    tourList.add(tour);
                }
            }

        // ---------------------------------------------------------
        // 관광지가 1개일 때
        // 설명: item이 배열이 아니라 객체 하나로 오는 경우
        // ---------------------------------------------------------

        } else if (itemElement.isJsonObject()) {

            TourDto tour =
                    convertToTourDTO(
                            itemElement.getAsJsonObject(),
                            latitude,
                            longitude
                    );

            tourList.add(tour);
        }

        return tourList;
    }


    // =========================================================
    // 주제: JSON 관광지 1개를 TourDTO로 변환
    // 설명: API에서 온 title, addr1, firstimage, mapx, mapy 등을 DTO에 담음
    // =========================================================

    private TourDto convertToTourDTO(
            JsonObject item,
            double userLatitude,
            double userLongitude
    ) { 
    	TourDto tour = new TourDto();

    	// =========================================================
    	// 주제: 관광지 타입 ID 저장
    	// 설명: detailCommon2 상세 설명 조회에 사용
    	// =========================================================
    	if(item.has("contenttypeid")&&!item.get("contenttypeid").isJsonNull()){
    	    tour.setContent_type_id(item.get("contenttypeid").getAsString());
    	}else{
    	    tour.setContent_type_id("12");
    	}
    	
        // ---------------------------------------------------------
        // 기본 문자열 값 꺼내기
        // ---------------------------------------------------------

        String title =
                getString(item, "title");

        String cat3 =
                getString(item, "cat3");

        String address =
                getString(item, "addr1");


        // ---------------------------------------------------------
        // 좌표 꺼내기
        // 설명:
        // - mapy = 위도
        // - mapx = 경도
        // ---------------------------------------------------------

        double tourLatitude =
                getDouble(item, "mapy");

        double tourLongitude =
                getDouble(item, "mapx");


        // ---------------------------------------------------------
        // 이미지 꺼내기
        // 설명:
        // firstimage가 없으면 firstimage2를 사용
        // 둘 다 없으면 JSP에서 no-image 처리
        // ---------------------------------------------------------

        String image =
                getString(item, "firstimage");

        if (image == null || image.trim().isEmpty()) {
            image =
                    getString(item, "firstimage2");
        }


        // ---------------------------------------------------------
        // DTO에 값 담기
        // ---------------------------------------------------------

        tour.setContent_id(
                getString(item, "contentid")
        );

        tour.setTitle(
                title
        );

        tour.setCategory(
                convertTourCategory(cat3, title)
        );

        tour.setAddress(
                address
        );

        tour.setImage(
                image
        );

        tour.setLatitude(
                tourLatitude
        );

        tour.setLongitude(
                tourLongitude
        );


        // ---------------------------------------------------------
        // 현재 위치와 관광지 사이 거리 계산
        // ---------------------------------------------------------

        tour.setDistance(
                calculateDistance(
                        userLatitude,
                        userLongitude,
                        tourLatitude,
                        tourLongitude
                )
        );

        return tour;
    }


    // =========================================================
    // 주제: 관광지 카테고리 이름 변환
    // 설명: API 코드가 어려우니까 화면에 보이기 쉬운 이름으로 바꿈
    // =========================================================

    private String convertTourCategory(String cat3, String title) {

        String name =
                title != null ? title : "";

        if (name.contains("공원")) {
            return "관광지 · 공원";
        }

        if (name.contains("해수욕장")
                || name.contains("바다")
                || name.contains("해변")) {
            return "관광지 · 자연";
        }

        if (name.contains("시장")) {
            return "관광지 · 시장";
        }

        if (name.contains("박물관")
                || name.contains("미술관")) {
            return "관광지 · 전시";
        }

        if (name.contains("성")
                || name.contains("궁")
                || name.contains("사찰")
                || name.contains("문화")) {
            return "관광지 · 역사문화";
        }

        return "관광지";
    }


    // =========================================================
    // 주제: JSON 문자열 안전하게 꺼내기
    // 설명: 값이 없거나 null이면 빈 문자열 반환
    // =========================================================

    private String getString(JsonObject obj, String key) {

        if (obj == null
                || !obj.has(key)
                || obj.get(key).isJsonNull()) {
            return "";
        }

        return obj.get(key).getAsString();
    }


    // =========================================================
    // 주제: JSON 숫자 안전하게 꺼내기
    // 설명: 위도/경도 값이 비어 있으면 0으로 처리
    // =========================================================

    private double getDouble(JsonObject obj, String key) {

        try {

            if (obj == null
                    || !obj.has(key)
                    || obj.get(key).isJsonNull()) {
                return 0;
            }

            String value =
                    obj.get(key).getAsString();

            if (value == null || value.trim().isEmpty()) {
                return 0;
            }

            return Double.parseDouble(value);

        } catch (Exception e) {
            return 0;
        }
    }


    // =========================================================
    // 주제: 두 좌표 사이 거리 계산
    // 설명: 현재 위치와 관광지 위치 사이의 거리를 km 단위로 계산
    // =========================================================

    private double calculateDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2
    ) {

        if (lat2 == 0 || lon2 == 0) {
            return 0;
        }

        final int earthRadius =
                6371;

        double dLat =
                Math.toRadians(lat2 - lat1);

        double dLon =
                Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );

        return earthRadius * c;
    }
}
