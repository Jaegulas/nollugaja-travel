package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;


import dto.AdminReviewDto;
import dto.MypageReviewDto;
import util.DBCPUtil;
import vo.ReviewCommentVo;
import vo.ReviewVo;

// =========================================================
// 주제: 파일 역할
// 설명: ReviewDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


public class ReviewDao {

	// =========================================================
	// 주제: getReviewCount 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	public int getReviewCount() {

		String sql = "SELECT COUNT(*) FROM review";

		try (Connection conn = DBCPUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			if (rs.next()) {
				return rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	// =========================================================
	// 주제: getReviewList 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	public List<ReviewVo> getReviewList() throws SQLException {

		List<ReviewVo> reviews = new ArrayList<>();

		String sql = "SELECT review_id, member_id, category, title, "
				+ "content, author_nickname, view_count, created_at "
				+ "FROM review ORDER BY created_at DESC, review_id DESC";

		String imageSql = "SELECT image_url FROM review_image " + "WHERE review_id = ? ORDER BY image_order ASC";

		try (Connection conn = DBCPUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery();
				PreparedStatement imagePstmt = conn.prepareStatement(imageSql)) {

			while (rs.next()) {

				ReviewVo review = new ReviewVo();

				review.setReviewId(rs.getLong("review_id"));
				review.setMemberId(rs.getString("member_id"));
				review.setCategory(rs.getString("category"));
				review.setTitle(rs.getString("title"));
				review.setContent(rs.getString("content"));
				review.setAuthorNickname(rs.getString("author_nickname"));
				review.setViewCount(rs.getInt("view_count"));
				review.setCreatedAt(rs.getTimestamp("created_at"));

				// 해당 리뷰에 연결된 사진 조회
				List<String> images = new ArrayList<>();

				imagePstmt.setLong(1, review.getReviewId());

				try (ResultSet imageRs = imagePstmt.executeQuery()) {

					while (imageRs.next()) {
						images.add(imageRs.getString("image_url"));
					}
				}

				review.setImages(images);

				reviews.add(review);
			}
		}

		return reviews;
	}

	// =========================================================
	// 주제: insertReview 처리
	// 설명: 입력받은 데이터를 생성하거나 저장
	// =========================================================
	public void insertReview(String memberId, String category, String title, String content, String authorNickname,
			List<String> imagePaths, List<byte[]> imageDataList, List<String> contentTypes) throws SQLException {

		if (imagePaths.size() != imageDataList.size() || imagePaths.size() != contentTypes.size()) {
			throw new SQLException("이미지 데이터 개수가 일치하지 않습니다.");
		}

		String reviewSql = "INSERT INTO review "
				+ "(member_id, category, title, content, author_nickname, view_count, created_at) "
				+ "VALUES (?, ?, ?, ?, ?, 0, NOW())";

		String imageSql = "INSERT INTO review_image " + "(review_id, image_url, image_order, image_data, content_type) "
				+ "VALUES (?, ?, ?, ?, ?)";

		try (Connection conn = DBCPUtil.getConnection()) {

			conn.setAutoCommit(false);

			try {
				long reviewId;

				try (PreparedStatement pstmt = conn.prepareStatement(reviewSql, Statement.RETURN_GENERATED_KEYS)) {

					pstmt.setString(1, memberId);
					pstmt.setString(2, category);
					pstmt.setString(3, title);
					pstmt.setString(4, content);
					pstmt.setString(5, authorNickname);

					if (pstmt.executeUpdate() != 1) {
						throw new SQLException("리뷰 저장 실패");
					}

					try (ResultSet keys = pstmt.getGeneratedKeys()) {
						if (!keys.next()) {
							throw new SQLException("리뷰 ID 생성 실패");
						}
						reviewId = keys.getLong(1);
					}
				}

				try (PreparedStatement pstmt = conn.prepareStatement(imageSql)) {

					for (int i = 0; i < imagePaths.size(); i++) {
						pstmt.setLong(1, reviewId);
						pstmt.setString(2, imagePaths.get(i));
						pstmt.setInt(3, i);
						pstmt.setBytes(4, imageDataList.get(i));
						pstmt.setString(5, contentTypes.get(i));
						pstmt.addBatch();
					}

					if (!imagePaths.isEmpty()) {
						pstmt.executeBatch();
					}
				}

				conn.commit();

				System.out.println("[ReviewDao] 리뷰 및 DB 이미지 저장 완료 : " + reviewId);

			} catch (SQLException e) {
				conn.rollback();
				throw e;
			} finally {
				conn.setAutoCommit(true);
			}
		}
	}

	public Map<String, Object> getReviewImage(String fileName) throws SQLException {

		String sql = "SELECT image_data, content_type " + "FROM review_image "
				+ "WHERE image_url = ? AND image_data IS NOT NULL";

		try (Connection conn = DBCPUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, "/ReviewImage?file=" + fileName);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (!rs.next()) {
					return null;
				}

				Map<String, Object> result = new HashMap<>();
				result.put("data", rs.getBytes("image_data"));
				result.put("contentType", rs.getString("content_type"));

				return result;
			}
		}
	}

	// ========================================
	// 작성자 또는 관리자만 리뷰 삭제
	// 리뷰 사진 DB 정보도 함께 삭제
	// ========================================
	// =========================================================
	// 주제: deleteReview 처리
	// 설명: 대상 데이터를 삭제하거나 제거
	// =========================================================
	public int deleteReview(long reviewId, String memberId, boolean isAdmin) throws SQLException {

		String deleteImageSql = "DELETE FROM review_image WHERE review_id = ?";

		String deleteReviewSql = "DELETE FROM review WHERE review_id = ?";

		String checkSql = "SELECT member_id FROM review WHERE review_id = ? FOR UPDATE";

		try (Connection conn = DBCPUtil.getConnection()) {

			conn.setAutoCommit(false);

			try {

				// 1. DB에서 실제 작성자 조회
				String authorId = null;

				try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {

					pstmt.setLong(1, reviewId);

					try (ResultSet rs = pstmt.executeQuery()) {

						if (rs.next()) {
							authorId = rs.getString("member_id");
						} else {
							conn.rollback();
							return 0;
						}
					}
				}

				// 2. 삭제 권한 확인
				if (!isAdmin && !memberId.equals(authorId)) {
					conn.rollback();
					return 0;
				}

				// 3. 연결된 사진 DB 정보 삭제
				try (PreparedStatement pstmt = conn.prepareStatement(deleteImageSql)) {

					pstmt.setLong(1, reviewId);
					pstmt.executeUpdate();
				}

				// 4. 리뷰 삭제
				int result;

				try (PreparedStatement pstmt = conn.prepareStatement(deleteReviewSql)) {

					pstmt.setLong(1, reviewId);
					result = pstmt.executeUpdate();
				}

				// 5. 삭제 확정
				conn.commit();

				System.out.println("[ReviewDao] 리뷰 삭제 성공 : " + reviewId);

				return result;

			} catch (SQLException e) {

				conn.rollback();
				throw e;

			} finally {

				conn.setAutoCommit(true);
			}
		}
	}

	// =========================================================
	// 주제: updateReview 처리
	// 설명: 기존 데이터를 입력값에 맞게 수정
	// =========================================================
	public int updateReview(long reviewId, String memberId, boolean isAdmin, String category, String title,
			String content) throws SQLException {

		String sql = "UPDATE review " + "SET category = ?, title = ?, content = ? " + "WHERE review_id = ? "
				+ "AND (member_id = ? OR ? = TRUE)";

		try (Connection conn = DBCPUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, category);
			pstmt.setString(2, title);
			pstmt.setString(3, content);
			pstmt.setLong(4, reviewId);
			pstmt.setString(5, memberId);
			pstmt.setBoolean(6, isAdmin);

			return pstmt.executeUpdate();
		}
	}

	// =========================================================
	// 주제: updateReviewWithImages 처리
	// 설명: 기존 데이터를 입력값에 맞게 수정
	// =========================================================
	public int updateReviewWithImages(
	        long reviewId,
	        String memberId,
	        boolean isAdmin,
	        String category,
	        String title,
	        String content,
	        List<String> keepImages,
	        List<String> newImages,
	        List<byte[]> newImageData,
	        List<String> newContentTypes) throws SQLException {

	    if (keepImages == null || newImages == null
	            || newImageData == null || newContentTypes == null
	            || newImages.size() != newImageData.size()
	            || newImages.size() != newContentTypes.size()) {
	        throw new SQLException("이미지 데이터가 올바르지 않습니다.");
	    }

	    String checkSql =
	        "SELECT member_id FROM review WHERE review_id = ? FOR UPDATE";

	    String selectImagesSql =
	        "SELECT image_url FROM review_image WHERE review_id = ?";

	    String updateSql =
	        "UPDATE review SET category = ?, title = ?, content = ? "
	        + "WHERE review_id = ?";

	    String deleteImagesSql =
	        "DELETE FROM review_image WHERE review_id = ?";

	    String insertImageSql =
	        "INSERT INTO review_image "
	        + "(review_id, image_url, image_order, image_data, content_type) "
	        + "VALUES (?, ?, ?, ?, ?)";

	    try (Connection conn = DBCPUtil.getConnection()) {

	        conn.setAutoCommit(false);

	        try {
	            String authorId;

	            // 1. 리뷰 존재 여부 및 작성자 확인
	            try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
	                pstmt.setLong(1, reviewId);

	                try (ResultSet rs = pstmt.executeQuery()) {
	                    if (!rs.next()) {
	                        conn.rollback();
	                        return 0;
	                    }
	                    authorId = rs.getString("member_id");
	                }
	            }

	            // 2. 작성자 또는 관리자만 수정 가능
	            if (!isAdmin && !memberId.equals(authorId)) {
	                conn.rollback();
	                return 0;
	            }

	            // 3. 기존 사진 데이터 조회
	            Map<String, byte[]> existingData = new HashMap<>();
	            Map<String, String> existingTypes = new HashMap<>();

	            String existingSql =
	                "SELECT image_url, image_data, content_type "
	                + "FROM review_image WHERE review_id = ?";

	            try (PreparedStatement pstmt = conn.prepareStatement(existingSql)) {
	                pstmt.setLong(1, reviewId);

	                try (ResultSet rs = pstmt.executeQuery()) {
	                    while (rs.next()) {
	                        String url = rs.getString("image_url");
	                        existingData.put(url, rs.getBytes("image_data"));
	                        existingTypes.put(url, rs.getString("content_type"));
	                    }
	                }
	            }

	            // 4. 다른 리뷰의 사진을 유지하려는 요청 차단
	            if (keepImages.size() != new HashSet<>(keepImages).size()
	                    || !existingData.keySet().containsAll(keepImages)) {
	                throw new SQLException("유효하지 않은 기존 사진 목록입니다.");
	            }

	            if (keepImages.size() + newImages.size() > 5) {
	                throw new SQLException("사진은 최대 5장까지 가능합니다.");
	            }

	            // 5. 리뷰 내용 수정
	            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
	                pstmt.setString(1, category);
	                pstmt.setString(2, title);
	                pstmt.setString(3, content);
	                pstmt.setLong(4, reviewId);
	                pstmt.executeUpdate();
	            }

	            // 6. 기존 사진을 삭제하고 실제 데이터까지 포함해 재구성
	            try (PreparedStatement pstmt = conn.prepareStatement(deleteImagesSql)) {
	                pstmt.setLong(1, reviewId);
	                pstmt.executeUpdate();
	            }

	            try (PreparedStatement pstmt = conn.prepareStatement(insertImageSql)) {
	                int order = 0;

	                // 유지할 기존 사진: 원본 이미지 데이터도 함께 저장
	                for (String url : keepImages) {
	                    pstmt.setLong(1, reviewId);
	                    pstmt.setString(2, url);
	                    pstmt.setInt(3, order++);
	                    pstmt.setBytes(4, existingData.get(url));
	                    pstmt.setString(5, existingTypes.get(url));
	                    pstmt.addBatch();
	                }

	                // 새로 추가한 사진: 이미지 데이터와 MIME 타입 저장
	                for (int i = 0; i < newImages.size(); i++) {
	                    pstmt.setLong(1, reviewId);
	                    pstmt.setString(2, newImages.get(i));
	                    pstmt.setInt(3, order++);
	                    pstmt.setBytes(4, newImageData.get(i));
	                    pstmt.setString(5, newContentTypes.get(i));
	                    pstmt.addBatch();
	                }

	                if (order > 0) {
	                    pstmt.executeBatch();
	                }
	            }

	            // 7. 모두 성공했을 때만 확정
	            conn.commit();

	            System.out.println("[ReviewDao] 리뷰 및 DB 이미지 수정 완료: " + reviewId);

	            return 1;

	        } catch (SQLException e) {
	            conn.rollback();
	            throw e;
	        } finally {
	            conn.setAutoCommit(true);
	        }
	    }
	}

