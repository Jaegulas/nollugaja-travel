package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.DBCPUtil;
import vo.CulturalHeritageVo;

// =========================================================
// 주제: 파일 역할
// 설명: CulturalHeritageDao 관련 데이터베이스 조회·저장·수정·삭제를 담당하는 DAO
// =========================================================


public class CulturalHeritageDao {

	// =========================================================
	// 1. TourAPI 문화유산 데이터를 DB에 저장
	//
	// content_id가 없으면 → INSERT
	// content_id가 이미 있으면 → UPDATE
	// =========================================================
	// =========================================================
	// 주제: saveOrUpdate 처리
	// 설명: 입력받은 데이터를 생성하거나 저장
	// =========================================================
	public int saveOrUpdate(CulturalHeritageVo vo) {

		String sql = "INSERT INTO cultural_heritage (" + "content_id, title, addr1, first_image, "
				+ "map_x, map_y, area_code, sigungu_code, content_type_id, "
				+ "use_time, rest_date, parking, info_center" + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
				+ "ON DUPLICATE KEY UPDATE " + "title = VALUES(title), " + "addr1 = VALUES(addr1), "
				+ "first_image = VALUES(first_image), " + "map_x = VALUES(map_x), " + "map_y = VALUES(map_y), "
				+ "area_code = VALUES(area_code), " + "sigungu_code = VALUES(sigungu_code), "
				+ "content_type_id = VALUES(content_type_id), " + "use_time = VALUES(use_time), "
				+ "rest_date = VALUES(rest_date), " + "parking = VALUES(parking), "
				+ "info_center = VALUES(info_center)";

		try (Connection con = DBCPUtil.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			// contentId는 VO에서 String이므로 int로 변환
			pstmt.setInt(1, Integer.parseInt(vo.getContentId()));

			pstmt.setString(2, vo.getTitle());
			pstmt.setString(3, vo.getAddr1());
			pstmt.setString(4, vo.getFirstImage());

			// 좌표는 값이 없을 수도 있으므로 별도 처리
			setDoubleOrNull(pstmt, 5, vo.getMapX());
			setDoubleOrNull(pstmt, 6, vo.getMapY());

			pstmt.setString(7, vo.getAreaCode());
			pstmt.setString(8, vo.getSigunguCode());
			pstmt.setString(9, vo.getContentTypeId());

			pstmt.setString(10, vo.getUseTime());
			pstmt.setString(11, vo.getRestDate());
			pstmt.setString(12, vo.getParking());
			pstmt.setString(13, vo.getInfoCenter());

			int result = pstmt.executeUpdate();

			System.out.println("[CulturalHeritageDao] 저장/수정 성공 : " + vo.getContentId() + " / " + vo.getTitle());

			return result;

		} catch (Exception e) {

			System.out.println("[CulturalHeritageDao] 저장 실패 : " + vo.getContentId() + " / " + vo.getTitle());

			e.printStackTrace();

			return 0;
		}
	}

	// =========================================================
	// 주제: saveAll 처리
	// 설명: 입력받은 데이터를 생성하거나 저장
	// =========================================================
	public int saveAll(List<CulturalHeritageVo> list) {

		int successCount = 0;

		if (list == null || list.isEmpty()) {
			System.out.println("[CulturalHeritageDao] 저장할 데이터가 없습니다.");
			return 0;
		}

		for (CulturalHeritageVo vo : list) {

			if (vo == null) {
				continue;
			}

			if (vo.getContentId() == null || vo.getContentId().trim().isEmpty()) {

				System.out.println("[CulturalHeritageDao] contentId가 없어 저장하지 않습니다.");

				continue;
			}

			int result = saveOrUpdate(vo);

			if (result > 0) {
				successCount++;
			}
		}

		System.out.println("[CulturalHeritageDao] 전체 " + list.size() + "건 중 " + successCount + "건 저장/수정 완료");

		return successCount;
	}

