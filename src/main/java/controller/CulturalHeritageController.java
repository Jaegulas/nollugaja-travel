package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import config.AppConfigListener;
import dao.CulLikeDao;
import dao.CulturalHeritageDao;
import vo.CulturalHeritageVo;

// =========================================================
// 주제: 파일 역할
// 설명: CulturalHeritageController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================



@WebServlet("/Cultural")
public class CulturalHeritageController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/*
	 * 여기는 현재 네가 사용하고 있는 인증키를 그대로 사용해. 실제 인증키는 GitHub에 올리지 않는 것을 권장.
	 */
	

	private static final String API_BASE_URL = "https://apis.data.go.kr/B551011/KorService2";

	// 화면 한 페이지당 12개
	private static final int NUM_OF_ROWS = 12;

	// 좋아요순 전체조회 때 한 번에 요청할 개수
	private static final int BULK_ROWS = 1000;

	private static final String CONTENT_TYPE_ID = "12";

	private static final String LCLS_SYSTM_1 = "HS";

	// =========================================================
	// GET
	// =========================================================

	@Override
	// =========================================================
	// 주제: doGet 처리
	// 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
	// =========================================================
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		// =====================================================
		// 상세보기 AJAX
		// =====================================================

		String action = request.getParameter("action");

		if ("detail".equals(action)) {

			handleDetailRequest(request, response);

			return;
		}

		// =====================================================
		// 검색어
		// =====================================================

		String keyword = request.getParameter("keyword");

		if (keyword != null) {

			keyword = keyword.trim();
		}

		// =====================================================
		// 지역
		// =====================================================

		String region = request.getParameter("region");

		if (region == null) {

			region = "";
		}

		region = region.trim();

		// =====================================================
		// 페이지
		// =====================================================

		int pageNo = parsePageNo(request.getParameter("pageNo"));

		// =====================================================
		// ★ 정렬
		// =====================================================

		String sort = request.getParameter("sort");

		if (sort == null || sort.trim().isEmpty()) {

			sort = "default";
		}

		sort = sort.trim();

		// 허용된 정렬값만 사용
		if (!sort.equals("default") && !sort.equals("name") && !sort.equals("liked")) {

			sort = "default";
		}

		System.out.println();
		System.out.println("==========================================");

		System.out.println("[Cultural] 현재 정렬 : " + sort);

		System.out.println("[Cultural] 현재 페이지 : " + pageNo);

		System.out.println("[Cultural] 현재 지역 : " + region);

		System.out.println("[Cultural] 검색어 : " + keyword);

		System.out.println("==========================================");

		// =====================================================
		// ★ 로그인 회원 좋아요 전체 조회
		// =====================================================

		List<Integer> likedHeritageIds = new ArrayList<>();

		HttpSession session = request.getSession(false);

		if (session != null && session.getAttribute("loginId") != null) {

			String memberId = String.valueOf(session.getAttribute("loginId"));

			try {

				CulLikeDao dao = new CulLikeDao();

				likedHeritageIds = dao.getLikedHeritageIds(memberId);

				System.out.println("[좋아요 조회] memberId : " + memberId);

				System.out.println("[좋아요 조회] heritageId : " + likedHeritageIds);

			} catch (Exception e) {

				System.err.println("[Cultural] 좋아요 조회 오류");

				e.printStackTrace();
			}
		}


        // =====================================================
        // 문화유산 목록 DB 조건 조회
        // =====================================================

        List<CulturalHeritageVo> dbPageList = new ArrayList<>();
        int totalCount = 0;

        try {
            CulturalHeritageDao heritageDao =
                new CulturalHeritageDao();

            String memberId =
                session != null && session.getAttribute("loginId") != null
                ? String.valueOf(session.getAttribute("loginId"))
                : null;

            // 검색 + 지역 조건에 해당하는 전체 개수
            totalCount = heritageDao.countFiltered(keyword, region);

            // 페이지 번호 보정
            int totalPagesForQuery =
                (int) Math.ceil(totalCount / (double) NUM_OF_ROWS);

            if (totalPagesForQuery > 0
                    && pageNo > totalPagesForQuery) {
                pageNo = totalPagesForQuery;
            }

            // DB에서 검색 + 지역 + 정렬 + 페이지 조회
            dbPageList = heritageDao.findFilteredPage(
                keyword, region, sort, pageNo, NUM_OF_ROWS, memberId
            );

            // 화면에 표시할 정보 정리
            for (CulturalHeritageVo vo : dbPageList) {

                vo.setTitle(cleanHeritageTitle(vo.getTitle()));

                vo.setUseTime(onlyTime(vo.getUseTime()));

                vo.setInfoCenter(onlyPhone(vo.getInfoCenter()));
            }

            System.out.println("[Cultural] DB 조건 조회 성공");
            System.out.println("[Cultural] 전체 결과 : " + totalCount);
            System.out.println("[Cultural] 현재 목록 : " + dbPageList.size());

        } catch (Exception e) {

            System.err.println("[Cultural] DB 조건 조회 실패");
            e.printStackTrace();

            request.setAttribute(
                "dbError", "문화유산을 불러오지 못했습니다."
            );
        }

		

		// =====================================================
		// 페이지네이션
		// =====================================================

        int totalPages = (int) Math.ceil(totalCount / (double) NUM_OF_ROWS);

		int pageGroupSize = 5;

		int startPage = ((pageNo - 1) / pageGroupSize) * pageGroupSize + 1;

		int endPage = Math.min(startPage + pageGroupSize - 1, totalPages);

		// =====================================================
		// JSP 전달
		// =====================================================

		request.setAttribute("heritageList", dbPageList);

		request.setAttribute("totalCount", totalCount);

		request.setAttribute("likedHeritageIds", likedHeritageIds);

		// 검색어
		String keywordForJsp = keyword == null ? "" : keyword;

		request.setAttribute("keyword", keywordForJsp);

		request.setAttribute("encodedKeyword", URLEncoder.encode(keywordForJsp, StandardCharsets.UTF_8));

		// 지역
		request.setAttribute("region", region);

		// ★ 정렬
		request.setAttribute("sort", sort);

		// 페이지
		request.setAttribute("pageNo", pageNo);

		request.setAttribute("totalPages", totalPages);

		request.setAttribute("startPage", startPage);

		request.setAttribute("endPage", endPage);

		// =====================================================
		// JSP
		// =====================================================

		request.getRequestDispatcher("/WEB-INF/cultural/cultural.jsp").forward(request, response);
	}

	// =========================================================
	// POST
	// =========================================================

	@Override
	// =========================================================
	// 주제: doPost 처리
	// 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
	// =========================================================
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

	// =========================================================
	// 주제: getNameSortedResult 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	private TourApiResult getNameSortedResult(String keyword, String region, int pageNo) throws Exception {

		/*
		 * 검색이 없는 경우
		 */
		if (keyword == null || keyword.isEmpty()) {

			return getAreaHeritageList(pageNo, region, "A");
		}

		/*
		 * 검색이 있는 경우
		 */
		return searchHeritageList(keyword, pageNo, region, "A");
	}

	// =========================================================
	// 주제: getLikedSortedResult 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	private TourApiResult getLikedSortedResult(String keyword, String region, int pageNo,
			List<Integer> likedHeritageIds) throws Exception {

		// -----------------------------------------------------
		// 1. 현재 조건에 해당하는 전체 문화유산 조회
		// -----------------------------------------------------

		TourApiResult allResult;

		if (keyword == null || keyword.isEmpty()) {

			allResult = getAllAreaHeritageList(region);

		} else {

			allResult = getAllSearchHeritageList(keyword, region);
		}

		// -----------------------------------------------------
		// 2. DB 좋아요 ID → Set
		// -----------------------------------------------------

		Set<String> likedSet = new HashSet<>();

		for (Integer id : likedHeritageIds) {

			if (id != null) {

				likedSet.add(String.valueOf(id));
			}
		}

		// -----------------------------------------------------
		// 3. ★ 좋아요한 문화유산 우선 정렬
		//
		// 좋아요 O → 위
		// 좋아요 X → 아래
		//
		// 같은 그룹에서는 가나다순
		// -----------------------------------------------------

		allResult.items.sort(new Comparator<CulturalHeritageVo>() {

			@Override
			// =========================================================
			// 주제: compare 처리
			// 설명: compare 기능에 필요한 처리 로직을 수행
			// =========================================================
			public int compare(CulturalHeritageVo a, CulturalHeritageVo b) {

				boolean likedA = likedSet.contains(a.getContentId());

				boolean likedB = likedSet.contains(b.getContentId());

				// A만 좋아요
				if (likedA && !likedB) {

					return -1;
				}

				// B만 좋아요
				if (!likedA && likedB) {

					return 1;
				}

				// 둘 다 같은 상태라면 가나다순
				String titleA = a.getTitle() == null ? "" : a.getTitle();

				String titleB = b.getTitle() == null ? "" : b.getTitle();

				return titleA.compareTo(titleB);
			}
		});

		// -----------------------------------------------------
		// 4. ★ 정렬이 끝난 다음 페이지를 자른다.
		// -----------------------------------------------------

		return paginate(allResult.items, pageNo);
	}

	// =========================================================
	// 주제: getAreaHeritageList 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	private TourApiResult getAreaHeritageList(int pageNo, String region, String arrange) throws Exception {

		// 전체
		if (region == null || region.isEmpty()) {

			return callAreaApi("", pageNo, NUM_OF_ROWS, arrange);
		}

		String[] areaCodes = getAreaCodes(region);

		if (areaCodes.length == 0) {

			return callAreaApi("", pageNo, NUM_OF_ROWS, arrange);
		}

		// 단일 지역
		if (areaCodes.length == 1) {

			return callAreaApi(areaCodes[0], pageNo, NUM_OF_ROWS, arrange);
		}

		// 묶음 지역
		return getCombinedAreaList(areaCodes, pageNo, arrange);
	}

	// =========================================================
	// 주제: callAreaApi 처리
	// 설명: callAreaApi 기능에 필요한 처리 로직을 수행
	// =========================================================
	private TourApiResult callAreaApi(String areaCode, int pageNo, int numOfRows, String arrange) throws Exception {

		String encodedServiceKey = encodeServiceKey();

		String apiUrl = API_BASE_URL + "/areaBasedList2" + "?serviceKey=" + encodedServiceKey + "&numOfRows="
				+ numOfRows + "&pageNo=" + pageNo + "&MobileOS=ETC" + "&MobileApp=Nolleogaja" + "&_type=json"
				+ "&arrange=" + arrange + "&contentTypeId=" + CONTENT_TYPE_ID + "&lclsSystm1=" + LCLS_SYSTM_1;

		if (areaCode != null && !areaCode.isEmpty()) {

			apiUrl += "&areaCode=" + URLEncoder.encode(areaCode, StandardCharsets.UTF_8);
		}

		System.out.println("[TourAPI] areaBasedList2");

		System.out.println("[TourAPI] areaCode : " + areaCode);

		System.out.println("[TourAPI] arrange : " + arrange);

		return callTourApi(apiUrl);
	}

	// =========================================================
	// 주제: getCombinedAreaList 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	private TourApiResult getCombinedAreaList(String[] areaCodes, int pageNo, String arrange) throws Exception {

		List<CulturalHeritageVo> allItems = new ArrayList<>();

		int totalCount = 0;

		/*
		 * 가나다순일 경우 묶음 지역 전체를 합친 후 다시 제목순 정렬해야 정확하다.
		 */
		if ("A".equals(arrange)) {

			for (String areaCode : areaCodes) {

				TourApiResult result = getAllAreaByCode(areaCode, arrange);

				totalCount += result.totalCount;

				allItems.addAll(result.items);
			}

			allItems.sort(Comparator.comparing(CulturalHeritageVo::getTitle, Comparator.nullsLast(String::compareTo)));

			TourApiResult result = paginate(allItems, pageNo);

			result.totalCount = totalCount;

			return result;
		}

		/*
		 * 기본순은 기존 방식 유지
		 */
		int requiredCount = pageNo * NUM_OF_ROWS;

		for (String areaCode : areaCodes) {

			TourApiResult result = callAreaApi(areaCode, 1, requiredCount, arrange);

			totalCount += result.totalCount;

			allItems.addAll(result.items);
		}

		int startIndex = (pageNo - 1) * NUM_OF_ROWS;

		int endIndex = Math.min(startIndex + NUM_OF_ROWS, allItems.size());

		List<CulturalHeritageVo> pageItems = new ArrayList<>();

		if (startIndex < allItems.size()) {

			pageItems.addAll(allItems.subList(startIndex, endIndex));
		}

		TourApiResult result = new TourApiResult();

		result.items = pageItems;

		result.totalCount = totalCount;

		return result;
	}

	// =========================================================
	// 주제: getAllAreaHeritageList 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	private TourApiResult getAllAreaHeritageList(String region) throws Exception {

		List<CulturalHeritageVo> allItems = new ArrayList<>();

		int totalCount = 0;

		String[] areaCodes = getAreaCodes(region);

		// -----------------------------------------------------
		// 전체 지역
		// -----------------------------------------------------

		if (region == null || region.isEmpty() || areaCodes.length == 0) {

			TourApiResult result = getAllAreaByCode("", "A");

			return result;
		}

		// -----------------------------------------------------
		// 선택 지역
		// -----------------------------------------------------

		for (String areaCode : areaCodes) {

			TourApiResult result = getAllAreaByCode(areaCode, "A");

			totalCount += result.totalCount;

			allItems.addAll(result.items);
		}

		TourApiResult result = new TourApiResult();

		result.items = allItems;

		result.totalCount = totalCount;

		return result;
	}

	// =========================================================
	// 주제: getAllAreaByCode 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	private TourApiResult getAllAreaByCode(String areaCode, String arrange) throws Exception {

		TourApiResult first = callAreaApi(areaCode, 1, BULK_ROWS, arrange);

		List<CulturalHeritageVo> allItems = new ArrayList<>(first.items);

		int totalCount = first.totalCount;

		int totalApiPages = (int) Math.ceil(totalCount / (double) BULK_ROWS);

		for (int p = 2; p <= totalApiPages; p++) {

			TourApiResult next = callAreaApi(areaCode, p, BULK_ROWS, arrange);

			allItems.addAll(next.items);
		}

		TourApiResult result = new TourApiResult();

		result.items = allItems;

		result.totalCount = totalCount;

		return result;
	}

	// =========================================================
	// 주제: searchHeritageList 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	private TourApiResult searchHeritageList(String keyword, int pageNo, String region, String arrange)
			throws Exception {

		return callSearchApi(keyword, pageNo, NUM_OF_ROWS, region, arrange);
	}

	// =========================================================
	// 주제: callSearchApi 처리
	// 설명: callSearchApi 기능에 필요한 처리 로직을 수행
	// =========================================================
	private TourApiResult callSearchApi(String keyword, int pageNo, int numOfRows, String region, String arrange)
			throws Exception {

		String encodedServiceKey = encodeServiceKey();

		String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

		String apiUrl = API_BASE_URL + "/searchKeyword2" + "?serviceKey=" + encodedServiceKey + "&numOfRows="
				+ numOfRows + "&pageNo=" + pageNo + "&MobileOS=ETC" + "&MobileApp=Nolleogaja" + "&_type=json"
				+ "&arrange=" + arrange + "&keyword=" + encodedKeyword + "&contentTypeId=" + CONTENT_TYPE_ID
				+ "&lclsSystm1=" + LCLS_SYSTM_1;

		String[] areaCodes = getAreaCodes(region);

		/*
		 * 현재 네 기존 코드와 동일하게 단일 지역 검색일 때 areaCode 적용
		 */
		if (areaCodes.length == 1) {

			apiUrl += "&areaCode=" + URLEncoder.encode(areaCodes[0], StandardCharsets.UTF_8);
		}

		return callTourApi(apiUrl);
	}

	// =========================================================
	// 주제: getAllSearchHeritageList 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	private TourApiResult getAllSearchHeritageList(String keyword, String region) throws Exception {

		TourApiResult first = callSearchApi(keyword, 1, BULK_ROWS, region, "A");

		List<CulturalHeritageVo> allItems = new ArrayList<>(first.items);

		int totalCount = first.totalCount;

		int totalApiPages = (int) Math.ceil(totalCount / (double) BULK_ROWS);

		for (int p = 2; p <= totalApiPages; p++) {

			TourApiResult next = callSearchApi(keyword, p, BULK_ROWS, region, "A");

			allItems.addAll(next.items);
		}

		TourApiResult result = new TourApiResult();

		result.items = allItems;

		result.totalCount = totalCount;

		return result;
	}

	// =========================================================
	// 주제: paginate 처리
	// 설명: paginate 기능에 필요한 처리 로직을 수행
	// =========================================================
	private TourApiResult paginate(List<CulturalHeritageVo> allItems, int pageNo) {

		TourApiResult result = new TourApiResult();

		result.totalCount = allItems.size();

		int startIndex = (pageNo - 1) * NUM_OF_ROWS;

		if (startIndex >= allItems.size()) {

			result.items = new ArrayList<>();

			return result;
		}

		int endIndex = Math.min(startIndex + NUM_OF_ROWS, allItems.size());

		result.items = new ArrayList<>(allItems.subList(startIndex, endIndex));

		return result;
	}

	// =========================================================
	// 주제: getAreaCodes 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	private String[] getAreaCodes(String region) {

		if (region == null || region.isEmpty()) {

			return new String[0];
		}

		switch (region) {

		case "서울":

			return new String[] { "1" };

		case "경기인천":

			return new String[] { "31", "2" };

		case "강원":

			return new String[] { "32" };

		case "충청":

			return new String[] { "33", "34", "3", "8" };

		case "전라":

			return new String[] { "37", "38", "5" };

		case "경상":

			return new String[] { "35", "36", "4", "6", "7" };

		case "제주":

			return new String[] { "39" };

		default:

			return new String[0];
		}
	}

	// =========================================================
	// 주제: callTourApi 처리
	// 설명: callTourApi 기능에 필요한 처리 로직을 수행
	// =========================================================
	private TourApiResult callTourApi(String apiUrl) throws Exception {

		URI uri = URI.create(apiUrl);

		HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

		connection.setRequestMethod("GET");

		connection.setConnectTimeout(10000);

		connection.setReadTimeout(10000);

		int responseCode = connection.getResponseCode();

		System.out.println("[TourAPI] HTTP 응답 코드 : " + responseCode);

		BufferedReader reader;

		if (responseCode >= 200 && responseCode < 300) {

			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

		} else {

			reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
		}

		StringBuilder jsonBuilder = new StringBuilder();

		String line;

		while ((line = reader.readLine()) != null) {

			jsonBuilder.append(line);
		}

		reader.close();

		connection.disconnect();

		String json = jsonBuilder.toString();

		if (responseCode < 200 || responseCode >= 300) {

			throw new IOException("TourAPI HTTP 오류 : " + responseCode + " / " + json);
		}

		return parseTourApiJson(json);
	}

	// =========================================================
	// 주제: encodeServiceKey 처리
	// 설명: encodeServiceKey 기능에 필요한 처리 로직을 수행
	// =========================================================
	private String encodeServiceKey() {

	    String serviceKey =
	            AppConfigListener.getProperty("CulturalHeritageController.api.key");

	    if (serviceKey == null || serviceKey.isBlank()) {
	        throw new IllegalStateException(
	                "CulturalHeritageController.api.key가 config.properties에 없습니다."
	        );
	    }

	    String cleanServiceKey =
	            serviceKey.replace("\r", "").replace("\n", "").trim();

	    return URLEncoder.encode(
	            cleanServiceKey,
	            StandardCharsets.UTF_8
	    );
	}

	// =========================================================
	// 주제: parseTourApiJson 처리
	// 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공
	// =========================================================
	private TourApiResult parseTourApiJson(String json) throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		JsonNode root = mapper.readTree(json);

		JsonNode header = root.path("response").path("header");

		String resultCode = header.path("resultCode").asText();

		String resultMsg = header.path("resultMsg").asText();

		if (!"0000".equals(resultCode)) {

			throw new IOException("TourAPI 오류 : " + resultCode + " / " + resultMsg);
		}

		JsonNode body = root.path("response").path("body");

		TourApiResult result = new TourApiResult();

		result.totalCount = body.path("totalCount").asInt(0);

		result.items = new ArrayList<>();

		JsonNode itemNode = body.path("items").path("item");

		if (itemNode.isArray()) {

			for (JsonNode item : itemNode) {

				result.items.add(createHeritageVo(item));
			}

		} else if (itemNode.isObject()) {

			result.items.add(createHeritageVo(itemNode));
		}

		System.out.println("[TourAPI] 전체 데이터 수 : " + result.totalCount);

		System.out.println("[TourAPI] 현재 받은 카드 수 : " + result.items.size());

		return result;
	}
	
	// =========================================================
	// 문화유산 제목 정리
	// (제주), [유네스코 세계유산] 같은 부가정보 제거
	// =========================================================

