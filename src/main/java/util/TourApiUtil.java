package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import config.AppConfigListener;

// =========================================================
// 주제: 파일 역할
// 설명: TourApiUtil에서 공통으로 사용하는 변환·API·DB 보조 기능을 제공하는 유틸리티
// =========================================================


public class TourApiUtil {

    // 발급받은 TourAPI 인증키 config.properties에서 가져오기
	private static String apiKey = AppConfigListener.getProperty("tour.api.key");
            
	

	
    // TourAPI 위치기반 조회 API
    private static final String BASE_URL =
            "https://apis.data.go.kr/B551011/KorService2/locationBasedList2";


    // =========================================================
    // 현재 위치 주변 음식점 조회
    // 공공 API JSON → item JsonElement 반환
    // =========================================================
    // =========================================================
    // 주제: getNearbyFoods 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    public static JsonElement getNearbyFoods(double latitude, double longitude) {
    
        HttpURLConnection conn = null;

        try {

            String urlString =
                    BASE_URL
                    + "?serviceKey=" + URLEncoder.encode(apiKey,"UTF-8")
                    + "&MobileOS=ETC"
                    + "&MobileApp=TravelProject"
                    + "&mapX=" + longitude
                    + "&mapY=" + latitude
                    + "&radius=5000"
                    + "&contentTypeId=39"
                    + "&arrange=E"
                    + "&numOfRows=20"
                    + "&pageNo=1"
                    + "&_type=json";

 //           System.out.println("===== TourAPI 요청 =====");
//            System.out.println(urlString);


            // -------------------------------------------------
            // API 연결
            // -------------------------------------------------
            URL url = new URL(urlString);

            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);


            int responseCode = conn.getResponseCode();

            System.out.println("TourAPI 응답 코드 : " + responseCode);


            // -------------------------------------------------
            // 응답 받기
            // -------------------------------------------------
            InputStream inputStream;

            if (responseCode >= 200 && responseCode < 300) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
            }

            if (inputStream == null) {
                System.out.println("TourAPI 응답이 비어 있습니다.");
                return null;
            }


            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    inputStream,
                                    StandardCharsets.UTF_8
                            )
                    );

            StringBuilder response = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();