	// =========================================================
	// 주제: findAll 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	public List<CulturalHeritageVo> findAll() {

		List<CulturalHeritageVo> list = new ArrayList<>();

		String sql = "SELECT " + "content_id, title, addr1, first_image, "
				+ "map_x, map_y, area_code, sigungu_code, content_type_id, "
				+ "use_time, rest_date, parking, info_center " + "FROM cultural_heritage " + "ORDER BY content_id DESC";

		try (Connection con = DBCPUtil.getConnection();
				PreparedStatement pstmt = con.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {

				CulturalHeritageVo vo = new CulturalHeritageVo();

				vo.setContentId(String.valueOf(rs.getInt("content_id")));

				vo.setTitle(rs.getString("title"));
				vo.setAddr1(rs.getString("addr1"));
				vo.setFirstImage(rs.getString("first_image"));

				vo.setMapX(rs.getString("map_x"));
				vo.setMapY(rs.getString("map_y"));

				vo.setAreaCode(rs.getString("area_code"));
				vo.setSigunguCode(rs.getString("sigungu_code"));
				vo.setContentTypeId(rs.getString("content_type_id"));

				vo.setUseTime(rs.getString("use_time"));
				vo.setRestDate(rs.getString("rest_date"));
				vo.setParking(rs.getString("parking"));
				vo.setInfoCenter(rs.getString("info_center"));

				list.add(vo);
			}

			System.out.println("[CulturalHeritageDao] DB 문화유산 조회 : " + list.size() + "건");

		} catch (SQLException e) {

			System.out.println("[CulturalHeritageDao] 문화유산 조회 실패");

			e.printStackTrace();
		}

