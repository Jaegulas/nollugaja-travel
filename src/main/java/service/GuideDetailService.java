package service;

import java.sql.SQLException;
import java.util.Map;
import dao.GuideDetailDao;

// =========================================================
// 주제: 파일 역할
// 설명: GuideDetailService의 핵심 비즈니스 로직과 데이터 가공을 담당하는 서비스
// =========================================================


public class GuideDetailService {
    private final GuideDetailDao dao = new GuideDetailDao();
    public Map<String, Object> getGuide(int guideId) throws SQLException {
        return dao.findById(guideId);
    }
}