//            System.out.println("===== TourAPI 응답 일부 =====");
//           System.out.println(response.substring(0, Math.min(response.length(), 300)));


            // -------------------------------------------------
            // JSON 파싱
            // -------------------------------------------------
            JsonObject root =
                    JsonParser.parseString(response.toString())
                            .getAsJsonObject();

            JsonObject responseObject =
                    root.getAsJsonObject("response");

            if (responseObject == null) {
                System.out.println("response 객체가 없습니다.");
                return null;
            }

            JsonObject bodyObject =
                    responseObject.getAsJsonObject("body");

            if (bodyObject == null) {
                System.out.println("body 객체가 없습니다.");
                return null;
            }

            JsonObject itemsObject =
                    bodyObject.getAsJsonObject("items");

            if (itemsObject == null) {
                System.out.println("items 객체가 없습니다.");
                return null;
            }

            JsonElement itemElement =
                    itemsObject.get("item");

            if (itemElement == null || itemElement.isJsonNull()) {
                System.out.println("item 데이터가 없습니다.");
                return null;
            }

            return itemElement;

        } catch (Exception e) {

            System.out.println("===== TourAPI 오류 =====");
            e.printStackTrace();

        } finally {

            if (conn != null) {
                conn.disconnect();
            }
        }

        return null;
    }
 // =========================================================
 // 주제: 현재 위치 주변 관광지 조회
 // 설명:
 // - 음식점은 contentTypeId=39
 // - 관광지는 contentTypeId=12
 // - 현재 위도/경도 기준 반경 5km 안의 관광지를 가져옴
 // - 결과는 TourService에서 TourDTO로 변환해서 사용
 // =========================================================
 public static JsonElement getNearbyTours(double latitude, double longitude) {

     HttpURLConnection conn = null;

     try {

         // ---------------------------------------------------------
         // 주제: TourAPI 요청 주소 만들기
         // 설명:
         // - mapX = 경도
         // - mapY = 위도
         // - contentTypeId=12 는 관광지
         // - radius=5000 은 반경 5km
         // ---------------------------------------------------------

         String urlString =
                 BASE_URL
                 + "?serviceKey=" + URLEncoder.encode(apiKey, "UTF-8")
                 + "&MobileOS=ETC"
                 + "&MobileApp=TravelProject"
                 + "&mapX=" + longitude
                 + "&mapY=" + latitude
                 + "&radius=5000"
                 + "&contentTypeId=12"
                 + "&arrange=E"
                 + "&numOfRows=10"
                 + "&pageNo=1"
                 + "&_type=json";


         // ---------------------------------------------------------
         // 주제: API 연결
         // 설명: 위에서 만든 urlString으로 TourAPI 서버에 연결
         // ---------------------------------------------------------

         URL url =
                 new URL(urlString);

         conn =
                 (HttpURLConnection) url.openConnection();

         conn.setRequestMethod("GET");
         conn.setConnectTimeout(5000);
         conn.setReadTimeout(10000);


         // ---------------------------------------------------------
         // 주제: 응답 코드 확인
         // 설명:
         // - 200번대면 정상 응답
         // - 그 외에는 오류 응답
         // ---------------------------------------------------------

         int responseCode =
                 conn.getResponseCode();

         System.out.println("TourAPI 관광지 응답 코드 : " + responseCode);


         // ---------------------------------------------------------
         // 주제: 응답 받기
         // 설명:
         // - 정상 응답이면 getInputStream()
         // - 오류 응답이면 getErrorStream()
         // ---------------------------------------------------------

         InputStream inputStream;

         if (responseCode >= 200 && responseCode < 300) {
             inputStream =
                     conn.getInputStream();
         } else {
             inputStream =
                     conn.getErrorStream();
         }

         if (inputStream == null) {
             System.out.println("TourAPI 관광지 응답이 비어 있습니다.");
             return null;
         }


         // ---------------------------------------------------------
         // 주제: 응답 문자열로 변환
         // 설명: API에서 받은 데이터를 StringBuilder에 한 줄씩 담음
         // ---------------------------------------------------------

         BufferedReader reader =
                 new BufferedReader(
                         new InputStreamReader(
                                 inputStream,
                                 StandardCharsets.UTF_8
                         )
                 );

         StringBuilder response =
                 new StringBuilder();

         String line;

         while ((line = reader.readLine()) != null) {
             response.append(line);
         }

         reader.close();


         // ---------------------------------------------------------
         // 주제: JSON 파싱
         // 설명:
         // TourAPI 응답 구조는 보통
         // response → body → items → item
         // 순서로 들어 있음
         // ---------------------------------------------------------

         JsonObject root =
                 JsonParser.parseString(response.toString())
                         .getAsJsonObject();

         JsonObject responseObject =
                 root.getAsJsonObject("response");

         if (responseObject == null) {
             System.out.println("관광지 response 객체가 없습니다.");
             return null;
         }

         JsonObject bodyObject =
                 responseObject.getAsJsonObject("body");

         if (bodyObject == null) {
             System.out.println("관광지 body 객체가 없습니다.");
             return null;
         }


         // ---------------------------------------------------------
         // 주제: items 확인
         // 설명:
         // 관광지가 없으면 items가 빈 문자열로 올 수도 있어서
         // getAsJsonObject() 하기 전에 안전하게 확인
         // ---------------------------------------------------------

         JsonElement itemsElement =
                 bodyObject.get("items");

         if (itemsElement == null
                 || itemsElement.isJsonNull()
                 || itemsElement.isJsonPrimitive()) {

             System.out.println("관광지 items 데이터가 없습니다.");
             return null;
         }

         JsonObject itemsObject =
                 itemsElement.getAsJsonObject();

         JsonElement itemElement =
                 itemsObject.get("item");


         // ---------------------------------------------------------
         // 주제: item 확인
         // 설명:
         // 실제 관광지 목록 데이터
         // 여러 개면 JsonArray, 1개면 JsonObject로 올 수 있음
         // ---------------------------------------------------------

         if (itemElement == null || itemElement.isJsonNull()) {
             System.out.println("관광지 item 데이터가 없습니다.");
             return null;
         }


         // ---------------------------------------------------------
         // 주제: 관광지 item 반환
         // 설명: TourService에서 이 값을 받아서 TourDTO로 바꿈
         // ---------------------------------------------------------

         return itemElement;

     } catch (Exception e) {

         // ---------------------------------------------------------
         // 주제: 오류 처리
         // 설명: 관광지 API 오류가 나도 페이지 전체가 죽지 않게 처리
         // ---------------------------------------------------------

         System.out.println("===== TourAPI 관광지 오류 =====");
         e.printStackTrace();

     } finally {

         // ---------------------------------------------------------
         // 주제: API 연결 종료
         // 설명: 연결을 닫아서 자원 낭비 방지
         // ---------------------------------------------------------

         if (conn != null) {
             conn.disconnect();
         }
     }

     return null;
 }
    
    
 // =========================================================
 // 주제: 음식점 상세 소개 정보 조회
 // 설명: contentId로 detailIntro2 API를 호출해서