		return list;
	}

	// =====================================================
	// 문화유산 페이지 단위 조회
	// pageNo   : 현재 페이지 번호 (1, 2, 3...)
	// pageSize : 한 페이지에 보여줄 개수
	// =====================================================
	// =========================================================
	// 주제: findPage 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	public List<CulturalHeritageVo> findPage(int pageNo, int pageSize) {

	    List<CulturalHeritageVo> list = new ArrayList<>();

	    String sql =
	        "SELECT "
	      + "content_id, title, addr1, first_image, "
	      + "map_x, map_y, area_code, sigungu_code, content_type_id, "
	      + "use_time, rest_date, parking, info_center "
	      + "FROM cultural_heritage "
	      + "ORDER BY content_id DESC "
	      + "LIMIT ? OFFSET ?";

	    // 1페이지 → 0
	    // 2페이지 → 12
	    // 3페이지 → 24
	    int offset = (pageNo - 1) * pageSize;

	    try (
	        Connection conn = DBCPUtil.getConnection();
	        PreparedStatement pstmt = conn.prepareStatement(sql)
	    ) {

	        pstmt.setInt(1, pageSize);
	        pstmt.setInt(2, offset);

	        try (ResultSet rs = pstmt.executeQuery()) {

	            while (rs.next()) {

	                CulturalHeritageVo vo = new CulturalHeritageVo();

	                vo.setContentId(
	                    String.valueOf(rs.getInt("content_id"))
	                );

	                vo.setTitle(rs.getString("title"));
	                vo.setAddr1(rs.getString("addr1"));
	                vo.setFirstImage(rs.getString("first_image"));

	                vo.setMapX(rs.getString("map_x"));
	                vo.setMapY(rs.getString("map_y"));

	                vo.setAreaCode(rs.getString("area_code"));
	                vo.setSigunguCode(rs.getString("sigungu_code"));
	                vo.setContentTypeId(
	                    rs.getString("content_type_id")
	                );

	                vo.setUseTime(rs.getString("use_time"));
	                vo.setRestDate(rs.getString("rest_date"));
	                vo.setParking(rs.getString("parking"));
	                vo.setInfoCenter(rs.getString("info_center"));

	                list.add(vo);
	            }
	        }

	        System.out.println(
	            "[CulturalHeritageDao] DB 페이지 조회 성공"
	            + " / pageNo : " + pageNo
	            + " / pageSize : " + pageSize
	            + " / 조회 건수 : " + list.size()
	        );

	    } catch (Exception e) {

	        System.err.println(
	            "[CulturalHeritageDao] DB 페이지 조회 실패"
	        );

	        e.printStackTrace();
	    }

	    return list;
	}
	
	
	// =========================================================
	// 주제: findById 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	public CulturalHeritageVo findById(String contentId) {

		String sql = "SELECT " + "content_id, title, addr1, first_image, "
				+ "map_x, map_y, area_code, sigungu_code, content_type_id, "
				+ "use_time, rest_date, parking, info_center " + "FROM cultural_heritage " + "WHERE content_id = ?";

		try (Connection con = DBCPUtil.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setInt(1, Integer.parseInt(contentId));

			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {

					CulturalHeritageVo vo = new CulturalHeritageVo();

					vo.setContentId(String.valueOf(rs.getInt("content_id")));

					vo.setTitle(rs.getString("title"));
					vo.setAddr1(rs.getString("addr1"));
					vo.setFirstImage(rs.getString("first_image"));

					vo.setMapX(rs.getString("map_x"));
					vo.setMapY(rs.getString("map_y"));

					vo.setAreaCode(rs.getString("area_code"));
					vo.setSigunguCode(rs.getString("sigungu_code"));
					vo.setContentTypeId(rs.getString("content_type_id"));

					vo.setUseTime(rs.getString("use_time"));
					vo.setRestDate(rs.getString("rest_date"));
					vo.setParking(rs.getString("parking"));
					vo.setInfoCenter(rs.getString("info_center"));

					return vo;
				}
			}

		} catch (Exception e) {

			System.out.println("[CulturalHeritageDao] 단일 문화유산 조회 실패 : " + contentId);

			e.printStackTrace();
		}

		return null;
	}
	
	// =========================================================
	// 5. 상세 이용정보가 아직 없는 문화유산 조회
	//
	// use_time / rest_date / parking / info_center가
	// 모두 비어 있는 문화유산을 지정한 개수만큼 가져온다.
	// =========================================================
	// =========================================================
	// 주제: findMissingIntroInfo 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	public List<CulturalHeritageVo> findMissingIntroInfo(int limit) {

	    List<CulturalHeritageVo> list = new ArrayList<>();

	    String sql =
	        "SELECT "
	      + "content_id, title, addr1, first_image, "
	      + "map_x, map_y, area_code, sigungu_code, content_type_id, "
	      + "use_time, rest_date, parking, info_center "
	      + "FROM cultural_heritage "
	      + "WHERE "
	      + "(use_time IS NULL OR use_time = '') "
	      + "AND (rest_date IS NULL OR rest_date = '') "
	      + "AND (parking IS NULL OR parking = '') "
	      + "AND (info_center IS NULL OR info_center = '') "
	      + "ORDER BY content_id "
	      + "LIMIT ?";

	    try (
	        Connection con = DBCPUtil.getConnection();
	        PreparedStatement pstmt = con.prepareStatement(sql)
	    ) {

	        pstmt.setInt(1, limit);

	        try (ResultSet rs = pstmt.executeQuery()) {

	            while (rs.next()) {

	                CulturalHeritageVo vo =
	                    new CulturalHeritageVo();

	                vo.setContentId(
	                    String.valueOf(
	                        rs.getInt("content_id")
	                    )
	                );

	                vo.setTitle(
	                    rs.getString("title")
	                );

	                vo.setAddr1(
	                    rs.getString("addr1")
	                );

	                vo.setFirstImage(
	                    rs.getString("first_image")
	                );

	                vo.setMapX(
	                    rs.getString("map_x")
	                );

	                vo.setMapY(
	                    rs.getString("map_y")
	                );

	                vo.setAreaCode(
	                    rs.getString("area_code")
	                );

	                vo.setSigunguCode(
	                    rs.getString("sigungu_code")
	                );

	                vo.setContentTypeId(
	                    rs.getString("content_type_id")
	                );

	                vo.setUseTime(
	                    rs.getString("use_time")
	                );

	                vo.setRestDate(
	                    rs.getString("rest_date")
	                );

	                vo.setParking(
	                    rs.getString("parking")
	                );

	                vo.setInfoCenter(
	                    rs.getString("info_center")
	                );

	                list.add(vo);
	            }
	        }

	        System.out.println(
	            "[CulturalHeritageDao] 상세정보 미등록 문화유산 조회 : "
	            + list.size()
	            + "건"
	        );

	    } catch (Exception e) {

	        System.err.println(
	            "[CulturalHeritageDao] 상세정보 미등록 문화유산 조회 실패"
	        );

	        e.printStackTrace();
	    }

	    return list;
	}
	
	// =========================================================
	// 주제: updateIntroInfo 처리
	// 설명: 기존 데이터를 입력값에 맞게 수정
	// =========================================================
	public int updateIntroInfo(
	        String contentId,
	        String useTime,
	        String restDate,
	        String parking,
	        String infoCenter) {

	    String sql =
	        "UPDATE cultural_heritage "
	      + "SET use_time = ?, "
	      + "rest_date = ?, "
	      + "parking = ?, "
	      + "info_center = ? "
	      + "WHERE content_id = ?";

	    try (
	        Connection con = DBCPUtil.getConnection();
	        PreparedStatement pstmt = con.prepareStatement(sql)
	    ) {

	        pstmt.setString(1, useTime);
	        pstmt.setString(2, restDate);
	        pstmt.setString(3, parking);
	        pstmt.setString(4, infoCenter);
	        pstmt.setInt(5, Integer.parseInt(contentId));

	        int result = pstmt.executeUpdate();

	        System.out.println(
	            "[CulturalHeritageDao] 상세정보 업데이트 성공 : "
	            + contentId
	        );

	        return result;

	    } catch (Exception e) {

	        System.err.println(
	            "[CulturalHeritageDao] 상세정보 업데이트 실패 : "
	            + contentId
	        );

	        e.printStackTrace();

	        return 0;
	    }
	}

	// =========================================================
	// 좌표값 처리
	//
	// TourAPI에서 mapX/mapY가 빈 문자열로 들어오는 경우
	// DB에는 NULL을 저장한다.
	// =========================================================
	// =========================================================
	// 주제: setDoubleOrNull 처리
	// 설명: 객체의 값을 설정
	// =========================================================
	private void setDoubleOrNull(PreparedStatement pstmt, int index, String value) throws SQLException {

		if (value == null || value.trim().isEmpty()) {

			pstmt.setNull(index, java.sql.Types.DECIMAL);

			return;
		}

		try {

			pstmt.setDouble(index, Double.parseDouble(value));

		} catch (NumberFormatException e) {

			pstmt.setNull(index, java.sql.Types.DECIMAL);
		}
	}
	

