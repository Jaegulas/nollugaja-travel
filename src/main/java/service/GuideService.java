package service;

import java.sql.SQLException;
import java.util.List;
import dao.GuideDao;
import vo.GuideVO;

// =========================================================
// 주제: 파일 역할
// 설명: GuideService의 핵심 비즈니스 로직과 데이터 가공을 담당하는 서비스
// =========================================================


public class GuideService {
    private final GuideDao dao = new GuideDao();
    public int count(String region, String keyword) throws SQLException {
        return dao.count(region, keyword);
    }
    // =========================================================
    // 주제: findPage 처리
    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
    // =========================================================
    public List<GuideVO> findPage(String region, String keyword, int page, int size) throws SQLException {
        return dao.findPage(region, keyword, page, size);
    }
}
