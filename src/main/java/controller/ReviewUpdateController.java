
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
// 설명: ReviewUpdateController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/ReviewUpdate")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 5 * 1024 * 1024,
    maxRequestSize = 30 * 1024 * 1024
)
public class ReviewUpdateController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ReviewDao reviewDao = new ReviewDao();

    

    @Override
    // =========================================================
    // 주제: doPost 처리
    // 설명: POST 요청을 받아 입력값을 검증하고 저장/처리 결과를 응답
    // =========================================================
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("[ReviewUpdate] 요청 시작");

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null ||
                !(session.getAttribute("loginId") instanceof String)) {
            response.sendError(401, "로그인이 필요합니다.");
            return;
        }

        String memberId = (String) session.getAttribute("loginId");
        boolean isAdmin = "ADMIN".equals(session.getAttribute("role"));


        try {
            String reviewIdParam = request.getParameter("reviewId");
            String category = request.getParameter("category");
            String title = request.getParameter("title");
            String content = request.getParameter("content");

            long reviewId;

            try {
                reviewId = Long.parseLong(reviewIdParam);
            } catch (NumberFormatException | NullPointerException e) {
                response.sendError(400, "잘못된 리뷰 번호입니다.");
                return;
            }

            if (reviewId <= 0 ||
                    category == null ||
                    title == null ||
                    content == null) {
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

            // 삭제하지 않고 유지할 기존 사진
            List<String> keepImages = new ArrayList<>();

            String[] keepParams =
                request.getParameterValues("keepImages");

            if (keepParams != null) {
                keepImages.addAll(Arrays.asList(keepParams));
            }

            // 새로 추가할 사진
            List<Part> newParts = new ArrayList<>();

            for (Part part : request.getParts()) {
                if ("images".equals(part.getName())
                        && part.getSize() > 0) {
                    newParts.add(part);
                }
            }

            if (keepImages.size() + newParts.size() > 5) {
                response.sendError(400, "사진은 최대 5장까지 가능합니다.");
                return;
            }

            List<String> extensions = new ArrayList<>();

            for (Part part : newParts) {
                if (part.getSize() > 5 * 1024 * 1024) {
                    response.sendError(400, "사진당 최대 5MB입니다.");
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

         // 새 사진의 주소, 실제 이미지 데이터, 파일 형식
            List<String> newImagePaths = new ArrayList<>();
            List<byte[]> newImageData = new ArrayList<>();
            List<String> newContentTypes = new ArrayList<>();

            for (int i = 0; i < newParts.size(); i++) {

                String extension = extensions.get(i);

                String fileName =
                    UUID.randomUUID().toString() + extension;

                // 기존 ReviewImageController에서 사용하는 주소 형식
                newImagePaths.add("/ReviewImage?file=" + fileName);

                // 실제 이미지 데이터를 읽어 MySQL에 전달
                try (InputStream in = newParts.get(i).getInputStream()) {
                    newImageData.add(in.readAllBytes());
                }

                // 파일 확장자에 맞는 Content-Type 저장
                String contentType;

                if (".jpg".equals(extension)) {
                    contentType = "image/jpeg";
                } else if (".png".equals(extension)) {
                    contentType = "image/png";
                } else {
                    contentType = "image/webp";
                }

                newContentTypes.add(contentType);
            }

            int result = reviewDao.updateReviewWithImages(
            	    reviewId,
            	    memberId,
            	    isAdmin,
            	    category,
            	    title,
            	    content,
            	    keepImages,
            	    newImagePaths,
            	    newImageData,
            	    newContentTypes
            	);

            if (result != 1) {
                response.sendError(
                    403, "수정 권한이 없거나 리뷰가 존재하지 않습니다."
                );
                return;
            }

            

            System.out.println(
                "[ReviewUpdate] 수정 성공 : " + reviewId
                + " / 기존 유지: " + keepImages.size()
                + " / 신규 추가: " + newImagePaths.size()
            );

            response.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (IllegalStateException e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                response.sendError(413, "업로드 용량 제한을 초과했습니다.");
            }

        } catch (SQLException | IOException | ServletException e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                response.sendError(500, "리뷰 수정 중 오류가 발생했습니다.");
            }

        } 
    }

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
            h[0] == 'R' && h[1] == 'I' &&
            h[2] == 'F' && h[3] == 'F' &&
            h[8] == 'W' && h[9] == 'E' &&
            h[10] == 'B' && h[11] == 'P') {
            return ".webp";
        }

        return null;
    }
}