// =========================================================
// 주제: cleanHeritageTitle 처리
// 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공
// =========================================================
private String cleanHeritageTitle(String title) {
    if (title == null) {
        return "";
    }

    return title
        .replaceAll("\\([^)]*\\)", "")
        .replaceAll("\\[[^]]*\\]", "")
        .replaceAll("\\s+", " ")
        .trim();
}

// =========================================================
// 주제: onlyTime 처리
// 설명: onlyTime 기능에 필요한 처리 로직을 수행
// =========================================================
private String onlyTime(String value) {
    if (value == null || value.isBlank()) {
        return "정보 없음";
    }

    String text = value
        .replaceAll("(?i)<br\\s*/?>", " ")
        .replaceAll("<[^>]*>", " ");

    java.util.regex.Matcher matcher =
        java.util.regex.Pattern.compile(
            "(?<!\\d)(?:[01]?\\d|2[0-3]):[0-5]\\d(?!\\d)"
        ).matcher(text);

    if (!matcher.find()) {
        return "정보 없음";
    }

    String start = matcher.group();

    if (matcher.find()) {
        return start + "~" + matcher.group();
    }

    return start;
}

// =========================================================
// 주제: onlyPhone 처리
// 설명: onlyPhone 기능에 필요한 처리 로직을 수행
// =========================================================
private String onlyPhone(String value) {
    if (value == null || value.isBlank()) {
        return "정보 없음";
    }

    java.util.regex.Matcher matcher =
        java.util.regex.Pattern.compile(
            "(?<!\\d)(?:0\\d{1,2}[-\\s]?\\d{3,4}[-\\s]?\\d{4}|1\\d{3}[-\\s]?\\d{4})(?!\\d)"
        ).matcher(value.replaceAll("<[^>]*>", " "));

    if (matcher.find()) {
        return matcher.group().replaceAll("\\s+", "-");
    }

    return "정보 없음";
}


	// =========================================================
	// 주제: createHeritageVo 처리
	// 설명: 입력받은 데이터를 생성하거나 저장
	// =========================================================
	private CulturalHeritageVo createHeritageVo(JsonNode item) {

	    CulturalHeritageVo vo = new CulturalHeritageVo();

	    vo.setContentId(item.path("contentid").asText());
	    vo.setTitle(item.path("title").asText());
	    vo.setAddr1(item.path("addr1").asText());
	    vo.setFirstImage(item.path("firstimage").asText());
	    vo.setMapX(item.path("mapx").asText());
	    vo.setMapY(item.path("mapy").asText());

	    vo.setAreaCode(item.path("areacode").asText());
	    vo.setSigunguCode(item.path("sigungucode").asText());
	    vo.setContentTypeId(item.path("contenttypeid").asText());

	    // ========================================
	    // TourAPI 지역 코드 확인용 로그
	    // ========================================
	    System.out.println("----------------------------------");
	    System.out.println("[TourAPI 데이터 확인]");
	    System.out.println("contentId : " + vo.getContentId());
	    System.out.println("title : " + vo.getTitle());
	    System.out.println("areaCode : [" + vo.getAreaCode() + "]");
	    System.out.println("sigunguCode : [" + vo.getSigunguCode() + "]");
	    System.out.println("contentTypeId : [" + vo.getContentTypeId() + "]");
	    System.out.println("----------------------------------");

	    return vo;
	}

	// =========================================================
	// ★ 문화유산 이용정보 조회
	// TourAPI detailIntro2
	//
	// 한 번의 API 호출로
	// 운영시간 / 휴무일 / 주차 / 문의정보를 모두 가져온다.
	// =========================================================

	// =========================================================
	// 주제: getHeritageIntroInfo 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	private HeritageIntroInfo getHeritageIntroInfo(String contentId) throws Exception {

		String encodedServiceKey = encodeServiceKey();

		String apiUrl = API_BASE_URL + "/detailIntro2" + "?serviceKey=" + encodedServiceKey + "&MobileOS=ETC"
				+ "&MobileApp=Nolleogaja" + "&_type=json" + "&contentId="
				+ URLEncoder.encode(contentId, StandardCharsets.UTF_8) + "&contentTypeId=" + CONTENT_TYPE_ID;

		System.out.println("[TourAPI 이용정보] detailIntro2 요청");

		System.out.println("[TourAPI 이용정보] contentId : " + contentId);

		URI uri = URI.create(apiUrl);

		HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

		connection.setRequestMethod("GET");

		connection.setConnectTimeout(10000);

		connection.setReadTimeout(10000);

		int responseCode = connection.getResponseCode();

		System.out.println("[TourAPI 이용정보] HTTP 응답 코드 : " + responseCode);

		BufferedReader reader;

		if (responseCode >= 200 && responseCode < 300) {

			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

		} else {

			reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
		}

		StringBuilder jsonBuilder = new StringBuilder();

		String line;

		while ((line = reader.readLine()) != null) {

			jsonBuilder.append(line);
		}

		reader.close();

		connection.disconnect();

		String json = jsonBuilder.toString();

		if (responseCode < 200 || responseCode >= 300) {

			throw new IOException("TourAPI 이용정보 HTTP 오류 : " + responseCode);
		}

		ObjectMapper mapper = new ObjectMapper();

		JsonNode root = mapper.readTree(json);

		// =====================================================
		// API 결과 확인
		// =====================================================

		JsonNode header = root.path("response").path("header");

		String resultCode = header.path("resultCode").asText();

		String resultMsg = header.path("resultMsg").asText();

		if (!"0000".equals(resultCode)) {

			throw new IOException("TourAPI 이용정보 오류 : " + resultCode + " / " + resultMsg);
		}

		// =====================================================
		// item 찾기
		// =====================================================

		JsonNode itemNode = root.path("response").path("body").path("items").path("item");

		JsonNode item;

		if (itemNode.isArray() && itemNode.size() > 0) {

			item = itemNode.get(0);

		} else if (itemNode.isObject()) {

			item = itemNode;

		} else {

			return new HeritageIntroInfo();
		}

		// =====================================================
		// ★ 필요한 네 가지 정보 추출
		// =====================================================

		HeritageIntroInfo info = new HeritageIntroInfo();

		// -----------------------------
		// 운영시간
		// -----------------------------

		String useTime = item.path("usetime").asText("");

		useTime = cleanTourApiText(useTime);

		useTime = formatCardUseTime(useTime);

		info.useTime = useTime;

		// -----------------------------
		// 휴무일
		// -----------------------------

		String restDate = item.path("restdate").asText("");

		restDate = cleanTourApiText(restDate);

		info.restDate = restDate;

		// -----------------------------
		// 주차정보
		// -----------------------------

		String parking = item.path("parking").asText("");

		parking = cleanTourApiText(parking);

		info.parking = parking;

		// -----------------------------
		// 문의 및 안내
		// -----------------------------

		// -----------------------------
		// 문의 및 안내
		// 전화번호만 추출
		// -----------------------------

		String infoCenter = item.path("infocenter").asText("");

		infoCenter = cleanTourApiText(infoCenter);

		// 기관명 등은 제거하고 전화번호만 추출
		infoCenter = extractPhoneNumber(infoCenter);

		info.infoCenter = infoCenter;

		return info;
	}

	// =========================================================
	// 주제: formatCardUseTime 처리
	// 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공
	// =========================================================
	private String formatCardUseTime(String useTime) {

		// 운영시간 자체가 없는 경우
		if (useTime == null || useTime.isBlank()) {
			return "문의로 직접확인";
		}

		String text = useTime.trim();

		// =====================================================
		// 1. 상시개방 / 24시간
		// =====================================================

		if (text.contains("상시개방")) {
			return "상시개방";
		}

		if (text.contains("24시간")) {
			return "24시간";
		}

		// =====================================================
		// 2. 09:00~18:00 같은 시간 범위 추출
		//
		// 인식 가능:
		// 09:00~18:00
		// 09:00 ~ 18:00
		// 09:00-18:00
		// 09:00 ∼ 18:00
		// 09:00～18:00
		// =====================================================

		java.util.regex.Pattern timeRangePattern = java.util.regex.Pattern
				.compile("(\\d{1,2}:\\d{2})\\s*[~∼～-]\\s*(\\d{1,2}:\\d{2})");

		java.util.regex.Matcher matcher = timeRangePattern.matcher(text);

		if (matcher.find()) {

			return matcher.group(1) + "~" + matcher.group(2);
		}

		// =====================================================
		// 3. "18:00 이후"처럼 시간 하나만 있는 경우
		// 카드에서 애매한 정보는 긴 문장으로 출력하지 않음
		// =====================================================

		return "문의로 직접확인";
	}

	// =========================================================
	// 주제: cleanTourApiText 처리
	// 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공
	// =========================================================
	private String cleanTourApiText(String text) {

		if (text == null || text.trim().isEmpty()) {

			return "";
		}

		String result = text;

		// <br>, <br/>, <br /> → 공백
		result = result.replaceAll("(?i)<br\\s*/?>", " ");

		// 나머지 HTML 태그 제거
		result = result.replaceAll("<[^>]*>", "");

		// HTML 공백 문자
		result = result.replace("&nbsp;", " ");

		// 여러 공백을 하나로
		result = result.replaceAll("\\s+", " ");

		return result.trim();
	}

	// =========================================================
	// 주제: extractPhoneNumber 처리
	// 설명: extractPhoneNumber 기능에 필요한 처리 로직을 수행
	// =========================================================
	private String extractPhoneNumber(String text) {

		if (text == null || text.isBlank()) {
			return "";
		}

		// 예:
		// 064-760-6322
		// 061-530-5856
		// 02-1234-5678
		// 033-123-4567
		// 1588-1234
		java.util.regex.Pattern phonePattern = java.util.regex.Pattern
				.compile("(?:0\\d{1,2}-\\d{3,4}-\\d{4}|1\\d{3}-\\d{4})");

		java.util.regex.Matcher matcher = phonePattern.matcher(text);

		if (matcher.find()) {
			return matcher.group();
		}

		// 전화번호를 찾지 못한 경우
		return "";
	}

	// =========================================================
	// 주제: handleDetailRequest 처리
	// 설명: handleDetailRequest 기능에 필요한 처리 로직을 수행
	// =========================================================
	private void handleDetailRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("application/json; charset=UTF-8");

		String contentId = request.getParameter("contentId");

		if (contentId == null || contentId.trim().isEmpty()) {

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			response.getWriter().write("{\"success\":false," + "\"message\":\"contentId가 없습니다.\"}");

			return;
		}

		try {

			String overview = getHeritageOverview(contentId);

			ObjectMapper mapper = new ObjectMapper();

			Map<String, Object> result = new HashMap<>();

			result.put("success", true);

			result.put("overview", overview);

			response.getWriter().write(mapper.writeValueAsString(result));

		} catch (Exception e) {

			e.printStackTrace();

			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

			ObjectMapper mapper = new ObjectMapper();

			Map<String, Object> result = new HashMap<>();

			result.put("success", false);

			result.put("message", "문화유산 상세정보를 불러오지 못했습니다.");

			response.getWriter().write(mapper.writeValueAsString(result));
		}
	}

	// =========================================================
	// 주제: getHeritageOverview 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	private String getHeritageOverview(String contentId) throws Exception {

		String encodedServiceKey = encodeServiceKey();

		String apiUrl = API_BASE_URL + "/detailCommon2" + "?serviceKey=" + encodedServiceKey + "&MobileOS=ETC"
				+ "&MobileApp=Nolleogaja" + "&_type=json" + "&contentId="
				+ URLEncoder.encode(contentId, StandardCharsets.UTF_8);

		System.out.println("==========================================");
		System.out.println("[TourAPI 상세] detailCommon2 요청");
		System.out.println("[TourAPI 상세] contentId : " + contentId);
		System.out.println("[TourAPI 상세] URL : " + apiUrl);
		System.out.println("==========================================");

		URI uri = URI.create(apiUrl);

		HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

		connection.setRequestMethod("GET");
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);

		int responseCode = connection.getResponseCode();

		System.out.println("[TourAPI 상세] HTTP 응답 코드 : " + responseCode);

		BufferedReader reader;

		if (responseCode >= 200 && responseCode < 300) {

			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

		} else {

			reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
		}

		StringBuilder jsonBuilder = new StringBuilder();

		String line;

		while ((line = reader.readLine()) != null) {
			jsonBuilder.append(line);
		}

		reader.close();
		connection.disconnect();

		String json = jsonBuilder.toString();

		// ★ API가 실제로 무엇을 보내는지 확인
		System.out.println("[TourAPI 상세] 원본 JSON : " + json);

		if (responseCode < 200 || responseCode >= 300) {

			throw new IOException("TourAPI 상세 HTTP 오류 : " + responseCode + " / " + json);
		}

		ObjectMapper mapper = new ObjectMapper();

		JsonNode root = mapper.readTree(json);

		JsonNode header = root.path("response").path("header");

		String resultCode = header.path("resultCode").asText();

		String resultMsg = header.path("resultMsg").asText();

		System.out.println("[TourAPI 상세] resultCode : " + resultCode);

		System.out.println("[TourAPI 상세] resultMsg : " + resultMsg);

		if (!"0000".equals(resultCode)) {

			throw new IOException("TourAPI 상세 오류 : " + resultCode + " / " + resultMsg);
		}

		JsonNode itemNode = root.path("response").path("body").path("items").path("item");

		System.out.println("[TourAPI 상세] item : " + itemNode);

		JsonNode item;

		if (itemNode.isArray() && itemNode.size() > 0) {

			item = itemNode.get(0);

		} else if (itemNode.isObject()) {

			item = itemNode;

		} else {

			System.out.println("[TourAPI 상세] item 데이터 없음");

			return "등록된 문화유산 설명이 없습니다.";
		}

		String overview = item.path("overview").asText("");

		System.out.println("[TourAPI 상세] overview : " + overview);

		if (overview == null || overview.trim().isEmpty()) {

			return "등록된 문화유산 설명이 없습니다.";
		}

		return overview;
	}

	// =========================================================
	// 주제: parsePageNo 처리
	// 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공
	// =========================================================
	private int parsePageNo(String pageNoParam) {

		if (pageNoParam == null || pageNoParam.isBlank()) {

			return 1;
		}

		try {

			int pageNo = Integer.parseInt(pageNoParam);

			return Math.max(pageNo, 1);

		} catch (NumberFormatException e) {

			return 1;
		}
	}

	// =========================================================
	// TourAPI 결과
	// =========================================================

	private static class TourApiResult {

		private List<CulturalHeritageVo> items;

		private int totalCount;
	}

	// =========================================================
	// TourAPI detailIntro2 이용정보 보관용
	// =========================================================

	private static class HeritageIntroInfo {

		String useTime = "";

		String restDate = "";

		String parking = "";

		String infoCenter = "";
	}
}
