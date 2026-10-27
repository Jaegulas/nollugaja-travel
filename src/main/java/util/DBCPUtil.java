package util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

// =========================================================
// 주제: 파일 역할
// 설명: DBCPUtil에서 공통으로 사용하는 변환·API·DB 보조 기능을 제공하는 유틸리티
// =========================================================


public class DBCPUtil {

	
	private static DataSource ds;
	
	static {
		
		
		try {
			Context ctx = new InitialContext();
			ds= (DataSource) ctx.lookup("java:comp/env/jdbc/travel");
		} catch (NamingException e) {
			System.out.println("[DBCPUtil] DataSource 객체 생성시 에러");
			e.printStackTrace();
		}
		
	}
	
	// =========================================================
	// 주제: getConnection 처리
	// 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	// =========================================================
	public static Connection getConnection()throws SQLException {
		return ds.getConnection();
	}
	
}
