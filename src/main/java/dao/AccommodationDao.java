package dao;



import java.io.IOException;

import java.net.URI;

import java.net.URLEncoder;

import java.net.http.HttpClient;

import java.net.http.HttpRequest;

import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;

import java.sql.Connection;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.sql.SQLIntegrityConstraintViolationException;

import java.sql.Timestamp;

import java.sql.Types;

import java.time.Duration;

import java.time.LocalDateTime;

import java.util.ArrayList;

import java.util.List;



import org.json.simple.JSONObject;

import com.google.gson.JsonArray;

import com.google.gson.JsonElement;

import com.google.gson.JsonObject;

import com.google.gson.JsonParser;



import service.AccommodationKakaoGeocoderService;



import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;



import util.AccommodationTypeConverter;

import util.AccommodationTypeTwoConverter;

import util.DBCPUtil;

import vo.AccommodationGoecoderResultVo;

import vo.AccommodationReviewVo;

import vo.AccommodationVo;



// =========================================================

// 주제: 파일 역할

// 설명: AccommodationDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO

// =========================================================





public class AccommodationDao {





	private Connection con;

	// 한국관광공사_국문 관광정보 서비스_GW 7번숙박정보조회

    private static final String API_URL = "http://apis.data.go.kr/B551011/KorService2/searchStay2";

    private static final String API_URL_CULTURE = "http://apis.data.go.kr/1741000/lodgings/info";

    private static final String API_URL_CULTURE_TOURISM = "https://apis.data.go.kr/1741000/tourist_accommodations/info";



    private static final HttpClient client = HttpClient.newBuilder()

            .connectTimeout(Duration.ofSeconds(10))

            .build();



	private AccommodationTypeConverter tc;

	private AccommodationTypeTwoConverter tc2;







	// =========================================================

	// 주제: AccommodationDao 처리

	// 설명: AccommodationDao 기능에 필요한 처리 로직을 수행

	// =========================================================

	public AccommodationDao() {

		tc= new AccommodationTypeConverter();

		tc2 =new AccommodationTypeTwoConverter();

	}



	public int registerAccommodation(AccommodationVo vo) throws Exception {





		 // 필수값 확인: 없으면 등록하면 안 되는 값

	    if (vo.getName() == null || vo.getName().isBlank()) {

	        throw new IllegalArgumentException("숙소 이름이 없습니다.");

	    }

	    if (vo.getLat() == null || vo.getLon() == null) {

	        throw new IllegalArgumentException("좌표가 없습니다: " + vo.getName());

	    }





		String query ="insert into accommodation( placeid, name, addr, type, tel, link, lat, lon, avg_rating, picpath)"

				+" values (?,?,?,?,? , ?,?,?,?,?)";



		int result = 0;



		try(Connection con = DBCPUtil.getConnection();

			PreparedStatement pstmt = con.prepareStatement(query)){

			pstmt.setString(1, vo.getPlaceid());

			pstmt.setString(2, vo.getName());

			pstmt.setString(3, vo.getAddr());

			pstmt.setString(4, vo.getType());

			pstmt.setString(5, vo.getTel());

			pstmt.setString(6, vo.getLink());

			pstmt.setDouble(7, vo.getLat()); //db컬럼은 decimal

			pstmt.setDouble(8, vo.getLon());

			//pstmt.setInt(9, vo.getAvg_rating());

			pstmt.setObject(9, vo.getAvg_rating(), java.sql.Types.DECIMAL);//3개 인수필요

			pstmt.setString(10, vo.getPicpath());



			result =pstmt.executeUpdate();



		}catch(Exception e) {

			e.printStackTrace();

			System.out.println("[AccommodationDao] registerAccommodation() 에러발생");

			throw new RuntimeException("서버에 문제가 있습니다.");

		}

		return result;		

	}



	//지역선택시 기본DB숙박데이터 가져오기

	// =========================================================

	// 주제: getAccommodationListFromRegion 처리

	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환

	// =========================================================

