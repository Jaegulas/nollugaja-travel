package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import config.AppConfigListener;

// =========================================================
// 주제: 파일 역할
// 설명: WeatherService의 핵심 비즈니스 로직과 데이터 가공을 담당하는 서비스
// =========================================================


public class WeatherService {

	// =========================================================
	// 기상청 API 기본 주소
	// =========================================================
	private static final String API_BASE_URL = "https://apis.data.go.kr/1360000/" + "VilageFcstInfoService_2.0/";

	// 현재 날씨
	private static final String ULTRA_NCST_URL = API_BASE_URL + "getUltraSrtNcst";

	// 단기 예보
	private static final String VILAGE_FCST_URL = API_BASE_URL + "getVilageFcst";

	private static String apikey = AppConfigListener.getProperty("weather.api.key");

	private final ObjectMapper objectMapper = new ObjectMapper();

	// =========================================================
	// 주제: getWeather 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	public String getWeather(String region) throws Exception {

		//System.out.println("======================================");
		//System.out.println("[WeatherService] 전체 날씨 조회 시작");
		//System.out.println("[WeatherService] region : " + region);

		// -----------------------------------------------------
		// 지역 좌표
		// -----------------------------------------------------
		int[] grid = getGrid(region);

		int nx = grid[0];
		int ny = grid[1];

		//System.out.println("[WeatherService] nx : " + nx);
		//System.out.println("[WeatherService] ny : " + ny);

		// -----------------------------------------------------
		// 최종적으로 브라우저에 보낼 JSON
		// -----------------------------------------------------
		ObjectNode result = objectMapper.createObjectNode();

		result.put("success", true);
		result.put("region", region);
		result.put("regionName", getRegionName(region));

		// -----------------------------------------------------
		// 1. 현재 날씨
		// -----------------------------------------------------
		loadCurrentWeather(result, nx, ny);

		// -----------------------------------------------------
		// 2. 단기 예보
		// -----------------------------------------------------
		loadForecast(result, nx, ny);

		//System.out.println("[WeatherService] 최종 JSON");

		//System.out.println(result.toPrettyString());

		//System.out.println("======================================");

