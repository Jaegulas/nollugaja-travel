
package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ReviewDao;

// =========================================================
// 주제: 파일 역할
// 설명: ReviewImageController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/ReviewImage")
public class ReviewImageController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ReviewDao reviewDao = new ReviewDao();

    @Override
    // =========================================================
    // 주제: doGet 처리
    // 설명: GET 요청을 받아 필요한 데이터를 준비하고 화면 또는 응답으로 전달
    // =========================================================
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        String fileName = request.getParameter("file");

        // 1. 파일명 확인
        if (fileName == null ||
            !fileName.matches("[a-fA-F0-9-]{36}\\.(jpg|png|webp)")) {

            response.sendError(400);
            return;
        }

        try {
            // 2. MySQL에서 이미지 조회
            Map<String, Object> image =
                reviewDao.getReviewImage(fileName);

            if (image == null) {
                response.sendError(404);
                return;
            }

            byte[] imageData =
                (byte[]) image.get("data");

            String contentType =
                (String) image.get("contentType");

            // 3. 이미지 데이터 검증
            if (imageData == null ||
                contentType == null ||
                !(contentType.equals("image/jpeg") ||
                  contentType.equals("image/png") ||
                  contentType.equals("image/webp"))) {

                response.sendError(404);
                return;
            }

            // 4. 이미지 응답
            response.setContentType(contentType);
            response.setContentLength(imageData.length);

            response.setHeader(
                "X-Content-Type-Options",
                "nosniff"
            );

            response.getOutputStream().write(imageData);

        } catch (SQLException e) {

            e.printStackTrace();

            if (!response.isCommitted()) {
                response.sendError(
                    500, "이미지 조회 중 오류가 발생했습니다."
                );
            }
        }
    }
}
