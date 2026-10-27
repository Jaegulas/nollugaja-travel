package service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import api.CommonGetApiKey;
import vo.AccommodationGoecoderResultVo;

// =========================================================
// 주제: 파일 역할
// 설명: AccommodationKakaoGeocoderService의 핵심 비즈니스 로직과 데이터 가공을 담당하는 서비스
// =========================================================


//
//카카오위도경도정리
//1.Accommodation/kakaogeocoder/insert?limit 서블릿매핑 컨트롤러에서 kakao.api.key 얻고
//2.bachInsertKakaoGeocoder 서비스메소드로 간다.
//3.bachFindEmptyKakaoGeocoder DAO메소드에서 경도위도 빈레코드를 찾는다 숙박VO List반환
//4.AccommodationKakaoGeocoderService클래스의 findCoords() 인인수로 업체명과 주소를 전달해서 AccommodationGoecoderResult 결과반환한다. (lat, lon, tel, placeid 값채워진다.)
//5.컨트롤러에서 숙박VO의 id를 결과VO에 setId() 넣어준다.
//6.bachInsertKakaoGeocoder DAO메소드로 배치inser한다.
public class AccommodationKakaoGeocoderService {

	
	private static final String KAKAO_API_JS_KEY =CommonGetApiKey.get("kakao.api.js.key", "KAKAO_API_JS_KEY", "kakao.api.js.key");
    
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5)).build();

    /** 쿼터 초과·인증 오류처럼 배치를 멈춰야 하는 상황 */
    public static class QuotaException extends Exception {
        // =========================================================
        // 주제: QuotaException 처리
        // 설명: QuotaException 기능에 필요한 처리 로직을 수행
        // =========================================================
        public QuotaException(String msg) { super(msg); }
    }

    /** 1순위 숙소명 키워드 검색, 2순위 주소 검색. 못 찾으면 null */
    public AccommodationGoecoderResultVo makeKakaoGeocoder(String name, String addr, Integer accommodation_id) throws IOException, QuotaException {
        String sigungu = sigunguOf(addr);
        System.out.println("[AccommodationKakaoGeocoderService] findmakeKakaoGeocoderCoords() 주소 2번째 문자열 : "+ sigungu);
        
        if (name != null && sigungu != null) {

       	 System.out.println("[AccommodationKakaoGeocoderService] makeKakaoGeocoder() 주소 업체명 모두 전달될 경우 키워드로 찾기 카카오서비스 이용");
            JsonArray docs = call("https://dapi.kakao.com/v2/local/search/keyword.json"
                    + "?category_group_code=AD5&size=5&query=" + enc(name + " " + sigungu));
            for (JsonElement e : docs) {
                JsonObject d = e.getAsJsonObject();
                //아래 이름으로 카카오톡에서 전달되어온다.
                System.out.println("[AccommodationKakaoGeocoderService] makeKakaoGeocoder() JsonObject : "+ d);
                String a1 = str(d, "address_name"), a2 = str(d, "road_address_name");
                System.out.println("[AccommodationKakaoGeocoderService] makeKakaoGeocoder() String : "+ a1);
                
                //주소에 문자열포함확인
                if ((a1 != null && a1.contains(sigungu)) || (a2 != null && a2.contains(sigungu))) {
                    return new AccommodationGoecoderResultVo(d.get("y").getAsDouble(), 
                    		d.get("x").getAsDouble(), null,str(d, "phone"), str(d, "id"));
                    //str()은 키가 아예 없거나 값이 JSON null이면 null을 돌려주고, 있으면 문자열을 돌려줍니다.
                }
            }
        }

        if (addr != null && !addr.isBlank()) {
        	 System.out.println("[AccommodationKakaoGeocoderService] makeKakaoGeocoder() 주소만 존재할경우 주소찾기 카카오서비스 이용 주소 : "+addr);
             
            JsonArray docs = call("https://dapi.kakao.com/v2/local/search/address.json"
                    + "?size=1&query=" + enc(addr));
            if (docs.size() > 0) {
                JsonObject d = docs.get(0).getAsJsonObject();
                return new AccommodationGoecoderResultVo(d.get("y").getAsDouble(), d.get("x").getAsDouble(), null,
                		str(d, "phone"), str(d, "id")); // 괄호는 카카오데이터의 키값들
            }
        }
        System.out.println("[AccommodationKakaoGeocoderService] makeKakaoGeocoder() 찾기불가능 accommodation_id : "+accommodation_id);
        
        return null;
    }

    // =========================================================
    // 주제: call 처리
    // 설명: call 기능에 필요한 처리 로직을 수행
    // =========================================================
    private JsonArray call(String url) throws IOException, QuotaException {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .header("Authorization", "KakaoAK " + KAKAO_API_JS_KEY)
                .timeout(Duration.ofSeconds(10))
                .GET().build();

        HttpResponse<String> res;
        try {
            res = client.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("카카오 API 호출 중 인터럽트", e);
        }

        int code = res.statusCode();
        if (code == 429) throw new QuotaException("호출 한도 초과(429)");
        if (code == 401 || code == 403) throw new QuotaException("인증/권한 오류(" + code + ")");
        if (code != 200) return new JsonArray();

        return JsonParser.parseString(res.body()).getAsJsonObject().getAsJsonArray("documents");
    }

    // =========================================================
    // 주제: sigunguOf 처리
    // 설명: sigunguOf 기능에 필요한 처리 로직을 수행
    // =========================================================
    private static String sigunguOf(String addr) {
        if (addr == null) return null;
        String[] t = addr.trim().split("\\s+");
        return t.length >= 2 ? t[1] : null;
    }
    private static String enc(String s) { return URLEncoder.encode(s, StandardCharsets.UTF_8); }
    
    
    // =========================================================
    // 주제: str 처리
    // 설명: str 기능에 필요한 처리 로직을 수행
    // =========================================================
    private static String str(JsonObject o, String k) {
        JsonElement v = o.get(k);
        return (v == null || v.isJsonNull()) ? null : v.getAsString();
    }
}
