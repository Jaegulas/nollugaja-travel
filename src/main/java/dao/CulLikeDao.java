package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import util.DBCPUtil;

// =========================================================
// 주제: 파일 역할
// 설명: CulLikeDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


public class CulLikeDao {

	// =========================================================
	// 주제: isLiked 처리
	// 설명: 입력값 또는 현재 상태가 조건에 맞는지 검사
	// =========================================================
	public boolean isLiked(String memberId, int heritageId) throws Exception {

		String sql =
				"SELECT COUNT(*) " +
				"FROM cultural_like " +
				"WHERE member_id = ? AND heritage_id = ?";

		try (
			Connection con = DBCPUtil.getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql)
		) {

			pstmt.setString(1, memberId);
			pstmt.setInt(2, heritageId);

			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		}

		return false;
	}


	// =========================================================
	// 주제: insertLike 처리
	// 설명: 입력받은 데이터를 생성하거나 저장
	// =========================================================
	public void insertLike(String memberId, int heritageId) throws Exception {

		String sql =
				"INSERT INTO cultural_like " +
				"(member_id, heritage_id) " +
				"VALUES (?, ?)";

		try (
			Connection con = DBCPUtil.getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql)
		) {

			pstmt.setString(1, memberId);
			pstmt.setInt(2, heritageId);

			pstmt.executeUpdate();
		}
	}


	// =========================================================
	// 주제: deleteLike 처리
	// 설명: 대상 데이터를 삭제하거나 제거
	// =========================================================
	public void deleteLike(String memberId, int heritageId) throws Exception {

		String sql =
				"DELETE FROM cultural_like " +
				"WHERE member_id = ? AND heritage_id = ?";

		try (
			Connection con = DBCPUtil.getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql)
		) {

			pstmt.setString(1, memberId);
			pstmt.setInt(2, heritageId);

			pstmt.executeUpdate();
		}
	}


	// =========================================================
	// 주제: toggleLike 처리
	// 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공
	// =========================================================
	public boolean toggleLike(String memberId, int heritageId) throws Exception {

		boolean liked = isLiked(memberId, heritageId);

		// 이미 좋아요가 있으면 삭제
		if (liked) {

			deleteLike(memberId, heritageId);

			return false;

		} else {

			// 좋아요가 없으면 추가
			insertLike(memberId, heritageId);

			return true;
		}
	}


	// ==========================================
	// ★ 초기 조회 기능
	//
	// 현재 로그인한 회원이 좋아요한
	// 모든 문화유산 heritage_id를 가져온다.
	//
	// 예:
	// member_id = "sanghui"
	//
	// DB에
	// sanghui / 1
	// sanghui / 3
	// sanghui / 4
	//
	// 가 저장되어 있다면
	//
	// [1, 3, 4]
	//
	// 를 반환한다.
	// ==========================================
	// ==========================================
	// ★ 좋아요 초기 조회
	// 현재 로그인한 회원의 모든 좋아요 조회
	// ==========================================
	// =========================================================
	// 주제: getLikedHeritageIds 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	public List<Integer> getLikedHeritageIds(String memberId) throws Exception {

	    List<Integer> likedHeritageIds = new ArrayList<>();

	    String sql =
	            "SELECT heritage_id " +
	            "FROM cultural_like " +
	            "WHERE member_id = ? " +
	            "ORDER BY heritage_id";

	    System.out.println();
	    System.out.println("========== DAO 좋아요 초기조회 ==========");
	    System.out.println("[CulLikeDao] memberId = " + memberId);

	    try (
	        Connection con = DBCPUtil.getConnection();
	        PreparedStatement pstmt = con.prepareStatement(sql)
	    ) {

	        pstmt.setString(1, memberId);

	        try (ResultSet rs = pstmt.executeQuery()) {

	            while (rs.next()) {

	                int heritageId =
	                        rs.getInt("heritage_id");

	                likedHeritageIds.add(heritageId);

	                System.out.println(
	                        "[CulLikeDao] 조회 heritageId = "
	                        + heritageId
	                );
	            }
	        }
	    }

	    System.out.println(
	            "[CulLikeDao] 최종 좋아요 목록 = "
	            + likedHeritageIds
	    );

	    System.out.println(
	            "=========================================="
	    );

	    return likedHeritageIds;
	}
}