// =========================================================
// 주제: regionCondition 처리
// 설명: regionCondition 기능에 필요한 처리 로직을 수행
// =========================================================
private String regionCondition(String region) {

    if (region == null || region.isBlank()) {
        return "1=1";
    }

    switch (region) {
        case "서울":
            return "addr1 LIKE '서울%'";
        case "경기인천":
            return "(addr1 LIKE '경기%' OR addr1 LIKE '인천%')";
        case "강원":
            return "addr1 LIKE '강원%'";
        case "충청":
            return "(addr1 LIKE '충북%' OR addr1 LIKE '충남%' "
                 + "OR addr1 LIKE '충청%' OR addr1 LIKE '대전%' "
                 + "OR addr1 LIKE '세종%')";
        case "전라":
            return "(addr1 LIKE '전북%' OR addr1 LIKE '전남%' "
                 + "OR addr1 LIKE '전라%' OR addr1 LIKE '광주%')";
        case "경상":
            return "(addr1 LIKE '경북%' OR addr1 LIKE '경남%' "
                 + "OR addr1 LIKE '경상%' OR addr1 LIKE '대구%' "
                 + "OR addr1 LIKE '부산%' OR addr1 LIKE '울산%')";
        case "제주":
            return "addr1 LIKE '제주%'";
        default:
            return "1=0";
    }
}

