package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import config.AppConfigListener;

// =========================================================
// 주제: 파일 역할
// 설명: NaverLocalSearchUtil에서 공통으로 사용하는 변환·API·DB 보조 기능을 제공하는 유틸리티
// =========================================================


public class NaverLocalSearchUtil{
    private static final String CLIENT_ID=AppConfigListener.getProperty("naver.api.client-id");
    private static final String CLIENT_SECRET=AppConfigListener.getProperty("naver.api.client-secret");
    private static final String BASE_URL="https://naverapihub.apigw.ntruss.com/search/v1/local";

    // =========================================================
    // 주제: 네이버 지역 검색
    // 설명: 장소명/주소로 네이버 지역검색 API를 호출하고 JSON 문자열 반환
    // =========================================================
    public static String searchPlace(String keyword){
        HttpURLConnection conn=null;
        try{
            String query=URLEncoder.encode(keyword,StandardCharsets.UTF_8);
            String urlString=BASE_URL+"?query="+query+"&display=5&start=1&sort=random&format=json";
            conn=(HttpURLConnection)URI.create(urlString).toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID",CLIENT_ID);
            conn.setRequestProperty("X-NCP-APIGW-API-KEY",CLIENT_SECRET);
            int responseCode=conn.getResponseCode();
            BufferedReader br=new BufferedReader(new InputStreamReader(responseCode>=200&&responseCode<300?conn.getInputStream():conn.getErrorStream(),StandardCharsets.UTF_8));
            StringBuilder result=new StringBuilder();
            String line;
            while((line=br.readLine())!=null)result.append(line);
            br.close();
            System.out.println("[NaverLocalSearchUtil] 검색어="+keyword);
            System.out.println("[NaverLocalSearchUtil] 응답="+result);
            return result.toString();
        }catch(Exception e){
            System.out.println("[NaverLocalSearchUtil] 네이버 지역 검색 실패");
            e.printStackTrace();
            return null;
        }finally{
            if(conn!=null)conn.disconnect();
        }
    }

    // =========================================================
    // 주제: 네이버 지도 검색 링크 생성
    // 설명: API가 실제 place link를 안 줄 때 네이버 지도 검색으로 이동
    // =========================================================
    private static String makeMapSearchUrl(String keyword){
        try{
            return "https://map.naver.com/p/search/"+URLEncoder.encode(keyword,StandardCharsets.UTF_8);
        }catch(Exception e){
            return "https://search.naver.com/search.naver?query="+keyword;
        }
    }

    // =========================================================
    // 주제: 네이버 실제 장소 링크 조회
    // 설명: API link가 있으면 사용하고, 없으면 네이버 지도 검색 링크로 대체
    // =========================================================
    public static String getPlaceLink(String name,String address){
        try{
            String keyword=((name!=null?name:"")+" "+(address!=null?address:"")).trim();
            if(keyword.isEmpty())return "";
            String json=searchPlace(keyword);
            if(json==null||json.trim().isEmpty())return makeMapSearchUrl(keyword);
            JsonObject root=JsonParser.parseString(json).getAsJsonObject();
            JsonArray items=root.getAsJsonArray("items");
            if(items==null||items.size()==0)return makeMapSearchUrl(keyword);
            JsonObject item=items.get(0).getAsJsonObject();
            String link=item.has("link")&&!item.get("link").isJsonNull()?item.get("link").getAsString():"";
            if(link!=null&&!link.trim().isEmpty())return link;
            return makeMapSearchUrl(keyword);
        }catch(Exception e){
            System.out.println("[NaverLocalSearchUtil] 네이버 장소 링크 조회 실패");
            e.printStackTrace();
            String keyword=((name!=null?name:"")+" "+(address!=null?address:"")).trim();
            return keyword.isEmpty()?"":makeMapSearchUrl(keyword);
        }
    }
    