//       영업시간, 대표메뉴, 전화번호 등 상세정보를 가져옴
 // =========================================================
 public static JsonObject getFoodIntroDetail(String contentId) {

     HttpURLConnection conn = null;

     try {

         String urlString =
                 "https://apis.data.go.kr/B551011/KorService2/detailIntro2"
                 + "?serviceKey=" + URLEncoder.encode(apiKey, "UTF-8")
                 + "&MobileOS=ETC"
                 + "&MobileApp=TravelProject"
                 + "&contentId=" + contentId
                 + "&contentTypeId=39"
                 + "&_type=json";

//         System.out.println("===== TourAPI 음식점 상세 요청 =====");
//         System.out.println(urlString);


         // =====================================================
         // 주제: API 연결
         // =====================================================

         URL url = new URL(urlString);

         conn = (HttpURLConnection) url.openConnection();

         conn.setRequestMethod("GET");
         conn.setConnectTimeout(5000);
         conn.setReadTimeout(10000);


         // =====================================================
         // 주제: 응답 읽기
         // =====================================================

         int responseCode = conn.getResponseCode();

         InputStream inputStream;

         if (responseCode >= 200 && responseCode < 300) {
             inputStream = conn.getInputStream();
         } else {
             inputStream = conn.getErrorStream();
         }

         if (inputStream == null) {
             return null;
         }


         BufferedReader reader =
                 new BufferedReader(
                         new InputStreamReader(
                                 inputStream,
                                 StandardCharsets.UTF_8
                         )
                 );

         StringBuilder response = new StringBuilder();

         String line;

         while ((line = reader.readLine()) != null) {
             response.append(line);
         }

         reader.close();


         // =====================================================
         // 주제: JSON 파싱
         // =====================================================

         JsonObject root =
                 JsonParser.parseString(response.toString())
                         .getAsJsonObject();

         JsonObject responseObject =
                 root.getAsJsonObject("response");

         if (responseObject == null) {
             return null;
         }

         JsonObject bodyObject =
                 responseObject.getAsJsonObject("body");

         if (bodyObject == null) {
             return null;
         }

         JsonObject itemsObject =
                 bodyObject.getAsJsonObject("items");

         if (itemsObject == null) {
             return null;
         }

         JsonElement itemElement =
                 itemsObject.get("item");

         if (itemElement == null || itemElement.isJsonNull()) {
             return null;
         }


         // =====================================================
         // 주제: 상세 item 반환
         // 설명: 보통 객체 1개가 오지만, 혹시 배열이면 첫 번째만 사용
         // =====================================================

         if (itemElement.isJsonArray()) {
             JsonArray array = itemElement.getAsJsonArray();

             if (array.size() == 0) {
                 return null;
             }

             return array.get(0).getAsJsonObject();
         }

         return itemElement.getAsJsonObject();

     } catch (Exception e) {

         System.out.println("===== TourAPI 음식점 상세 오류 =====");
         e.printStackTrace();

     } finally {

         if (conn != null) {
             conn.disconnect();
         }
     }

     return null;
 }
 

//=========================================================
//주제: 관광지 공통 상세정보 조회
//설명: contentId와 contentTypeId로 detailCommon2를 호출해서 overview를 가져온다
//=========================================================
	public static JsonObject getTourCommonDetail(String contentId,String contentTypeId){
	  HttpURLConnection conn=null;
	  try{
	      String typeParam=(contentTypeId!=null&&!contentTypeId.trim().isEmpty())?"&contentTypeId="+URLEncoder.encode(contentTypeId,"UTF-8"):"&contentTypeId=12";
	      String urlString="https://apis.data.go.kr/B551011/KorService2/detailCommon2?serviceKey="+URLEncoder.encode(apiKey,"UTF-8")+"&MobileOS=ETC&MobileApp=TravelProject&contentId="+URLEncoder.encode(contentId,"UTF-8")+typeParam+"&defaultYN=Y&firstImageYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&_type=json";
	      URL url=new URL(urlString);
	      conn=(HttpURLConnection)url.openConnection();
	      conn.setRequestMethod("GET");
	      conn.setConnectTimeout(5000);
	      conn.setReadTimeout(10000);
	      InputStream is=(conn.getResponseCode()>=200&&conn.getResponseCode()<300)?conn.getInputStream():conn.getErrorStream();
	      if(is==null)return null;
	      BufferedReader br=new BufferedReader(new InputStreamReader(is,StandardCharsets.UTF_8));
	      StringBuilder sb=new StringBuilder();
	      String line;
	      while((line=br.readLine())!=null)sb.append(line);
	      br.close();
	
	      System.out.println("[TourApiUtil] detailCommon2 응답 = "+sb);
	
	      JsonObject root=JsonParser.parseString(sb.toString()).getAsJsonObject();
	      JsonObject body=root.getAsJsonObject("response").getAsJsonObject("body");
	      JsonElement items=body.get("items");
	      if(items==null||items.isJsonNull()||items.isJsonPrimitive())return null;
	
	      JsonElement item=items.getAsJsonObject().get("item");
	      if(item==null||item.isJsonNull())return null;
	      if(item.isJsonArray()&&item.getAsJsonArray().size()>0)return item.getAsJsonArray().get(0).getAsJsonObject();
	      if(item.isJsonObject())return item.getAsJsonObject();
	      return null;
	  }catch(Exception e){
	      System.out.println("[TourApiUtil] 관광지 상세정보 조회 실패");
	      e.printStackTrace();
	      return null;
	  }finally{
	      if(conn!=null)conn.disconnect();
	  }
	}
 
}
