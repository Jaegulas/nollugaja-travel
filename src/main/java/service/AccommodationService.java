package service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

import api.CommonGetApiKey;
import dao.AccommodationDao;
import vo.AccommodationGoecoderResultVo;
import vo.AccommodationReviewVo;
import vo.AccommodationVo;

// =========================================================
// 주제: 파일 역할
// 설명: AccommodationService의 핵심 비즈니스 로직과 데이터 가공을 담당하는 서비스
// =========================================================


public class AccommodationService {
    
	private transient AccommodationDao ad;
	
	private static final String PUBLIC_API_DECODING_KEY =CommonGetApiKey.get("public.api.decoding.key", "PUBLIC_API_DECODING_KEY", "public.api.decoding.key");
	
	// =========================================================
	// 주제: AccommodationService 처리
	// 설명: AccommodationService 기능에 필요한 처리 로직을 수행
	// =========================================================
	public AccommodationService() {	
            this.ad = new AccommodationDao();
    }
	
	//지역선택시 기본DB숙박데이터 가져오기
	public List<AccommodationVo> getAccommodationListFromRegion(String region, String loginId) throws Exception{
		System.out.println("[AccommodationService] getAccommodationListFromRegion()");
		return ad.getAccommodationListFromRegion(region, loginId);
	}
	
	// ==========================================
	// 메인 화면 숙박 목록 조회
	// ==========================================
	public List<AccommodationVo> getMainAccommodationList(
	        String loginId) throws SQLException {

	    return ad.getMainAccommodationList(loginId);
	}

	// =========================================================
	// 주제: registerAccommodation 처리
	// 설명: 입력받은 데이터를 생성하거나 저장
	// =========================================================
	public int registerAccommodation(AccommodationVo vo) throws Exception{
		System.out.println("[AccommodationService] registerAccommodation()");
		return ad.registerAccommodation(vo);
	}
	

	// =========================================================
	// 주제: bachInsertPublicAPI 처리
	// 설명: bachInsertPublicAPI 기능에 필요한 처리 로직을 수행
	// =========================================================
	public int bachInsertPublicAPI() throws IOException, InterruptedException, SQLException {
		return ad.bachInsertPublicAPI(PUBLIC_API_DECODING_KEY);
	}

	public int bachInsertPublicAPICulture() throws IOException, InterruptedException, SQLException {
		return ad.bachInsertPublicAPICulture(PUBLIC_API_DECODING_KEY);
	}
	
	// =========================================================
	// 주제: bachInsertPublicAPICultureTourism 처리
	// 설명: bachInsertPublicAPICultureTourism 기능에 필요한 처리 로직을 수행
	// =========================================================
	public int bachInsertPublicAPICultureTourism() throws IOException, InterruptedException, SQLException {
		return ad.bachInsertPublicAPICultureTourism(PUBLIC_API_DECODING_KEY);
	}

	public String bachInsertKakaoGeocoder(int limit, int offset) throws SQLException, IOException {
		 List<AccommodationVo> targets = ad.bachFindEmptyKakaoGeocoder(limit, offset, PUBLIC_API_DECODING_KEY);
		    int ok = 0, fail = 0;
		    String stopReason = null;

		    for (AccommodationVo vo : targets) {
		        AccommodationGoecoderResultVo  geo;
		        try {
		        	AccommodationKakaoGeocoderService kakaoClient = new AccommodationKakaoGeocoderService();
		            geo = kakaoClient.makeKakaoGeocoder(vo.getName(), vo.getAddr(), vo.getAccommodation_id());
		            System.out.println("[AccommodaitonService] bachInsertKakaoGeocoder() 카카오서비스에서 얻은 데이터로 AccommodationGoecoderResult : "+geo);
		        } catch (AccommodationKakaoGeocoderService.QuotaException qe) {
		            stopReason = qe.getMessage();
		            break;
		        }
		        geo.setId(vo.getAccommodation_id());
		        ad.bachInsertKakaoGeocoder(geo);
		        if (geo != null) ok++; else fail++;
		    }

		    return "대상 " + targets.size() + "건 중 성공 " + ok + "건, 실패 " + fail + "건"
		         + (stopReason != null ? " / 중단: " + stopReason : "");
		
	}

	// =========================================================
	// 주제: accommodationReviewInsert 처리
	// 설명: accommodationReviewInsert 기능에 필요한 처리 로직을 수행
	// =========================================================
	public int accommodationReviewInsert(String content, Integer accommodationId) throws SQLException{
		return ad.accommodationReviewInsert(content, accommodationId);
		
	}
	
	public AccommodationVo findAccommodationByPlaceid(String placeid) throws SQLException {
		return ad.findAccommodationByPlaceid(placeid);
	}

	// =========================================================
	// 주제: findReviewByAccommodationId 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	public AccommodationReviewVo findReviewByAccommodationId(Integer accommodationId) throws SQLException {
		return ad.findReviewByAccommodationId(accommodationId);
		
	}
	
	public int searchAccommodationId_registerAccommodation(String placeid) throws SQLException {
		return ad.searchAccommodationId_registerAccommodation(placeid);
		
	}
	
	//해당 숙박 근처 km 찾아서 보여주기
	// =========================================================
	// 주제: st_distance_sphere 처리
	// 설명: st_distance_sphere 기능에 필요한 처리 로직을 수행
	// =========================================================
	public List<AccommodationVo> st_distance_sphere(Double lon, Double lat, Integer accommodation_id, int distance)throws SQLException {
		return ad.st_distance_sphere(lon, lat, accommodation_id, distance);
	}

	public AccommodationVo findAccommdationByAccommdationId(Integer accommodation_id) throws SQLException {
		
		return ad.findAccommdationByAccommdationId(accommodation_id);
	}

	// =========================================================
	// 주제: insertLike 처리
	// 설명: 입력받은 데이터를 생성하거나 저장
	// =========================================================
	public int insertLike(String loginId, int accommodationId)  throws SQLException {
		
		return ad.insertLike(loginId, accommodationId);
	}


	// =========================================================
	// 주제: deleteLike 처리
	// 설명: 대상 데이터를 삭제하거나 제거
	// =========================================================
	public int deleteLike(String loginId, int accommodationId)  throws SQLException {
		return ad.deleteLike(loginId, accommodationId);
	}

	//Accommodation/openrouter_summary_search 서블릿경로에서 사용
	public boolean isAccommodationLiked(String loginId, Integer accommodation_id) throws SQLException{
		// TODO Auto-generated method stub
		return ad.isAccommodationLiked(loginId, accommodation_id);
	}


}