 // =========================================================
 // 주제: 지역+키워드 맛집 목록 검색
 // 설명: 예) 부산 + 국밥 → 부산 국밥 맛집 목록을 food.jsp 렌더링용 JSON으로 변환
 // =========================================================
 public static JsonArray searchFoodList(String region,String keyword,double baseLat,double baseLng){
     JsonArray arr=new JsonArray();
     try{
         String query=((region!=null?region:"")+" "+(keyword!=null?keyword:"")+" 맛집").trim();
         String json=searchPlace(query);
         if(json==null||json.trim().isEmpty())return arr;
         JsonObject root=JsonParser.parseString(json).getAsJsonObject();
         JsonArray items=root.getAsJsonArray("items");
         if(items==null)return arr;
         for(int i=0;i<items.size();i++){
             JsonObject item=items.get(i).getAsJsonObject();
             String name=cleanNaverText(getString(item,"title"));
             String category=cleanNaverText(getString(item,"category"));
             String address=getString(item,"roadAddress");
             if(address.isEmpty())address=getString(item,"address");
             double lng=parseNaverCoord(getString(item,"mapx"));
             double lat=parseNaverCoord(getString(item,"mapy"));
             JsonObject o=new JsonObject();
             o.addProperty("name",name);
             o.addProperty("category",category.isEmpty()?"맛집":category);
             o.addProperty("address",address);
             o.addProperty("phone","");
             o.addProperty("rating",0);
             o.addProperty("reviewCount",0);
             o.addProperty("image","");
             o.addProperty("hours","");
             o.addProperty("hoursNote","");
             o.addProperty("menu",keyword==null?"":keyword);
             o.addProperty("menuNote","");
             o.addProperty("comment",name+"은(는) "+address+"에 위치한 맛집입니다. 네이버 지도에서 최신 정보와 리뷰를 확인해보세요.");
             o.addProperty("naverLink","");
             o.addProperty("distance",calcDistance(baseLat,baseLng,lat,lng));
             o.addProperty("lat",lat);
             o.addProperty("lng",lng);
             arr.add(o);
         }
     }catch(Exception e){
         System.out.println("[NaverLocalSearchUtil] 지역 키워드 맛집 검색 실패");
         e.printStackTrace();
     }
     return arr;
 }

 // =========================================================
 // 주제: 네이버 검색 결과 문자열 정리
 // 설명: <b>, &amp; 같은 값을 화면용 텍스트로 정리
 // =========================================================
 private static String cleanNaverText(String text){
     if(text==null)return "";
     return text.replaceAll("<[^>]*>","").replace("&amp;","&").replace("&lt;","<").replace("&gt;",">").replace("&quot;","\"").trim();
 }

	 // =========================================================
	 // 주제: JSON 문자열 안전 추출
	 // 설명: 값이 없거나 null이면 빈 문자열 반환
	 // =========================================================
	 private static String getString(JsonObject obj,String key){
	     if(obj==null||!obj.has(key)||obj.get(key).isJsonNull())return "";
	     return obj.get(key).getAsString();
	 }
	
	 // =========================================================
	 // 주제: 네이버 좌표 변환
	 // 설명: mapx/mapy 값을 지도에서 쓰는 위도/경도 숫자로 변환
	 // =========================================================
	 private static double parseNaverCoord(String value){
	     try{
	         if(value==null||value.trim().isEmpty())return 0;
	         return Double.parseDouble(value)/10000000.0;
	     }catch(Exception e){
	         return 0;
	     }
	 }
	
	 // =========================================================
	 // 주제: 거리 계산
	 // 설명: 현재 중심 좌표와 검색 결과 사이 거리 km 계산
	 // =========================================================
	 private static double calcDistance(double lat1,double lng1,double lat2,double lng2){
	     if(lat1==0||lng1==0||lat2==0||lng2==0)return 0;
	     double r=6371.0;
	     double dLat=Math.toRadians(lat2-lat1);
	     double dLng=Math.toRadians(lng2-lng1);
	     double a=Math.sin(dLat/2)*Math.sin(dLat/2)+Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*Math.sin(dLng/2)*Math.sin(dLng/2);
	     double c=2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
	     return r*c;
	 }
    
}
