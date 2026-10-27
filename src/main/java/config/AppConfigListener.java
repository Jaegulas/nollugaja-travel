package config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import util.DBCPUtil;

// =========================================================
// 주제: 파일 역할
// 설명: AppConfigListener 애플리케이션 설정과 초기화를 담당
// =========================================================


@WebListener
public class AppConfigListener implements ServletContextListener {

	    private static final Properties properties = new Properties();

	    // 서블릿용: 키 하나씩 꺼내 쓰기
	    // =========================================================
	    // 주제: getProperty 처리
	    // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
	    // =========================================================
	    public static String getProperty(String key) {
	        return properties.getProperty(key);
	    }

	    @Override
	    public void contextInitialized(ServletContextEvent event) {
	    	 System.out.println("[STARTUP] ========== 서버 시작 점검 ==========");
			System.out.println("AppConfigListener 리스너 실행됨");

			
			//클래스패스에서 읽기
	        //이클립스에선 되는데 리눅스에서 자주 실패
			//new FileInputStream("config.properties"); 이렇게 안됨
			
			try (InputStream in = getClass().getClassLoader()
	                .getResourceAsStream("config.properties")) { 
	        	


            if (in == null) {
            	System.out.println("[STARTUP][ERROR] config.properties 를 찾을 수 없음 (WEB-INF/classes 안에 있는지 확인)");
                throw new RuntimeException("config.properties 파일을 찾을 수 없습니다.");
                //throw (지금 코드): 리스너에서 예외가 나면 톰캣이 그 웹앱 배포 자체를 실패 처리해요. 접속하면 전부 404가 나고, 문제가 있다는 게 확실히 드러나요.	
            }
	            properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
	            System.out.println("[STARTUP] config.properties 로드 OK - 항목 " + properties.size() + "개");
	        } catch (IOException e) {
	            throw new RuntimeException("설정 파일 읽기 실패", e);
	        }

	        for (String key : properties.stringPropertyNames()) {
	            System.out.println("[STARTUP]   " + key + " = " + mask(key, properties.getProperty(key)));
	        }
	        
	        // 3) 꼭 있어야 하는 값 확인 → 실제 키 이름으로 바꾸세요
//	        String[] required = { "db.url", "db.user", "db.password", "kakao.api.key" };
//	        for (String key : required) {
//	            String value = properties.getProperty(key);
//	            if (value == null || value.trim().isEmpty()) {
//	                System.out.println("[STARTUP][ERROR] 필수 설정 없음: " + key);
//	            }
//	        }
	        
	        // DB 연결 테스트
	     // 4) 커넥션풀 연결 테스트
	        long start = System.currentTimeMillis();
	       
            try (Connection conn = DBCPUtil.getConnection()) {
	                System.out.println("[STARTUP] 커넥션풀 연결 OK - "
	                        + conn.getMetaData().getDatabaseProductName()
	                        + " / " + conn.getMetaData().getURL()
	                        + " (" + (System.currentTimeMillis() - start) + "ms)");
            } catch (Throwable e) {
	            System.out.println("[STARTUP][ERROR] DB 연결 실패: " + e);
	        }
            //Exception이 아니라 Throwable로 잡은 이유가 있어요. DBCPUtil이 static { } 블록에서 lookup을 하고 있다면, 
            //거기서 실패할 때 ExceptionInInitializerError라는 Error가 나서 catch (Exception e)로는 안 잡혀요. 
            //그러면 리스너 자체가 터지면서 원인 파악이 어려워져서 넓게 잡아 둔 거예요.

            //e.getMessage() 대신 e를 그대로 찍은 것도 같은 이유예요. 예외 종류 이름까지 같이 나와서 "이름을 못 찾음(NamingException)"인지 
            //“DB 접속 실패(SQLException)”인지 바로 구분돼요.
	        
	        
	        // JSP(→ JS)용: 화면에 나가도 되는 값만
	        event.getServletContext()
	             .setAttribute("kakaoJsKey", getProperty("kakao.api.js.key"));
	    }

	


	    @Override
	    // =========================================================
	    // 주제: contextDestroyed 처리
	    // 설명: contextDestroyed 기능에 필요한 처리 로직을 수행
	    // =========================================================
	    public void contextDestroyed(ServletContextEvent event) { }
	    
	    // 키, 비밀번호 등은 앞 4글자만 보여 주기
	    private String mask(String key, String value) {
	        if (value == null || value.isEmpty()) return "(비어 있음)";
	        String k = key.toLowerCase();
	        if (k.contains("key") || k.contains("secret") || k.contains("password") || k.contains("token")) {
	            if (value.length() <= 4) return "****";
	            return value.substring(0, 4) + "**** (" + value.length() + "자)";
	        }
	        return value;
	    }
	    

}