// ========================================
// 모든 리뷰의 좋아요 개수 초기 조회
// ========================================
	public java.util.Map<Long, Integer> getReviewLikeCounts() throws SQLException {

		java.util.Map<Long, Integer> result = new java.util.HashMap<>();

		String sql = "SELECT review_id, COUNT(*) AS like_count " + "FROM review_like GROUP BY review_id";

		try (Connection conn = DBCPUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				result.put(rs.getLong("review_id"), rs.getInt("like_count"));
			}
		}

		return result;
	}

	// =========================================================
	// 주제: getLikedReviewIds 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	public java.util.List<Long> getLikedReviewIds(String memberId) throws SQLException {

		java.util.List<Long> result = new java.util.ArrayList<>();

		String sql = "SELECT review_id FROM review_like WHERE member_id = ?";

		try (Connection conn = DBCPUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, memberId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					result.add(rs.getLong("review_id"));
				}
			}
		}

		return result;
	}

// ========================================
// 좋아요 토글: 누르면 저장, 다시 누르면 취소
// ========================================
	public java.util.Map<String, Object> toggleReviewLike(long reviewId, String memberId) throws SQLException {

		String lockReviewSql = "SELECT review_id FROM review WHERE review_id = ? FOR UPDATE";

		String findSql = "SELECT like_id FROM review_like " + "WHERE review_id = ? AND member_id = ?";

		String insertSql = "INSERT INTO review_like (review_id, member_id) VALUES (?, ?)";

		String deleteSql = "DELETE FROM review_like WHERE review_id = ? AND member_id = ?";

		String countSql = "SELECT COUNT(*) FROM review_like WHERE review_id = ?";

		try (Connection conn = DBCPUtil.getConnection()) {

			conn.setAutoCommit(false);

			try {
				// 같은 리뷰의 동시 좋아요 요청을 순서대로 처리
				try (PreparedStatement pstmt = conn.prepareStatement(lockReviewSql)) {

					pstmt.setLong(1, reviewId);

					try (ResultSet rs = pstmt.executeQuery()) {
						if (!rs.next()) {
							throw new SQLException("존재하지 않는 리뷰입니다.");
						}
					}
				}

				boolean alreadyLiked;

				try (PreparedStatement pstmt = conn.prepareStatement(findSql)) {

					pstmt.setLong(1, reviewId);
					pstmt.setString(2, memberId);

					try (ResultSet rs = pstmt.executeQuery()) {
						alreadyLiked = rs.next();
					}
				}

				boolean liked = !alreadyLiked;

				if (alreadyLiked) {
					try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
						pstmt.setLong(1, reviewId);
						pstmt.setString(2, memberId);
						pstmt.executeUpdate();
					}
				} else {
					try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
						pstmt.setLong(1, reviewId);
						pstmt.setString(2, memberId);
						pstmt.executeUpdate();
					}
				}

				int count = 0;

				try (PreparedStatement pstmt = conn.prepareStatement(countSql)) {

					pstmt.setLong(1, reviewId);

					try (ResultSet rs = pstmt.executeQuery()) {
						if (rs.next()) {
							count = rs.getInt(1);
						}
					}
				}

				conn.commit();

				java.util.Map<String, Object> result = new java.util.HashMap<>();

				result.put("likes", count);
				result.put("liked", liked);

				return result;

			} catch (SQLException e) {
				conn.rollback();
				throw e;
			} finally {
				conn.setAutoCommit(true);
			}
		}
	}

	// =========================================================
	// 주제: getRecentReviews 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	public List<ReviewVo> getRecentReviews() throws Exception {

		List<ReviewVo> reviews = new ArrayList<>();

		String sql = "SELECT review_id, member_id, category, title, "
				+ "content, author_nickname, view_count, created_at " + "FROM review "
				+ "ORDER BY created_at DESC, review_id DESC LIMIT 3";

		try (Connection con = DBCPUtil.getConnection();
				PreparedStatement pstmt = con.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				ReviewVo review = new ReviewVo();

				review.setReviewId(rs.getLong("review_id"));
				review.setMemberId(rs.getString("member_id"));
				review.setCategory(rs.getString("category"));
				review.setTitle(rs.getString("title"));
				review.setContent(rs.getString("content"));
				review.setAuthorNickname(rs.getString("author_nickname"));
				review.setViewCount(rs.getInt("view_count"));
				review.setCreatedAt(rs.getTimestamp("created_at"));

				reviews.add(review);
			}
		}

		// ==========================================
		// 메인 최신 리뷰 3개의 이미지 조회
		// ==========================================
		String imageSql = "SELECT image_url FROM review_image " + "WHERE review_id = ? "
				+ "ORDER BY image_id ASC LIMIT 3";

		try (Connection con = DBCPUtil.getConnection(); PreparedStatement pstmt = con.prepareStatement(imageSql)) {

			for (ReviewVo review : reviews) {

				pstmt.setLong(1, review.getReviewId());

				List<String> images = new ArrayList<>();

				try (ResultSet rs = pstmt.executeQuery()) {
					while (rs.next()) {
						images.add(rs.getString("image_url"));
					}
				}

				review.setImages(images);
			}
		}

		return reviews;
	}

	
	// ------------------------------------------------------------
    // 마이페이지 - 내 리뷰 목록 (최신순) 권성애 2026.10.15목 추가
    // ------------------------------------------------------------
    // =========================================================
    // 주제: getMypageReviewAll 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    public List<MypageReviewDto> getMypageReviewAll(String loginId) throws SQLException{

        // 리뷰 번호 → DTO  (넣은 순서가 유지되는 Map : 최신순 그대로)
        Map<Long, MypageReviewDto> map = new LinkedHashMap<>();

        // 1번 쿼리 : 리뷰 + 좋아요 수 + 댓글 수
        //   (SELECT COUNT(*) ...) 는 리뷰 한 줄마다 개수를 세어 붙여줌
        //   DATE_FORMAT 으로 날짜를 "2026-10-11" 글자로
        String sql1 =
              "SELECT r.review_id, r.category, r.title, r.content, r.view_count, "
            + "       DATE_FORMAT(r.created_at, '%Y-%m-%d') AS created_date, "
            + "       (SELECT COUNT(*) FROM review_like    l WHERE l.review_id = r.review_id) AS like_count, "
            + "       (SELECT COUNT(*) FROM review_comment c WHERE c.review_id = r.review_id) AS comment_count "
            + "FROM review r "
            + "WHERE r.member_id = ? "
            + "ORDER BY r.review_id DESC";

        // 2번 쿼리 : 내 리뷰에 달린 사진 전부 (리뷰별, 순서대로)
        String sql2 =
              "SELECT i.review_id, i.image_url "
            + "FROM review_image i "
            + "JOIN review r ON r.review_id = i.review_id "
            + "WHERE r.member_id = ? "
            + "ORDER BY i.review_id, i.image_order";

        try (Connection conn = DBCPUtil.getConnection()) {

            // ----- 1번 실행 -----
            try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                ps.setString(1, loginId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                    	MypageReviewDto dto = new MypageReviewDto();
                        dto.setReviewId(rs.getLong("review_id"));
                        dto.setCategory(rs.getString("category"));
                        dto.setTitle(rs.getString("title"));
                        dto.setContent(rs.getString("content"));
                        dto.setViewCount(rs.getInt("view_count"));
                        dto.setCreatedAt(rs.getString("created_date"));
                        dto.setLikeCount(rs.getInt("like_count"));
                        dto.setCommentCount(rs.getInt("comment_count"));

                        map.put(dto.getReviewId(), dto);
                    }
                }
            }

            // 리뷰가 하나도 없으면 사진 조회는 안 해도 됨
            if (map.isEmpty()) {
                return new ArrayList<>();
            }

            // ----- 2번 실행 : 사진을 해당 리뷰 DTO 에 넣기 -----
            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                ps.setString(1, loginId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                    	MypageReviewDto dto = map.get(rs.getLong("review_id"));
                        if (dto != null) {
                            dto.getImages().add(rs.getString("image_url"));
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new SQLException("[ReviewDao] getMypageReviewAll() 예외 발생 "+e);
        }

        return new ArrayList<>(map.values());
    }
    

	// ------------------------------------------------------------
    // 관라지페이지 - 새리뷰글목록 (최신순) 권성애 2026.10.15목 추가 * category 가 비어 있으면 전체, 있으면 그 카테고리만
    // ------------------------------------------------------------

    // =========================================================
    // 주제: reviewAllList 처리
    // 설명: reviewAllList 기능에 필요한 처리 로직을 수행
    // =========================================================
    public List<AdminReviewDto> reviewAllList(String startDate, String endDate, String category ,int offset, int limit)
            throws SQLException {

        List<AdminReviewDto> reviewList = new ArrayList<>();
        Map<Long, AdminReviewDto> reviewMap = new HashMap<>();

        boolean useCategory = category != null && !category.isEmpty();

        // 1) 리뷰글 : 최신글부터
        String reviewSql =
              "SELECT review_id, member_id, category, title, content, "
            + "       author_nickname, view_count, created_at "
            + "FROM review "
            + "WHERE created_at >= ? AND created_at < ? "
            + (useCategory ? "AND category = ? " : "")
            + "ORDER BY created_at DESC, review_id DESC "
            + "LIMIT ? OFFSET ?";             

        // ↓ 기존 DAO 에서 쓰는 커넥션 얻는 코드로 바꾸세요
        try (Connection conn = DBCPUtil.getConnection()) {

            // ---------- 1) 리뷰글 ----------
            try (PreparedStatement pstmt = conn.prepareStatement(reviewSql)) {
            	int idx = 1;
            	 pstmt.setString(idx++, startDate);
                 pstmt.setString(idx++, endDate);
                 if (useCategory) {
                     pstmt.setString(idx++, category);
                 }
                 pstmt.setInt(idx++, limit);// ← 추가 : 몇 개
                 pstmt.setInt(idx++, offset); // ← 추가 : 몇 번째부터
                 
                 
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        AdminReviewDto review = new AdminReviewDto();
                        review.setReviewId(rs.getLong("review_id"));
                        review.setMemberId(rs.getString("member_id"));
                        review.setCategory(rs.getString("category"));
                        review.setTitle(rs.getString("title"));
                        review.setContent(rs.getString("content"));
                        review.setAuthorNickname(rs.getString("author_nickname"));
                        review.setViewCount(rs.getInt("view_count"));
                        review.setCreatedAt(rs.getTimestamp("created_at"));

                        reviewList.add(review);
                        reviewMap.put(review.getReviewId(), review);
                    }
                }
            }

            if (reviewList.isEmpty()) {
                return reviewList;
            }

            // 글 번호 개수만큼 ? 만들기 → "?,?,?"
            StringBuilder marks = new StringBuilder();
            for (int i = 0; i < reviewList.size(); i++) {
                if (i > 0) marks.append(",");
                marks.append("?");
            }
