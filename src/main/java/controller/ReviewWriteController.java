
package controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import dao.ReviewDao;

// =========================================================
// 주제: 파일 역할
// 설명: ReviewWriteController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/ReviewWrite")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 5 * 1024 * 1024,
    maxRequestSize = 27 * 1024 * 1024
)
public class ReviewWriteController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ReviewDao reviewDao = new ReviewDao();

    @Override
    // =========================================================
    // 주제: doPost 처리
    // 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
    // =========================================================
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 1. 로그인 확인
        HttpSession session = request.getSession(false);

        if (session == null ||
            !(session.getAttribute("loginId") instanceof String)) {

            response.sendError(401, "로그인이 필요합니다.");
            return;
        }

        String memberId = (String) session.getAttribute("loginId");
        String category = request.getParameter("category");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        // 로그인한 회원의 이름 가져오기
        Object name = session.getAttribute("loginName");

        String authorNickname =
            name == null ? memberId : name.toString();
        // 2. 입력값 확인
        if (category == null || title == null || content == null) {
            response.sendError(400, "필수 입력값이 없습니다.");
            return;
        }

        category = category.trim();
        title = title.trim();
        content = content.trim();

        Set<String> categories = new HashSet<>(Arrays.asList(
            "자연", "맛집", "문화·역사", "체험·액티비티", "숙소"
        ));

        if (!categories.contains(category)
                || title.isEmpty()
                || content.isEmpty()
                || title.length() > 200
                || content.length() > 10000) {

            response.sendError(400, "입력값이 올바르지 않습니다.");
            return;
        }

        // 3. 업로드 이미지 수집
        List<Part> images = new ArrayList<>();

        for (Part part : request.getParts()) {
            if ("images".equals(part.getName())
                    && part.getSize() > 0) {

                images.add(part);
            }
        }

        if (images.size() > 5) {
            response.sendError(400, "사진은 최대 5장까지 가능합니다.");
            return;
        }

        // 4. 이미지 형식 확인
        List<String> extensions = new ArrayList<>();

        for (Part part : images) {

            if (part.getSize() > 5L * 1024 * 1024) {
                response.sendError(400, "사진은 장당 최대 5MB입니다.");
                return;
            }

            byte[] header = new byte[16];
            int read;

            try (InputStream in = part.getInputStream()) {
                read = in.read(header);
            }

            String extension = detectImageExtension(header, read);

            if (extension == null) {
                response.sendError(
                    400, "JPG, PNG, WEBP 이미지만 허용합니다."
                );
                return;
            }

            extensions.add(extension);
        }

        // 5. DB 저장용 이미지 데이터 생성
        List<String> imagePaths = new ArrayList<>();
        List<byte[]> imageDataList = new ArrayList<>();
        List<String> contentTypes = new ArrayList<>();

        try {
            for (int i = 0; i < images.size(); i++) {

                String extension = extensions.get(i);

                String fileName =
                    UUID.randomUUID().toString() + extension;

                byte[] imageBytes;

                try (InputStream in = images.get(i).getInputStream()) {
                    imageBytes = in.readAllBytes();
                }

                String contentType =
                    ".png".equals(extension) ? "image/png" :
                    ".webp".equals(extension) ? "image/webp" :
                    "image/jpeg";

                imagePaths.add("/ReviewImage?file=" + fileName);
                imageDataList.add(imageBytes);
                contentTypes.add(contentType);
            }

            // 6. 리뷰와 실제 사진을 MySQL에 저장
            reviewDao.insertReview(
                memberId,
                category,
                title,
                content,
                authorNickname,
                imagePaths,
                imageDataList,
                contentTypes
            );

            System.out.println(
                "[ReviewWrite] DB 이미지 저장 성공 : "
                + imagePaths.size()
            );

            // 7. 리뷰 목록으로 이동
            response.sendRedirect(
                request.getContextPath() + "/Review"
            );

        } catch (SQLException e) {

            e.printStackTrace();

            response.sendError(
                500, "리뷰 또는 사진 저장에 실패했습니다."
            );
        }
    }

    // 실제 이미지 파일 형식 판별
    // =========================================================
    // 주제: detectImageExtension 처리
    // 설명: detectImageExtension 기능에 필요한 처리 로직을 수행
    // =========================================================
    private String detectImageExtension(byte[] h, int n) {

        if (n >= 3 &&
            (h[0] & 0xff) == 0xff &&
            (h[1] & 0xff) == 0xd8 &&
            (h[2] & 0xff) == 0xff) {

            return ".jpg";
        }

        if (n >= 8 &&
            (h[0] & 0xff) == 0x89 &&
            h[1] == 0x50 &&
            h[2] == 0x4e &&
            h[3] == 0x47) {

            return ".png";
        }

        if (n >= 12 &&
            h[0] == 'R' &&
            h[1] == 'I' &&
            h[2] == 'F' &&
            h[3] == 'F' &&
            h[8] == 'W' &&
            h[9] == 'E' &&
            h[10] == 'B' &&
            h[11] == 'P') {

            return ".webp";
        }

        return null;
    }
}