		return result.toString();
	}

	// =========================================================
	// 현재 날씨
	// getUltraSrtNcst
	// =========================================================
	// =========================================================
	// 주제: loadCurrentWeather 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	private void loadCurrentWeather(ObjectNode result, int nx, int ny) throws Exception {

		LocalDateTime now = LocalDateTime.now();

		// 자료 제공 지연을 고려
		LocalDateTime base = now.minusHours(1);

		String baseDate = base.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

		String baseTime = base.format(DateTimeFormatter.ofPattern("HH00"));

		//System.out.println("[현재날씨] baseDate : " + baseDate);

		//System.out.println("[현재날씨] baseTime : " + baseTime);

		String json = callWeatherApi(ULTRA_NCST_URL, baseDate, baseTime, nx, ny);

		JsonNode root = objectMapper.readTree(json);

		checkApiResult(root);

		JsonNode items = root.path("response").path("body").path("items").path("item");

		String temperature = "-";
		String humidity = "-";
		String pty = "0";
		String rainAmount = "0";
		String windSpeed = "-";

		if (items.isArray()) {

			for (JsonNode item : items) {

				String category = item.path("category").asText();

				String value = item.path("obsrValue").asText();

				switch (category) {

				case "T1H":
					temperature = value;
					break;

				case "REH":
					humidity = value;
					break;

				case "PTY":
					pty = value;
					break;

				case "RN1":
					rainAmount = value;
					break;

				case "WSD":
					windSpeed = value;
					break;
				}
			}
		}

		result.put("temperature", temperature);

		result.put("humidity", humidity);

		result.put("condition", getPtyCondition(pty));

		result.put("rainAmount", rainAmount);

		result.put("windSpeed", windSpeed);

		result.put("updateTime", formatTime(baseTime));
	}

	// =========================================================
	// 단기예보
	// getVilageFcst
	// =========================================================
	// =========================================================
	// 주제: loadForecast 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	private void loadForecast(ObjectNode result, int nx, int ny) throws Exception {

		LocalDateTime now = LocalDateTime.now();

		LocalDateTime forecastBase = getForecastBaseTime(now);

		String baseDate = forecastBase.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

		String baseTime = forecastBase.format(DateTimeFormatter.ofPattern("HHmm"));

		//System.out.println("[단기예보] baseDate : " + baseDate);

		//System.out.println("[단기예보] baseTime : " + baseTime);

		String json = null;
		JsonNode root = null;

		// 최대 3개 발표 시각을 확인
		for (int attempt = 0; attempt < 3; attempt++) {

			String retryDate = forecastBase.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

			String retryTime = forecastBase.format(DateTimeFormatter.ofPattern("HHmm"));

			//System.out.println("[단기예보] 조회 시도 " + (attempt + 1) + " : " + retryDate + " " + retryTime);

			json = callWeatherApi(VILAGE_FCST_URL, retryDate, retryTime, nx, ny);

			root = objectMapper.readTree(json);

			String resultCode = root.path("response").path("header").path("resultCode").asText();

			if ("00".equals(resultCode)) {
				break;
			}

			// NO_DATA일 때만 이전 발표 자료로 재시도
			if ("03".equals(resultCode) && attempt < 2) {

				forecastBase = getForecastBaseTime(forecastBase.minusMinutes(1));

				continue;
			}

			checkApiResult(root);
		}

		// 마지막으로 정상 응답인지 확인
		checkApiResult(root);

		JsonNode items = root.path("response").path("body").path("items").path("item");

		// =====================================================
		// 시간별 예보 저장
		// =====================================================
		Map<String, ForecastItem> forecastMap = new LinkedHashMap<>();

		// =====================================================
		// 날짜별 예보 저장
		// =====================================================
		Map<String, DailyForecast> dailyMap = new LinkedHashMap<>();

		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

		// =====================================================
		// 기상청 데이터 분석
		// =====================================================
		if (items.isArray()) {

			for (JsonNode item : items) {

				String category = item.path("category").asText();

				String fcstDate = item.path("fcstDate").asText();

				String fcstTime = item.path("fcstTime").asText();

				String value = item.path("fcstValue").asText();

				// =================================================
				// 날짜별 객체 생성
				// =================================================
				DailyForecast daily = dailyMap.get(fcstDate);

				if (daily == null) {

					daily = new DailyForecast();

					daily.date = fcstDate;

					dailyMap.put(fcstDate, daily);
				}

				// =================================================
				// 최저기온
				// =================================================
				if ("TMN".equals(category)) {

					daily.minTemp = value;
				}

				// =================================================
				// 최고기온
				// =================================================
				if ("TMX".equals(category)) {

					daily.maxTemp = value;
				}

				// 오전 / 오후 구분
				int forecastHour = Integer.parseInt(fcstTime.substring(0, 2));
				boolean isMorning = forecastHour < 12;

				// SKY: 하늘 상태
				if ("SKY".equals(category)) {

					daily.sky = value;

					if (isMorning) {
						daily.amSky = value;
					} else {
						daily.pmSky = value;
					}
				}

				// PTY: 강수 형태
				if ("PTY".equals(category)) {

					if (!"0".equals(value)) {
						daily.pty = value;
					} else if (daily.pty == null) {
						daily.pty = value;
					}

					if (isMorning) {
						daily.amPty = value;
					} else {
						daily.pmPty = value;
					}
				}

				// =================================================
				// 시간별 예보
				// =================================================
				if ("TMP".equals(category) || "SKY".equals(category) || "PTY".equals(category)
						|| "POP".equals(category)) {

					String key = fcstDate + fcstTime;

					ForecastItem forecast = forecastMap.get(key);

					if (forecast == null) {

						forecast = new ForecastItem();

						forecast.date = fcstDate;

						forecast.time = fcstTime;

						forecastMap.put(key, forecast);
					}

					switch (category) {

					case "TMP":

						forecast.temperature = value;

						break;

					case "SKY":

						forecast.sky = value;

						break;

					case "PTY":

						forecast.pty = value;

						break;

					case "POP":

						forecast.pop = value;

						break;
					}
				}
			}
		}

		// =====================================================
		// 오늘 데이터
		// =====================================================
		DailyForecast todayForecast = dailyMap.get(today);

		String minTemp = "-";
		String maxTemp = "-";

		if (todayForecast != null) {

			if (todayForecast.minTemp != null) {

				minTemp = todayForecast.minTemp;
			}

			if (todayForecast.maxTemp != null) {

				maxTemp = todayForecast.maxTemp;
			}
		}

		// =====================================================
		// TMN이 없으면
		// 오늘 시간별 TMP 중 가장 낮은 값을 사용
		// =====================================================
		if ("-".equals(minTemp)) {

			Double minimum = null;

			for (ForecastItem item : forecastMap.values()) {

				if (!today.equals(item.date)) {
					continue;
				}

				if (item.temperature == null) {
					continue;
				}

				try {

					double temp = Double.parseDouble(item.temperature);

					if (minimum == null || temp < minimum) {

						minimum = temp;
					}

				} catch (NumberFormatException e) {

					// 숫자가 아닌 데이터는 무시
				}
			}

			if (minimum != null) {

				minTemp = formatTemperature(minimum);
			}
		}

		// =====================================================
		// TMX가 없으면
		// 오늘 시간별 TMP 중 가장 높은 값을 사용
		// =====================================================
		if ("-".equals(maxTemp)) {

			Double maximum = null;

			for (ForecastItem item : forecastMap.values()) {

				if (!today.equals(item.date)) {
					continue;
				}

				if (item.temperature == null) {
					continue;
				}

				try {

					double temp = Double.parseDouble(item.temperature);

					if (maximum == null || temp > maximum) {

						maximum = temp;
					}

				} catch (NumberFormatException e) {

					// 숫자가 아닌 데이터는 무시
				}
			}

			if (maximum != null) {

				maxTemp = formatTemperature(maximum);
			}
		}

		result.put("minTemp", minTemp);

		result.put("maxTemp", maxTemp);

		// =====================================================
		// 가장 가까운 강수확률
		// =====================================================
		String rainProbability = "0";

		for (ForecastItem item : forecastMap.values()) {

			if (item.pop != null) {

				rainProbability = item.pop;

				break;
			}
		}

		result.put("rainProbability", rainProbability);

		// =====================================================
		// 시간대별 예보
		// =====================================================
		ArrayNode hourly = objectMapper.createArrayNode();

		int hourlyCount = 0;

		for (ForecastItem item : forecastMap.values()) {

			if (item.temperature == null) {
				continue;
			}

			ObjectNode hour = objectMapper.createObjectNode();

			hour.put("date", item.date);

			hour.put("time", formatForecastTime(item.time));

			hour.put("temperature", item.temperature);

			hour.put("rainProbability", item.pop == null ? "0" : item.pop);

			hour.put("condition", getForecastCondition(item.sky, item.pty));

			hourly.add(hour);

			hourlyCount++;

			// 화면에는 8개만
			if (hourlyCount >= 8) {

				break;
			}
		}

		result.set("hourly", hourly);

		// =====================================================
		// 오늘 / 내일 / 모레 예보 생성
		// =====================================================
		ArrayNode weekly = objectMapper.createArrayNode();

		LocalDate todayDate = LocalDate.now();

		for (int day = 0; day < 3; day++) {

			LocalDate targetDate = todayDate.plusDays(day);

			String dateKey = targetDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

			DailyForecast daily = dailyMap.get(dateKey);

			if (daily == null) {
				continue;
			}

			String dailyMin = daily.minTemp;

			String dailyMax = daily.maxTemp;

			// -------------------------------------------------
			// TMN / TMX가 없다면 TMP로 계산
			// -------------------------------------------------
			Double calculatedMin = null;

			Double calculatedMax = null;

			for (ForecastItem item : forecastMap.values()) {

				if (!dateKey.equals(item.date)) {
					continue;
				}

				if (item.temperature == null) {
					continue;
				}

				try {

					double temp = Double.parseDouble(item.temperature);

					if (calculatedMin == null || temp < calculatedMin) {

						calculatedMin = temp;
					}

					if (calculatedMax == null || temp > calculatedMax) {

						calculatedMax = temp;
					}

				} catch (NumberFormatException e) {

					// 무시
				}
			}

			if (dailyMin == null && calculatedMin != null) {

				dailyMin = formatTemperature(calculatedMin);
			}

			if (dailyMax == null && calculatedMax != null) {

				dailyMax = formatTemperature(calculatedMax);
			}

			if (dailyMin == null) {

				dailyMin = "-";
			}

			if (dailyMax == null) {

				dailyMax = "-";
			}

			ObjectNode dayNode = objectMapper.createObjectNode();

			// -------------------------------------------------
			// 오늘 / 내일 / 모레
			// -------------------------------------------------
			String dateName;

			if (day == 0) {

				dateName = "오늘";

			} else if (day == 1) {

				dateName = "내일";

			} else {

				dateName = "모레";
			}

			dayNode.put("date", dateName);

			dayNode.put("realDate", targetDate.format(DateTimeFormatter.ofPattern("MM.dd")));

			dayNode.put("minTemp", dailyMin);

			dayNode.put("maxTemp", dailyMax);

			dayNode.put("condition", getForecastCondition(daily.sky, daily.pty));

			// 오전 예보
			if (daily.amSky != null || daily.amPty != null) {
				dayNode.put("amCondition", getForecastCondition(daily.amSky, daily.amPty));
			} else {
				dayNode.putNull("amCondition");
			}

			// 오후 예보
			if (daily.pmSky != null || daily.pmPty != null) {
				dayNode.put("pmCondition", getForecastCondition(daily.pmSky, daily.pmPty));
			} else {
				dayNode.putNull("pmCondition");
			}

			weekly.add(dayNode);
		}

		result.set("weekly", weekly);
	}

	// =========================================================
	// 주제: callWeatherApi 처리
	// 설명: callWeatherApi 기능에 필요한 처리 로직을 수행
	// =========================================================
	private String callWeatherApi(String apiUrl, String baseDate, String baseTime, int nx, int ny) throws Exception {

		StringBuilder urlBuilder = new StringBuilder(apiUrl);

		urlBuilder.append("?serviceKey=");

		urlBuilder.append(URLEncoder.encode(apikey, StandardCharsets.UTF_8));

		urlBuilder.append("&pageNo=1");

		urlBuilder.append("&numOfRows=1000");

		urlBuilder.append("&dataType=JSON");

		urlBuilder.append("&base_date=").append(baseDate);

		urlBuilder.append("&base_time=").append(baseTime);

		urlBuilder.append("&nx=").append(nx);

		urlBuilder.append("&ny=").append(ny);

		URI uri = URI.create(urlBuilder.toString());

		URL url = uri.toURL();

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestMethod("GET");

		connection.setConnectTimeout(5000);

		connection.setReadTimeout(5000);

		int responseCode = connection.getResponseCode();

		//System.out.println("[Weather API] HTTP 응답 : " + responseCode);

		BufferedReader reader;

		if (responseCode >= 200 && responseCode < 300) {

			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

		} else {

			reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
		}

		StringBuilder response = new StringBuilder();

		String line;

		while ((line = reader.readLine()) != null) {

			response.append(line);
		}

		reader.close();
		connection.disconnect();

		if (responseCode < 200 || responseCode >= 300) {

			throw new RuntimeException("기상청 API HTTP 오류 : " + responseCode + "\n" + response);
		}

		return response.toString();
	}

	// =========================================================
	// 주제: checkApiResult 처리
	// 설명: 입력값 또는 현재 상태가 조건에 맞는지 검사
	// =========================================================
	private void checkApiResult(JsonNode root) {

		String resultCode = root.path("response").path("header").path("resultCode").asText();

		String resultMsg = root.path("response").path("header").path("resultMsg").asText();

		if (!"00".equals(resultCode)) {

			throw new RuntimeException("기상청 API 오류 : " + resultCode + " / " + resultMsg);
		}
	}

	// =========================================================
	// 주제: getForecastBaseTime 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	private LocalDateTime getForecastBaseTime(LocalDateTime now) {

		/*
		 * 단기예보 발표시각
		 *
		 * 02:00 05:00 08:00 11:00 14:00 17:00 20:00 23:00
		 *
		 * API 자료가 생성되는 시간을 고려하여 약간의 여유를 둔다.
		 */

		int[] baseHours = { 2, 5, 8, 11, 14, 17, 20, 23 };

		// 자료 생성 지연을 고려하여
		// 현재시간에서 10분 여유
		LocalDateTime target = now.minusMinutes(30);

		for (int i = baseHours.length - 1; i >= 0; i--) {

			int hour = baseHours[i];

			if (target.getHour() >= hour) {

				return target.withHour(hour).withMinute(0).withSecond(0).withNano(0);
			}
		}

		// 새벽 02시 이전이면
		// 전날 23시 자료 사용
		return target.minusDays(1).withHour(23).withMinute(0).withSecond(0).withNano(0);
	}

	// =========================================================
	// 주제: getPtyCondition 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	private String getPtyCondition(String pty) {

		switch (pty) {

		case "0":
			return "맑음";

		case "1":
			return "비";

		case "2":
			return "비/눈";

		case "3":
			return "눈";

		case "5":
			return "빗방울";

		case "6":
			return "빗방울/눈날림";

		case "7":
			return "눈날림";

		default:
			return "맑음";
		}
	}

	// =========================================================
	// 주제: getForecastCondition 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	private String getForecastCondition(String sky, String pty) {

		// 비/눈이 있으면 SKY보다 PTY 우선
		if (pty != null && !"0".equals(pty)) {

			switch (pty) {

			case "1":
				return "비";

			case "2":
				return "비/눈";

			case "3":
				return "눈";

			case "4":
				return "소나기";

			default:
				return "비";
			}
		}

		if (sky == null) {

			return "맑음";
		}

		switch (sky) {

		case "1":
			return "맑음";

		case "3":
			return "구름많음";

		case "4":
			return "흐림";

		default:
			return "맑음";
		}
	}

	// =========================================================
	// 주제: formatTime 처리
	// 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공
	// =========================================================
	private String formatTime(String time) {

		if (time == null || time.length() != 4) {

			return time;
		}

		return time.substring(0, 2) + ":" + time.substring(2, 4);
	}

	// =========================================================
	// 시간대별 표시
	// 0900 → 09시
	// =========================================================
	// =========================================================
	// 주제: formatForecastTime 처리
	// 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공
	// =========================================================
	private String formatForecastTime(String time) {

		if (time == null || time.length() != 4) {

			return time;
		}

		return time.substring(0, 2) + "시";
	}

	// =========================================================
	// 주제: formatTemperature 처리
	// 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공
	// =========================================================
	private String formatTemperature(double temperature) {

		if (temperature == Math.floor(temperature)) {

			return String.valueOf((int) temperature);
		}

		return String.format("%.1f", temperature);
	}

	// =========================================================
	// 주제: getRegionName 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	private String getRegionName(String region) {

		switch (region) {

		case "cap":
			return "서울·경기권";

		case "gw":
			return "강원권";

		case "cc":
			return "충청권";

		case "hn":
			return "호남권";

		case "yn":
			return "영남권";

		case "jj":
			return "제주도";

		default:
			return "강원권";
		}
	}

	// =========================================================
	// 주제: getGrid 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	private int[] getGrid(String region) {

		switch (region) {

		// 서울
		case "cap":

			return new int[] { 60, 127 };

		// 춘천
		case "gw":

			return new int[] { 73, 134 };

		// 대전
		case "cc":

			return new int[] { 67, 100 };

		// 광주
		case "hn":

			return new int[] { 58, 74 };

		// 부산
		case "yn":

			return new int[] { 98, 76 };

		// 제주
		case "jj":

			return new int[] { 52, 38 };

		default:

			return new int[] { 73, 134 };
		}
	}

	// =========================================================
	// 시간별 예보 임시 저장용 클래스
	// =========================================================
	private static class ForecastItem {

		String date;

		String time;

		String temperature;

		String sky;

		String pty;

		String pop;
	}

	private static class DailyForecast {

		String date;

		String minTemp;
		String maxTemp;

		String sky;
		String pty;

		String amSky;
		String amPty;

		String pmSky;
		String pmPty;
	}
}