	public List<AccommodationVo> getAccommodationListFromRegion(String region, String loginId) throws Exception{

		System.out.println("[AccommodationDao] getAccommodationListFromRegion() 인수값 확인  region : "+region +", loginId : "+loginId);

//		String query ="select accommodation_id, placeid, name, addr, type, tel, "+

//						" link, lat, lon, avg_rating, review_count, isliked , "+

//							"picpath, updated_at from accommodation where addr like '"+region +"%' limit 9";



		//좋아요 추가되어 아래로 변경

		String query = "SELECT a.accommodation_id, a.placeid, a.name, a.addr, a.type, a.tel, "

	             + "       a.link, a.lat, a.lon, a.avg_rating, a.review_count, "

	             + "       a.picpath, a.updated_at, "

	             + "       (l.member_id IS NOT NULL) AS isliked "

	             + "FROM accommodation a "

	             + "LEFT JOIN accommodation_like l "

	             + "       ON l.accommodation_id = a.accommodation_id AND l.member_id = ? "

	             + "WHERE a.addr LIKE ? "

	             + "LIMIT 9";





		List<AccommodationVo> result = new ArrayList<>();

		AccommodationVo vo = new AccommodationVo();



		try(Connection con = DBCPUtil.getConnection();

			PreparedStatement pstmt = con.prepareStatement(query)){

			pstmt.setString(1, loginId); // 없으면 null 값들어가도 된다.

			pstmt.setString(2, region +"%");



			try(ResultSet rs = pstmt.executeQuery()){



				while(rs.next()) { //데이터없으면 이반복문 실행하지않는다.

					// accommodation_id, placeid, name, addr, type, tel, link, lat, lon, avg_rating, review_count, isliked , picpath, updated_at - vo

					/*

					 * public AccommodationVo(Integer accommodation_id, String placeid, String name,

					 * String addr, String type, String tel, String link, Double lat, Double lon,

					 * Integer avg_rating, Integer review_count, Boolean isliked, String picpath,

					 * Timestamp upadated_at) {

					 * 

					 * }

					 */			

					vo = new AccommodationVo(rs.getInt("accommodation_id"), rs.getString("placeid"), rs.getString("name"), 

							rs.getString("addr"), rs.getString("type"), rs.getString("tel"), rs.getString("link"), rs.getDouble("lat"),

							rs.getDouble("lon"), rs.getDouble("avg_rating"),

							rs.getInt("review_count"), rs.getBoolean("isliked"), rs.getString("picpath"), rs.getObject("updated_at",LocalDateTime.class));

					System.out.println(vo.toString());

					result.add(vo);

				}

			}//ResultSet



		}catch(Exception e) {

			e.printStackTrace();

			System.out.println("[AccommodationDao] getAccommodationListFromRegion() 에러발생");

			throw new RuntimeException("서버에 문제가 있습니다.");

		}



		return result;



	}





	//한국관광공사_국문 관광정보서비스

	// =========================================================

	// 주제: bachInsertPublicAPI 처리

	// 설명: bachInsertPublicAPI 기능에 필요한 처리 로직을 수행

	// =========================================================

	public int bachInsertPublicAPI(String publicAPIDecodingKey) throws IOException, InterruptedException, SQLException {

		System.out.println("[AccommodationDao] bachInsertPublicAPI()");



		int pageNo = 1, numOfRows = 10, inserted = 0;



	        String sql = "INSERT INTO accommodation (contentid, name, addr, type, type2, picpath ) "

	                   + "VALUES (?, ?, ?, ?, ?, ?)";



	        try (Connection conn = DBCPUtil.getConnection();          // 기존 DB 연결 방식 사용

	             PreparedStatement ps = conn.prepareStatement(sql)) {



	            conn.setAutoCommit(false);



	            while (true) {

	                String url = API_URL

	                        + "?serviceKey=" + URLEncoder.encode(publicAPIDecodingKey, StandardCharsets.UTF_8)

	                        + "&MobileOS=ETC&MobileApp=MyApp&_type=json"

	                        + "&numOfRows=" + numOfRows + "&pageNo=" + pageNo+"&arrange=C";



	                HttpRequest request = HttpRequest.newBuilder(URI.create(url))

	                        .timeout(Duration.ofSeconds(30))

	                        .GET()

	                        .build();



	                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());



	                if (response.statusCode() != 200) {

	                    throw new IOException("[AccommodationDao] bachInsertPublicAPI() IOException 발생" + response.statusCode() + " / " + response.body());

	                }



	                JsonObject  body = JsonParser.parseString(response.body()).getAsJsonObject().getAsJsonObject("response").getAsJsonObject("body");

	                int totalCount = body.get("totalCount").getAsInt();



	                // 결과가 없으면 items가 "" 로 오는 경우가 있음

	                JsonElement itemsEl = body.get("items");

	                if (itemsEl == null || !itemsEl.isJsonObject()) {

	                	System.out.println("[AccommodationDao] bachInsertPublicAPI() 응답이 빈문자열로 와서 break처리됨");

	                	break;

	                }

	                //const items = data.response.body.items.item;

	                // 결과가 1건이면 item이 배열이 아니라 객체로 오는 경우가 있음

	                JsonElement itemEl = itemsEl.getAsJsonObject().get("item");

	                System.out.println("item 값 \n"+itemEl);

	                //{"addr1":"충청남도 보령시 대천항1길 17 (신흑동)",

	                //"addr2":""

	                //areacode":"","sigungucode":"","cat1":"","cat2":"","cat3":"",

	                //"contentid":"2917187","contenttypeid":"32","createdtime":"20221124094526",

	                //"firstimage":"https://tong.visitkorea.or.kr/cms/resource/72/4073272_image2_1.jpg",

	                //"firstimage2":"https://tong.visitkorea.or.kr/cms/resource/72/4073272_image3_1.jpg",

	                //"cpyrhtDivCd":"Type3","mapx":"127.0990775152","mapy":"37.0402016414","mlevel":"6",

	                //"modifiedtime":"20260610151354","tel":"","title":"부티크호텔 XYM","zipcode":"17725",

	                //"lDongRegnCd":"41","lDongSignguCd":"220","lclsSystm1":"AC","lclsSystm2":"AC01",

	                //"lclsSystm3":"AC010100"},



	                JsonArray items = new JsonArray();

	                if (itemEl.isJsonArray()) {

	                	System.out.println("[AccommodationDao] bachInsertPublicAPI() 응답이 배열이 아니라 객체로와서 배열로 변환해서 JsonArray 저장시킴");



	                	items = itemEl.getAsJsonArray();

	                }

	                else items.add(itemEl);



	                for (JsonElement e : items) {

	                	//System.out.println(e);

	                	JsonObject it = e.getAsJsonObject();

	                    System.out.println("JsonElement.getAsJsonObject() 반환 JsonObject객체 : "+ it);

	                    ps.setString(1, str(it, "contentid"));

	                    ps.setString(2, str(it, "title"));

	                    ps.setString(3, str(it, "addr1"));

	                    //ps.setString(4, str(it, "type"));

	                    String typeCode = str(it, "lclsSystm2");

	                    ps.setString(4, tc.convertCategory(typeCode));

	                    //ps.setString(5, str(it, "typ2"));

	                    String typeCode2 = str(it, "lclsSystm3");

	                    ps.setString(5, tc2.convertCategory(typeCode2));

	                    ps.setString(6, str(it, "firstimage"));

	                    ps.addBatch();

	                }

	                ps.executeBatch();

	                conn.commit();

	                inserted += items.size();



	                if (pageNo * numOfRows >= totalCount) break;

	                pageNo++;

	            }//while

            }//try