// 검색 조건에 해당하는 문화유산 개수
// =========================================================
// 주제: countFiltered 처리
// 설명: countFiltered 기능에 필요한 처리 로직을 수행
// =========================================================
public int countFiltered(String keyword, String region)
        throws SQLException {

    String sql =
        "SELECT COUNT(*) FROM cultural_heritage "
      + "WHERE title LIKE ? AND " + regionCondition(region);

    try (Connection con = DBCPUtil.getConnection();
         PreparedStatement pstmt = con.prepareStatement(sql)) {

        pstmt.setString(1, "%" +
            (keyword == null ? "" : keyword.trim()) + "%");

        try (ResultSet rs = pstmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}

// 검색 + 지역 + 정렬 + 페이지네이션
// =========================================================
// 주제: findFilteredPage 처리
// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
// =========================================================
public List<CulturalHeritageVo> findFilteredPage(
        String keyword,
        String region,
        String sort,
        int pageNo,
        int pageSize,
        String memberId) throws SQLException {

    List<CulturalHeritageVo> list = new ArrayList<>();

    String orderBy;

    if ("name".equals(sort)) {
        orderBy =
            " ORDER BY "
          + "CASE "
          + "WHEN TRIM(h.title) REGEXP '^[0-9]' THEN 0 "
          + "WHEN TRIM(h.title) REGEXP '^[가-힣]' THEN 1 "
          + "ELSE 2 END ASC, "
          + "h.title COLLATE utf8mb4_bin ASC, "
          + "h.content_id ASC";
    } else if ("liked".equals(sort) && memberId != null) {
        orderBy =
            " ORDER BY CASE WHEN EXISTS ("
          + "SELECT 1 FROM cultural_like cl "
          + "WHERE cl.heritage_id = h.content_id "
          + "AND cl.member_id = ?) "
          + "THEN 1 ELSE 0 END DESC, "
          + "h.title ASC, h.content_id ASC";
    } else {
        orderBy = " ORDER BY h.content_id DESC";
    }

    String sql =
        "SELECT h.* FROM cultural_heritage h "
      + "WHERE h.title LIKE ? AND "
      + regionCondition(region)
      + orderBy
      + " LIMIT ? OFFSET ?";

    try (Connection con = DBCPUtil.getConnection();
         PreparedStatement pstmt = con.prepareStatement(sql)) {

        int index = 1;

        pstmt.setString(index++, "%" +
            (keyword == null ? "" : keyword.trim()) + "%");

        if ("liked".equals(sort) && memberId != null) {
            pstmt.setString(index++, memberId);
        }

        pstmt.setInt(index++, pageSize);
        pstmt.setInt(index++, Math.max(0, pageNo - 1) * pageSize);

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {

                CulturalHeritageVo vo = new CulturalHeritageVo();

                vo.setContentId(
                    String.valueOf(rs.getInt("content_id")));
                vo.setTitle(rs.getString("title"));
                vo.setAddr1(rs.getString("addr1"));
                vo.setFirstImage(rs.getString("first_image"));
                vo.setMapX(rs.getString("map_x"));
                vo.setMapY(rs.getString("map_y"));
                vo.setAreaCode(rs.getString("area_code"));
                vo.setSigunguCode(rs.getString("sigungu_code"));
                vo.setContentTypeId(rs.getString("content_type_id"));
                vo.setUseTime(rs.getString("use_time"));
                vo.setRestDate(rs.getString("rest_date"));
                vo.setParking(rs.getString("parking"));
                vo.setInfoCenter(rs.getString("info_center"));

                list.add(vo);
            }
        }
    }

    return list;
}

	
}