//            // 2) 댓글 : 위 글들에 달린 댓글만
//            String commentSql =
//                  "SELECT c.comment_id, c.review_id, c.member_id, c.author_nickname, "
//                + "       c.content, c.created_at, c.updated_at "
//                + "FROM review_comment c "
//                + "JOIN review r ON c.review_id = r.review_id "
//                + "WHERE r.created_at >= ? AND r.created_at < ? "
//                + (useCategory ? "AND r.category = ? " : "")
//                + "ORDER BY c.created_at ASC, c.comment_id ASC ";

            String commentSql =
                    "SELECT comment_id, review_id, member_id, author_nickname, "
                  + "       content, created_at, updated_at "
                  + "FROM review_comment "
                  + "WHERE review_id IN (" + marks + ") "              // ← 바뀐 곳
                  + "ORDER BY created_at ASC, comment_id ASC";

            
            // ---------- 2) 댓글 → 해당 글에 붙이기 ----------
            try (PreparedStatement pstmt = conn.prepareStatement(commentSql)) {
//                pstmt.setString(1, startDate);
//                pstmt.setString(2, endDate);
//                if (useCategory) {
//                    pstmt.setString(3, category);
//                }
            	
                int idx = 1;
                for (AdminReviewDto review : reviewList) {
                    pstmt.setLong(idx++, review.getReviewId());
                }
                
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        ReviewCommentVo comment = new ReviewCommentVo();
                        comment.setCommentId(rs.getLong("comment_id"));
                        comment.setReviewId(rs.getLong("review_id"));
                        comment.setMemberId(rs.getString("member_id"));
                        comment.setAuthorNickname(rs.getString("author_nickname"));
                        comment.setContent(rs.getString("content"));
                        comment.setCreatedAt(rs.getTimestamp("created_at"));
                        comment.setUpdatedAt(rs.getTimestamp("updated_at"));

                        AdminReviewDto review = reviewMap.get(comment.getReviewId());
                        if (review != null) {
                            review.getCommentList().add(comment);
                        }
                    }
                }
            }
        }catch(SQLException e) {
        	throw new SQLException("[ReviewDao] reviewAllList() 예외 발생" +e);
        }

        return reviewList;
    }

    /**
     * 같은 기간의 카테고리별 글 수 (글 많은 순)
     * 돌려주는 값 예) {"맛집"=4, "숙소"=3, "자연"=2 ...}
     * LinkedHashMap 이라 넣은 순서(= 글 많은 순) 그대로 탭에 나옴
     */
    public Map<String, Integer> reviewCategoryCount(String startDate, String endDate)
            throws SQLException {

        Map<String, Integer> countMap = new LinkedHashMap<>();

        String sql =
              "SELECT category, COUNT(*) AS cnt "
            + "FROM review "
            + "WHERE created_at >= ? AND created_at < ? "
            + "GROUP BY category "
            + "ORDER BY cnt DESC, category ASC";

        // ↓ 기존 DAO 에서 쓰는 커넥션 얻는 코드로 바꾸세요
        try (Connection conn = DBCPUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    countMap.put(rs.getString("category"), rs.getInt("cnt"));
                }
            }
        }catch(SQLException e) {
        	throw new SQLException("[ReviewDao] reviewCategoryCount() 예외 발생" +e);
        }
        System.out.println("[ReviewDao] reviewCategoryCount()  Map<String, Integer> : "+countMap);
        return countMap;
    }
    
    
    
    
}
