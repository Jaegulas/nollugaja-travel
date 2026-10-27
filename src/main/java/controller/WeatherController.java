package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.WeatherService;

// =========================================================
// 주제: 파일 역할
// 설명: WeatherController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/Weather")
public class WeatherController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final WeatherService weatherService = new WeatherService();

	@Override
	// =========================================================
	// 주제: doGet 처리
	// 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
	// =========================================================
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");

		String region = request.getParameter("region");

		if (region == null || region.trim().isEmpty()) {
			region = "gw";
		}

		//System.out.println("[WeatherController] 날씨 조회 요청 : " + region);

		try {

			String weatherJson = weatherService.getWeather(region);

			response.getWriter().write(weatherJson);

		} catch (Exception e) {

			//System.out.println("[WeatherController] 날씨 조회 실패");

			e.printStackTrace();

			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

			response.getWriter().write("{\"success\":false," + "\"message\":\"날씨 조회 실패\"}");
		}
	}
}