	        catch(InterruptedException e) {

	        	throw new InterruptedException("[AccommodationDao] bachInsertPublicAPI() InterruptedException 발생");

	        }catch(SQLException e) {

	        	throw new SQLException("[AccommodationDao] bachInsertPublicAPI() SQLException 발생",e);



	        }

	        return inserted;

        }







	//행정안정부 문화 숙박 6만개 제일많음

	// =========================================================

	// 주제: bachInsertPublicAPICulture 처리

	// 설명: bachInsertPublicAPICulture 기능에 필요한 처리 로직을 수행

	// =========================================================

	public int bachInsertPublicAPICulture(String publicAPIDecodingKey) throws IOException, InterruptedException, SQLException {

		System.out.println("[AccommodationDao] bachInsertPublicAPICulture()");



		int pageNo = 1, numOfRows = 1000, inserted = 0;



	        String sql = "INSERT INTO accommodation (contentid, name, addr, type ) "

	                   + "VALUES (?, ?, ?, ?)";



	        try (Connection conn = DBCPUtil.getConnection();          // 기존 DB 연결 방식 사용

	             PreparedStatement ps = conn.prepareStatement(sql)) {



	            conn.setAutoCommit(false);



	            while (true) {

	                String url = API_URL_CULTURE

	                        + "?serviceKey=" + URLEncoder.encode(publicAPIDecodingKey, StandardCharsets.UTF_8)

	                        + "&numOfRows=" + numOfRows + "&pageNo=" + pageNo+"&returnType=json";





	                HttpRequest request = HttpRequest.newBuilder(URI.create(url))

	                        .timeout(Duration.ofSeconds(30))

	                        .GET()

	                        .build();



	                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());



	                if (response.statusCode() != 200) {

	                    throw new IOException("[AccommodationDao] bachInsertPublicAPICulture() IOException 발생" + response.statusCode() + " / " + response.body());

	                }



	                JsonObject  body = JsonParser.parseString(response.body()).getAsJsonObject().getAsJsonObject("response").getAsJsonObject("body");

	                int totalCount = body.get("totalCount").getAsInt();



	                // 결과가 없으면 items가 "" 로 오는 경우가 있음

	                JsonElement itemsEl = body.get("items");

	                if (itemsEl == null || !itemsEl.isJsonObject()) {

	                	System.out.println("[AccommodationDao] bachInsertPublicAPICulture() 응답이 빈문자열로 와서 break처리됨");

	                	break;

	                }

	                //const items = data.response.body.items.item;

	                // 결과가 1건이면 item이 배열이 아니라 객체로 오는 경우가 있음

	                JsonElement itemEl = itemsEl.getAsJsonObject().get("item");

	                System.out.println("item 값 \n"+itemEl);

	                //{"addr1":"충청남도 보령시 대천항1길 17 (신흑동)",

	                //"addr2":""

	                //areacode":"","sigungucode":"","cat1":"","cat2":"","cat3":"",

	                //"contentid":"2917187","contenttypeid":"32","createdtime":"20221124094526",

	                //"firstimage":"https://tong.visitkorea.or.kr/cms/resource/72/4073272_image2_1.jpg",

	                //"firstimage2":"https://tong.visitkorea.or.kr/cms/resource/72/4073272_image3_1.jpg",

	                //"cpyrhtDivCd":"Type3","mapx":"127.0990775152","mapy":"37.0402016414","mlevel":"6",

	                //"modifiedtime":"20260610151354","tel":"","title":"부티크호텔 XYM","zipcode":"17725",

	                //"lDongRegnCd":"41","lDongSignguCd":"220","lclsSystm1":"AC","lclsSystm2":"AC01",

	                //"lclsSystm3":"AC010100"},



	                JsonArray items = new JsonArray();

	                if (itemEl.isJsonArray()) {

	                	System.out.println("[AccommodationDao] bachInsertPublicAPICulture() 응답이 배열이 아니라 객체로와서 배열로 변환해서 JsonArray 저장시킴");



	                	items = itemEl.getAsJsonArray();

	                }

	                else items.add(itemEl);



	                for (JsonElement e : items) {

	                	//System.out.println(e);

	                	JsonObject it = e.getAsJsonObject();

	                    System.out.println("JsonElement.getAsJsonObject() 반환 JsonObject객체 : "+ it);

	                    ps.setString(1, str(it, "MNG_NO")); //"4651000-201-2026-00003"

	                    ps.setString(2, str(it, "BPLC_NM"));

	                    ps.setString(3, str(it, "ROAD_NM_ADDR"));

	                    ps.setString(4, str(it,"BZSTAT_SE_NM"));



	                    ps.addBatch();

	                }

	                ps.executeBatch();

	                conn.commit();

	                inserted += items.size();



	                if (pageNo * numOfRows >= totalCount) break;

	                pageNo++;

	            }//while

            }//try

	        catch(InterruptedException e) {

	        	throw new InterruptedException("[AccommodationDao] bachInsertPublicAPICulture() InterruptedException 발생");

	        }catch(SQLException e) {

	        	throw new SQLException("[AccommodationDao] bachInsertPublicAPICulture() SQLException 발생",e);



	        }

	        return inserted;

        }









	//행정안정부 문화 숙박관광

	// =========================================================

	// 주제: bachInsertPublicAPICultureTourism 처리

	// 설명: bachInsertPublicAPICultureTourism 기능에 필요한 처리 로직을 수행

	// =========================================================

	public int bachInsertPublicAPICultureTourism(String publicAPIDecodingKey) throws IOException, InterruptedException, SQLException {

		System.out.println("[AccommodationDao] bachInsertPublicAPICultureTourism()");



		int pageNo = 1, numOfRows = 1000, inserted = 0;



	        String sql = "INSERT INTO accommodation (name, addr, type , type2, tel) "

	                   + "VALUES (?, ?, ?, ?, ?)";



	        try (Connection conn = DBCPUtil.getConnection();          // 기존 DB 연결 방식 사용

	             PreparedStatement ps = conn.prepareStatement(sql)) {



	            conn.setAutoCommit(false);



	            while (true) {

	                String url = API_URL_CULTURE_TOURISM

	                        + "?serviceKey=" + URLEncoder.encode(publicAPIDecodingKey, StandardCharsets.UTF_8)

	                        + "&numOfRows=" + numOfRows + "&pageNo=" + pageNo+"&returnType=json";





	                HttpRequest request = HttpRequest.newBuilder(URI.create(url))

	                        .timeout(Duration.ofSeconds(30))

	                        .GET()

	                        .build();



	                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());



	                if (response.statusCode() != 200) {

	                    throw new IOException("[AccommodationDao] bachInsertPublicAPICultureTourism() IOException 발생" + response.statusCode() + " / " + response.body());

	                }



	                JsonObject  body = JsonParser.parseString(response.body()).getAsJsonObject().getAsJsonObject("response").getAsJsonObject("body");

	                int totalCount = body.get("totalCount").getAsInt();



	                // 결과가 없으면 items가 "" 로 오는 경우가 있음

	                JsonElement itemsEl = body.get("items");

	                if (itemsEl == null || !itemsEl.isJsonObject()) {

	                	System.out.println("[AccommodationDao] bachInsertPublicAPICultureTourism() 응답이 빈문자열로 와서 break처리됨");

	                	break;

	                }

	                //const items = data.response.body.items.item;

	                // 결과가 1건이면 item이 배열이 아니라 객체로 오는 경우가 있음

	                JsonElement itemEl = itemsEl.getAsJsonObject().get("item");

	                System.out.println("item 값 \n"+itemEl);

	                //{"addr1":"충청남도 보령시 대천항1길 17 (신흑동)",

	                //"addr2":""

	                //areacode":"","sigungucode":"","cat1":"","cat2":"","cat3":"",

	                //"contentid":"2917187","contenttypeid":"32","createdtime":"20221124094526",

	                //"firstimage":"https://tong.visitkorea.or.kr/cms/resource/72/4073272_image2_1.jpg",

	                //"firstimage2":"https://tong.visitkorea.or.kr/cms/resource/72/4073272_image3_1.jpg",

	                //"cpyrhtDivCd":"Type3","mapx":"127.0990775152","mapy":"37.0402016414","mlevel":"6",

	                //"modifiedtime":"20260610151354","tel":"","title":"부티크호텔 XYM","zipcode":"17725",

	                //"lDongRegnCd":"41","lDongSignguCd":"220","lclsSystm1":"AC","lclsSystm2":"AC01",

	                //"lclsSystm3":"AC010100"},



	                JsonArray items = new JsonArray();

	                if (itemEl.isJsonArray()) {

	                	System.out.println("[AccommodationDao] bachInsertPublicAPICultureTourism() 응답이 배열이 아니라 객체로와서 배열로 변환해서 JsonArray 저장시킴");



	                	items = itemEl.getAsJsonArray();

	                }

	                else items.add(itemEl);



	                for (JsonElement e : items) {

	                	//System.out.println(e);

	                	JsonObject it = e.getAsJsonObject();

	                    System.out.println("JsonElement.getAsJsonObject() 반환 JsonObject객체 : "+ it);

	                    //ps.setString(1, str(it, "MNG_NO")); //"CDFI2260032026000019" 유티크값아니라서 제외

	                    ps.setString(1, str(it, "BPLC_NM"));

	                    ps.setString(2, str(it, "ROAD_NM_ADDR"));

	                    ps.setString(3, str(it, "BLDG_USG_NM"));

	                    ps.setString(4, str(it, "TOUR_LDGBIZ_DTL_NM"));

	                    ps.setString(5, str(it, "TELNO"));





	                    ps.addBatch();

	                }

	                ps.executeBatch();

	                conn.commit();

	                inserted += items.size();



	                if (pageNo * numOfRows >= totalCount) break;

	                pageNo++;

	            }//while

            }//try

	        catch(InterruptedException e) {

	        	throw new InterruptedException("[AccommodationDao] bachInsertPublicAPICultureTourism() InterruptedException 발생");

	        }catch(SQLException e) {

	        	throw new SQLException("[AccommodationDao] bachInsertPublicAPICultureTourism() SQLException 발생",e);



	        }

	        return inserted;

        }





	    // =========================================================

	    // 주제: str 처리

	    // 설명: str 기능에 필요한 처리 로직을 수행

	    // =========================================================

	    private static String str(JsonObject o, String key) {

	        JsonElement v = o.get(key);

	        return (v == null || v.isJsonNull()) ? null : v.getAsString();

	    }







		// =========================================================

		// 주제: bachFindEmptyKakaoGeocoder 처리

		// 설명: bachFindEmptyKakaoGeocoder 기능에 필요한 처리 로직을 수행

		// =========================================================

		public List<AccommodationVo> bachFindEmptyKakaoGeocoder(int limit, int offset, String apiKey) throws SQLException{

			System.out.println("[AccommodadtionDAO] bachFindEmptyKakaoGeocoder() 위도경도 빈레코드 List<AccommodationVo> 얻기");

			 String sql = "SELECT accommodation_id, name, addr FROM accommodation "

		               + "WHERE lat IS NULL AND lon IS NULL "

		               + "ORDER BY accommodation_id LIMIT ? offset ? ";//LIMIT 개수 OFFSET 위치 (직관적인 권장 방식)

		    List<AccommodationVo> list = new ArrayList<>();



		    try (Connection conn = DBCPUtil.getConnection();

		         PreparedStatement ps = conn.prepareStatement(sql)) {

		        ps.setInt(1, limit);

		        ps.setInt(2, offset);

		        try (ResultSet rs = ps.executeQuery()) {

		            while (rs.next()) {

		                AccommodationVo vo = new AccommodationVo();

		                vo.setAccommodation_id(rs.getInt("accommodation_id"));

		                vo.setName(rs.getString("name"));

		                vo.setAddr(rs.getString("addr"));

		                list.add(vo);

		            }

		        }

		    } catch (SQLException e) {

		        throw new SQLException("[AccommodationDao] bachFindEmptyKakaoGeocoder() 실패", e);

		    }

		    return list;

		}





		// =========================================================

		// 주제: bachInsertKakaoGeocoder 처리

		// 설명: bachInsertKakaoGeocoder 기능에 필요한 처리 로직을 수행

		// =========================================================

		public void bachInsertKakaoGeocoder(AccommodationGoecoderResultVo geo) throws SQLException {

		    String sql = "UPDATE accommodation SET lat = ?, lon = ? , tel =? , placeid = ? "

		               + "WHERE accommodation_id = ?";



		    try (Connection conn = DBCPUtil.getConnection();

		         PreparedStatement ps = conn.prepareStatement(sql)) {



		        if (geo != null) {

		            ps.setDouble(1, geo.getLat());

		            ps.setDouble(2, geo.getLon());

		            ps.setString(3, geo.getTel());

		            ps.setString(4, geo.getPlaceid());

		        } else {

		            ps.setNull(1, Types.DOUBLE);

		            ps.setNull(2, Types.DOUBLE);

		            ps.setNull(3, Types.NVARCHAR);

		            ps.setNull(4, Types.NVARCHAR);



		        }

		        ps.setInt(5, geo.getId());

	            ps.executeUpdate();



		    } catch (SQLException e) {

		        throw new SQLException("[AccommodationDao] bachInsertKakaoGeocoder() 실패 accommodation_id=" + geo.getId(), e);

		    }

	    }



		// =========================================================

		// 주제: accommodationReviewInsert 처리

		// 설명: accommodationReviewInsert 기능에 필요한 처리 로직을 수행

		// =========================================================

		public int accommodationReviewInsert(String content, Integer accommodationId) throws SQLException  {

			System.out.println("[AccommodationDao] accommodationReviewInsert() 진입");



			if(accommodationIdCheck(accommodationId).equals("미존재")) {

				System.out.println("[AccommodationDao] accommodationReviewInsert accommodation_id로 없음");

				throw new SQLException("[AccommodationDao] accommodationReviewInsert() 숙박id 존재하지않음");

			}



			String sql ="insert into accommodation_review (content, accommodation_id ) values(?, ?)";



			try(Connection con = DBCPUtil.getConnection();

				PreparedStatement ps = con.prepareStatement(sql)){

				ps.setString(1,content);

				ps.setInt(2,accommodationId);



				return ps.executeUpdate();//ps.executeUpdate() 실행 시 조건에 맞는 행이 없거나 변경된 데이터가 없으면 예외(Exception)를 던지는 것이 아니라 숫자 0을 반환합니다.



			}catch(SQLException e) {

				e.printStackTrace();

				throw new SQLException("",e);

			}

		}



		// =========================================================

		// 주제: accommodationIdCheck 처리

		// 설명: accommodationIdCheck 기능에 필요한 처리 로직을 수행

		// =========================================================

		public String accommodationIdCheck(Integer accommodationId) throws SQLException{

			String sql ="select accommodation_id from accommodation where accommodation_id= ?";



			try(Connection con = DBCPUtil.getConnection();

				PreparedStatement ps = con.prepareStatement(sql)){

				ps.setInt(1,accommodationId);



				ResultSet rs =ps.executeQuery();



				if(rs.next()) {

					return rs.getInt("accommodation_id") > 0 ? "존재" : "미존재" ;

				}



			}catch(SQLException e) {

				e.printStackTrace();

				throw new SQLException("[AccommodationDao] accommodationIdCheck() 에러 ",e);

			}

			return "존재";

		}





		// =========================================================

		// 주제: findReviewByAccommodationId 처리

		// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환

		// =========================================================

		public AccommodationReviewVo findReviewByAccommodationId(Integer accommodationId) throws SQLException {



			if(accommodationId==null) {

				throw new RuntimeException("[AccommodationDao] findReviewByAccommodationId() 숙박id 파라미터 null 로 전달됨");

			}

			String sql ="select * from accommodation_review where accommodation_id= ?";



			AccommodationReviewVo reviewVO = new AccommodationReviewVo();

			try(Connection con = DBCPUtil.getConnection();

				PreparedStatement ps = con.prepareStatement(sql)){

				ps.setInt(1,accommodationId);



				ResultSet rs =ps.executeQuery();



				if(rs.next()) {

					reviewVO.setAccommodation_id(rs.getInt("accommodation_id"));

					reviewVO.setReview_id(rs.getInt("review_id"));

					reviewVO.setContent(rs.getString("content"));

					reviewVO.setCreated_at(rs.getObject("created_at", LocalDateTime.class));

				}	



			}catch(SQLException e) {

				e.printStackTrace();

				throw new SQLException("[AccommodationDao] findReviewByAccommodationId() 에러 ",e);

			}		

			return reviewVO;



		}



		// =========================================================

		// 주제: st_distance_sphere 처리

		// 설명: st_distance_sphere 기능에 필요한 처리 로직을 수행

		// =========================================================

		public List<AccommodationVo> st_distance_sphere(Double lon, Double lat, Integer accommodation_id, int distance) throws SQLException{

			System.out.println("[AccommodationDao] st_distance_sphere() 파라미터 값 확인 lon : "+lon+ ", lat : "+lat +" , accommodation_id : "+accommodation_id+" , distance : "+distance);

			 // 필수값 확인: 없으면 등록하면 안 되는 값

		    if (lon == null || lat == null || accommodation_id ==null || distance == 0 || distance < 0) {

		    	System.out.println("[AccommodationDao] st_distance_sphere() 파라미터 값 문제");

		        throw new IllegalArgumentException("[AccommodationDao] st_distance_sphere() 파라미터 값 문제");

		    }



			String sql = "SELECT accommodation_id, name, addr, type, tel, placeid, lat, lon, picpath, "

			           + " ST_Distance_Sphere(POINT(lon, lat), POINT(?, ?)) AS distance_m "

				       + " FROM accommodation "

				       + " WHERE (? IS NULL OR accommodation_id <> ?) "   // id 있을 때만 제외

			           + " HAVING distance_m > 10 AND distance_m <= ? "   // 10m 이내(같은 건물)도 제외

//검색결과에 자신 숙소 보여서 위의 것 2줄로 변경			          

//				       + " WHERE accommodation_id <> ? "  //선택한 호텔 자신은 제외

//				       + " HAVING distance_m <= ? "   // 반경 5km 이내

				       + " ORDER BY distance_m " //가까운 순

				       + " LIMIT 8"; 



			List<AccommodationVo> volist = new ArrayList<AccommodationVo>();

			try(Connection con = DBCPUtil.getConnection();

				PreparedStatement ps = con.prepareStatement(sql)){

				ps.setDouble(1, lon);   // 선택한 호텔 경도

				ps.setDouble(2, lat);   // 선택한 호텔 위도

//				ps.setInt(3, accommodation_id);     // 선택한 호텔 id

//				ps.setInt(4, distance);



				ps.setObject(3, accommodation_id);   // null이면 null로 들어감

				ps.setObject(4, accommodation_id);

				ps.setInt(5, distance);



				ResultSet rs =ps.executeQuery();



				while(rs.next()) {



					AccommodationVo vo = new AccommodationVo();

					vo.setAccommodation_id(rs.getInt("accommodation_id"));

					vo.setAddr(rs.getString("addr"));

					vo.setLon(rs.getDouble("lon"));

					vo.setLat(rs.getDouble("lat"));

					vo.setName(rs.getString("name"));

					vo.setType(rs.getString("type"));

					vo.setPlaceid(rs.getString("placeid"));

					vo.setTel(rs.getString("tel"));

					vo.setPicpath(rs.getString("picpath"));



					System.out.println("[AccommodationDao] st_distance_sphere() VO 확인 : "+ vo);

					volist.add(vo);

				}	



			}catch(SQLException e) {

				e.printStackTrace();

				throw new SQLException("[AccommodationDao] st_distance_sphere() 에러 ",e);

			}		

			return volist;



		}//st_distance_sphere



		// =========================================================

		// 주제: findAccommodationByPlaceid 처리

		// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환

		// =========================================================

		public AccommodationVo findAccommodationByPlaceid(String placeid) throws SQLException {

			if(placeid==null) {

				throw new RuntimeException("[AccommodationDao] findAccommodationByPlaceid() 검색시 카카오id 파라미터 null 로 전달됨");

			}

			String sql ="select * from accommodation where placeid= ?";



			AccommodationVo accommodationVo = new AccommodationVo();



			try(Connection con = DBCPUtil.getConnection();

				PreparedStatement ps = con.prepareStatement(sql)){

				ps.setString(1,placeid);



				ResultSet rs =ps.executeQuery();



				if(rs.next()) {

					accommodationVo.setAccommodation_id(rs.getInt("accommodation_id"));

					accommodationVo.setAddr(rs.getString("addr"));

					accommodationVo.setLat(rs.getDouble("lat"));

					accommodationVo.setLon(rs.getDouble("lon"));

					accommodationVo.setName(rs.getString("name"));

					accommodationVo.setPlaceid(rs.getString("placeid"));

					accommodationVo.setTel(rs.getString("tel"));

					accommodationVo.setType(rs.getString("type"));

					accommodationVo.setPicpath(rs.getString("picpath"));

					accommodationVo.setLink(rs.getString("link"));

					accommodationVo.setContentid(rs.getString("contentid"));

				}	



			}catch(SQLException e) {

				e.printStackTrace();

				throw new SQLException("[AccommodationDao] findAccommodationByPlaceid() 에러 ",e);

			}		

			return accommodationVo;

		}





		// =========================================================

		// 주제: searchAccommodationId_registerAccommodation 처리

		// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환

		// =========================================================

		public int searchAccommodationId_registerAccommodation(String placeid) throws SQLException {

			if (placeid == null) {

				throw new RuntimeException(

						"[AccommodationDao] findAccommodationIdByPlaceid() 검색시 카카오id 파라미터 null 로 전달됨");

			}

			String sql = "select accommodation_id from accommodation where placeid= ?";



			int accommodationId = 0;



			try (Connection con = DBCPUtil.getConnection(); 

				PreparedStatement ps = con.prepareStatement(sql)) {



				ps.setString(1, placeid);



				ResultSet rs = ps.executeQuery();



				if (rs.next()) {

					accommodationId = rs.getInt("accommodation_id");

				}



			} catch (SQLException e) {

				e.printStackTrace();

				throw new SQLException("[AccommodationDao] findAccommodationIdByPlaceid() 에러 ", e);

			}

			return accommodationId;

		}



		// =========================================================

		// 주제: findAccommdationByAccommdationId 처리

		// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환

		// =========================================================

		public AccommodationVo findAccommdationByAccommdationId(Integer accommodation_id) throws SQLException{



			if(accommodation_id==null) {

				throw new RuntimeException("[AccommodationDao] findAccommdationByAccommdationId() accommodation_id 파라미터 비었음 ");

			}

			String sql ="select * from accommodation where accommodation_id= ?";



			AccommodationVo accommodationVo = new AccommodationVo();



			try(Connection con = DBCPUtil.getConnection();

				PreparedStatement ps = con.prepareStatement(sql)){

				ps.setInt(1,accommodation_id);

				//ps.setObject(1, accommdation_id, java.sql.Types.Integer)



				ResultSet rs =ps.executeQuery();



				if(rs.next()) {

					accommodationVo.setAccommodation_id(rs.getInt("accommodation_id"));

					accommodationVo.setAddr(rs.getString("addr"));

					accommodationVo.setLat(rs.getDouble("lat"));

					accommodationVo.setLon(rs.getDouble("lon"));

					accommodationVo.setName(rs.getString("name"));

					accommodationVo.setPlaceid(rs.getString("placeid"));

					accommodationVo.setTel(rs.getString("tel"));

					accommodationVo.setType(rs.getString("type"));

					accommodationVo.setPicpath(rs.getString("picpath"));

					accommodationVo.setLink(rs.getString("link"));

					accommodationVo.setContentid(rs.getString("contentid"));

				}	



			}catch(SQLException e) {

				e.printStackTrace();

				throw new SQLException("[AccommodationDao] findAccommdationByAccommdationId() 예외 ",e);

			}		

			return accommodationVo;



		}



		// =========================================================

		// 주제: insertLike 처리

		// 설명: 입력받은 데이터를 생성하거나 저장

		// =========================================================

		public int insertLike(String loginId, int accommodationId) throws SQLException{



			// insertLike : 이미 있으면 무시 (중복 키 오류 대신 결과 0)

			String query = "insert ignore into accommodation_like (member_id, accommodation_id) values (?, ?)";



			int result = 0;



			try(Connection con = DBCPUtil.getConnection();

				PreparedStatement pstmt = con.prepareStatement(query)){

				pstmt.setString(1, loginId);

				pstmt.setInt(2, accommodationId);



				result =pstmt.executeUpdate();



			}catch(Exception e) {

				System.out.println("[AccommodationDao] insertLike() 예외발생");

				throw new SQLException("insertLike 예외발생 "+ e);

			}

			return result;		



		}





		// =========================================================

		// 주제: deleteLike 처리

		// 설명: 대상 데이터를 삭제하거나 제거

		// =========================================================

		public int deleteLike(String loginId, int accommodationId) throws SQLException{

			//			-- member_id accommodation_id

			String query ="delete from accommodation_like where member_id = ? and accommodation_id = ?";



			int result = 0;



			try(Connection con = DBCPUtil.getConnection();

				PreparedStatement pstmt = con.prepareStatement(query)){

				pstmt.setString(1, loginId);

				pstmt.setInt(2, accommodationId);



				result =pstmt.executeUpdate();



			}catch(Exception e) {

				System.out.println("[AccommodationDao] deleteLike() 예외발생");

				throw new SQLException("deleteLike 예외발생 "+ e);

			}

			return result;		

		}







		// 이 회원이 이 숙소를 좋아요 했는지

		//Accommodation/openrouter_summary_search 서블릿경로에서 사용

		public boolean isAccommodationLiked(String memberId, int accommodationId) throws SQLException {

			if (memberId == null) return false;   // 로그인 안 했으면 무조건 false



			String sql = "SELECT 1 FROM accommodation_like WHERE member_id = ? AND accommodation_id = ?";

			try (Connection con = DBCPUtil.getConnection();

			     PreparedStatement ps = con.prepareStatement(sql)) {

				ps.setString(1, memberId);

				ps.setInt(2, accommodationId);

				try (ResultSet rs = ps.executeQuery()) {

					return rs.next();   // 한 줄이라도 있으면 true

				}

			}catch(SQLException e) {

				throw new SQLException("[AccommodationDao] isAccommodationLiked() 예외발생"+e);

			}
		}

		// ==========================================

		// 메인 화면 숙박 4개 조회

		// 사진과 1박 가격 제외

		// ==========================================

		public List<AccommodationVo> getMainAccommodationList(

		        String loginId) throws SQLException {



		    List<AccommodationVo> list = new ArrayList<>();



		    String sql =

		        "SELECT a.accommodation_id, a.name, a.addr, " +

		        "a.type, a.avg_rating, a.review_count, " +

		        "(l.member_id IS NOT NULL) AS isliked " +

		        "FROM accommodation a " +

		        "LEFT JOIN accommodation_like l " +

		        "ON a.accommodation_id = l.accommodation_id " +

		        "AND l.member_id = ? " +

		        "ORDER BY a.accommodation_id ASC " +

		        "LIMIT 4";



		    try (Connection con = DBCPUtil.getConnection();

		         PreparedStatement pstmt = con.prepareStatement(sql)) {



		        pstmt.setString(1, loginId);



		        try (ResultSet rs = pstmt.executeQuery()) {



		            while (rs.next()) {



		                AccommodationVo vo = new AccommodationVo();



		                vo.setAccommodation_id(

		                    rs.getInt("accommodation_id")

		                );



		                vo.setName(rs.getString("name"));

		                vo.setAddr(rs.getString("addr"));

		                vo.setType(rs.getString("type"));



		                double rating = rs.getDouble("avg_rating");

		                vo.setAvg_rating(

		                    rs.wasNull() ? null : rating

		                );



		                int reviewCount = rs.getInt("review_count");

		                vo.setReview_count(

		                    rs.wasNull() ? null : reviewCount

		                );



		                vo.setIsliked(rs.getBoolean("isliked"));



		                list.add(vo);

		            }

		        }

		    }



		    return list;

		}

}
