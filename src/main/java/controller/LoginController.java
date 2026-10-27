package controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.LoginDao;
import vo.LoginVo;

// =========================================================
// 주제: 파일 역할
// 설명: LoginController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


@WebServlet("/Login")
public class LoginController extends HttpServlet {

    private static final long serialVersionUID=1L;

    //=========================================================
    //주제: 로그인 후 복귀 주소 검사
    //설명: 외부 주소를 막고 현재 프로젝트 내부 주소만 허용
    //=========================================================
    private String validReturnTo(HttpServletRequest request,String url){
        if(url==null||url.length()>2048||url.contains("\\")||url.contains("\r")||url.contains("\n"))return null;
        try{
            URI uri=new URI(url);
            String path=uri.getRawPath();
            String contextPath=request.getContextPath();

            if(uri.isAbsolute()||uri.getRawAuthority()!=null||uri.getRawFragment()!=null||
               path==null||!path.startsWith(contextPath+"/")||
               path.startsWith(contextPath+"//")||
               path.equals(contextPath+"/Login")||
               path.matches("(?i).*%(?:2f|5c|2e|25).*"))return null;

            return url;
        }catch(URISyntaxException e){
            return null;
        }
    }

    //=========================================================
    //주제: 이전 페이지 주소 확인
    //설명: returnTo/redirect가 없어도 Referer에서 현재 프로젝트 주소를 복구
    //=========================================================
    private String getRefererReturnTo(HttpServletRequest request){
        String referer=request.getHeader("Referer");
        if(referer==null||referer.trim().isEmpty())return null;
        try{
            URI uri=new URI(referer);
            String path=uri.getRawPath();
            if(path==null)return null;
            String value=path;
            if(uri.getRawQuery()!=null&&!uri.getRawQuery().isEmpty())value+="?"+uri.getRawQuery();
            return validReturnTo(request,value);
        }catch(URISyntaxException e){
            return null;
        }
    }

    //=========================================================
    //주제: 로그인 화면 출력
    //설명: 로그인 성공 후 돌아갈 페이지를 세션에 저장
    //=========================================================
    @Override
    protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        System.out.println("[LoginController] 로그인 화면 요청");

        String returnTo=validReturnTo(request,request.getParameter("returnTo"));
        if(returnTo==null)returnTo=validReturnTo(request,request.getParameter("redirect"));
        if(returnTo==null)returnTo=getRefererReturnTo(request);
        if(returnTo!=null)request.getSession(true).setAttribute("loginReturnTo",returnTo);

        request.getRequestDispatcher("/WEB-INF/members/login.jsp").forward(request,response);
    }

    //=========================================================
    //주제: 로그인 처리
    //설명: 로그인 성공 후 이전 페이지가 있으면 복귀하고 없으면 기본 페이지 이동
    //=========================================================
    @Override
    protected void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        request.setCharacterEncoding("UTF-8");

        String id=request.getParameter("id");
        String passwd=request.getParameter("passwd");

        System.out.println("[LoginController] 로그인 요청");

        if(id==null||id.trim().isEmpty()||passwd==null||passwd.isEmpty()){
            request.setAttribute("loginError","아이디와 비밀번호를 모두 입력해주세요.");
            request.getRequestDispatcher("/WEB-INF/members/login.jsp").forward(request,response);
            return;
        }

        LoginVo loginVO=new LoginVo();
        loginVO.setId(id.trim());
        loginVO.setPass(passwd);

        LoginDao loginDAO=new LoginDao();

        try{
            LoginVo member=loginDAO.login(loginVO);
            System.out.println("[LoginController] DAO 조회 완료");

            if(member==null){
                System.out.println("[LoginController] 로그인 실패");
                request.setAttribute("loginError","아이디 또는 비밀번호가 일치하지 않습니다.");
                request.getRequestDispatcher("/WEB-INF/members/login.jsp").forward(request,response);
                return;
            }

            //=========================================================
            //주제: 기존 세션 값 보존
            //설명: 세션 교체 전에 공통 복귀 주소와 기존 필터 주소를 확보
            //=========================================================
            HttpSession oldSession=request.getSession(false);
            String savedReturnTo=null;
            String savedRedirectUrl=null;

            if(oldSession!=null){
                Object returnValue=oldSession.getAttribute("loginReturnTo");
                if(returnValue instanceof String)savedReturnTo=validReturnTo(request,(String)returnValue);

                Object redirectValue=oldSession.getAttribute("redirectUrl");
                if(redirectValue instanceof String)savedRedirectUrl=validReturnTo(request,(String)redirectValue);

                oldSession.invalidate();
            }

            HttpSession session=request.getSession(true);
            session.setAttribute("loginId",member.getId());
            session.setAttribute("loginName",member.getName());
            session.setAttribute("role",member.getRole());

            boolean isAdmin="ADMIN".equals(member.getRole());
            session.setAttribute("isAdmin",isAdmin);
            session.setMaxInactiveInterval(60*60);

            System.out.println("========== 로그인 성공 ==========");
            System.out.println("회원 ID : "+member.getId());
            System.out.println("회원 이름 : "+member.getName());
            System.out.println("세션 ID : "+session.getId());

            //=========================================================
            //주제: 로그인 성공 후 이동 우선순위
            //설명: 현재 페이지 → 기존 필터 경로 → 기본 페이지 순으로 이동
            //=========================================================
            String redirectUrl=savedReturnTo;
            if(redirectUrl==null||redirectUrl.trim().isEmpty())redirectUrl=savedRedirectUrl;

            if(redirectUrl==null||redirectUrl.trim().isEmpty()){
                redirectUrl=isAdmin
                        ?request.getContextPath()+"/Admin/admin_main"
                        :request.getContextPath()+"/Main";
            }

            System.out.println("[LoginController] 로그인 후 복귀 페이지 : "+redirectUrl);
            response.sendRedirect(redirectUrl);
            return;

        }catch(Exception e){
            System.err.println("[LoginController] 로그인 처리 오류");
            e.printStackTrace();
            request.setAttribute("loginError","로그인 처리 중 오류가 발생했습니다.");
            request.getRequestDispatcher("/WEB-INF/members/login.jsp").forward(request,response);
            return;
        }
    }
}
