package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import dao.QnaDao;
import vo.QnaQuestionVo;

// =========================================================
// 주제: 파일 역할
// 설명: QnaController 요청을 받아 서비스/DAO와 화면 또는 JSON 응답을 연결하는 컨트롤러
// =========================================================


//=========================================================
//주제: Q&A 게시판 컨트롤러
//설명: /Qna 목록, 검색, 질문/답변 작성·수정·삭제
//=========================================================
@WebServlet("/Qna")
public class QnaController extends HttpServlet {
 private static final long serialVersionUID=1L;
 private final QnaDao dao=new QnaDao();
 private static final String[] CATEGORIES={"여행코스","교통·이동","맛집","숙소","관광지","기타"};
 private static String s(String v){return v==null?"":v.trim();}
 // =========================================================
 // 주제: num 처리
 // 설명: num 기능에 필요한 처리 로직을 수행
 // =========================================================
 private static int num(String v,int def){try{return Integer.parseInt(v);}catch(Exception e){return def;}}
 private static long lng(String v){try{return Long.parseLong(v);}catch(Exception e){return -1;}}
 private static boolean validCategory(String s){for(String c:CATEGORIES)if(c.equals(s))return true;return false;}
 private static String sessionUser(HttpSession session){
  if(session==null)return null;
  Object id=session.getAttribute("id");if(id==null||s(String.valueOf(id)).isEmpty())id=session.getAttribute("loginId");
  return id==null?null:s(String.valueOf(id));
 }
 // =========================================================
 // 주제: admin 처리
 // 설명: admin 기능에 필요한 처리 로직을 수행
 // =========================================================
 private static boolean admin(HttpSession session){return session!=null&&("ADMIN".equals(session.getAttribute("role"))||Boolean.TRUE.equals(session.getAttribute("isAdmin")));}
 private static String token(HttpSession session){
  String t=(String)session.getAttribute("qnaCsrf");if(t==null){t=UUID.randomUUID().toString();session.setAttribute("qnaCsrf",t);}return t;
 }
 private void redirect(HttpServletRequest req,HttpServletResponse resp,String notice,long open)throws IOException{
  String url=req.getContextPath()+"/Qna?notice="+notice+(open>0?"&open="+open+"#qna-"+open:"");resp.sendRedirect(url);
 }
 @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
  req.setCharacterEncoding("UTF-8");HttpSession session=req.getSession(true);
  String cat=s(req.getParameter("category"));if(!validCategory(cat))cat="";
  String status=s(req.getParameter("status"));if(!"waiting".equals(status)&&!"answered".equals(status))status="all";
  String sort=s(req.getParameter("sort"));if(!"popular".equals(sort)&&!"answers".equals(sort))sort="latest";
  String keyword=s(req.getParameter("keyword"));if(keyword.length()>80)keyword=keyword.substring(0,80);
  int page=Math.max(1,Math.min(10000,num(req.getParameter("page"),1))),size=8;
  try{
   int total=dao.count(cat,status,keyword);int pages=Math.max(1,(total+size-1)/size);page=Math.min(page,pages);
   List<QnaQuestionVo> questions=dao.list(cat,status,keyword,sort,page,size);int[] stats=dao.stats();
   req.setAttribute("qnaQuestions",questions);req.setAttribute("qnaTotal",total);req.setAttribute("qnaTotalPages",pages);
   req.setAttribute("qnaPage",page);req.setAttribute("qnaPages",buildPages(page,pages));req.setAttribute("qnaStats",stats);
  }catch(SQLException e){getServletContext().log("[QnaController] DB 조회 오류",e);req.setAttribute("qnaDbError",true);
   req.setAttribute("qnaQuestions",Collections.emptyList());req.setAttribute("qnaTotal",0);req.setAttribute("qnaTotalPages",1);
   req.setAttribute("qnaPage",1);req.setAttribute("qnaPages",Arrays.asList(1));req.setAttribute("qnaStats",new int[]{0,0});
  }
  req.setAttribute("qnaCategory",cat);req.setAttribute("qnaStatus",status);req.setAttribute("qnaSort",sort);req.setAttribute("qnaKeyword",keyword);
  req.setAttribute("qnaCsrf",token(session));req.setAttribute("qnaMemberId",sessionUser(session));req.setAttribute("qnaAdmin",admin(session));
  req.setAttribute("qnaCategories",CATEGORIES);req.setAttribute("qnaOpen",lng(req.getParameter("open")));
  req.setAttribute("center","qna/qna.jsp");req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req,resp);
 }
 // =========================================================
 // 주제: buildPages 처리
 // 설명: 입력 데이터를 화면/API/DB에서 사용하기 좋은 형태로 변환 또는 가공
 // =========================================================
 private static List<Integer> buildPages(int page,int pages){List<Integer> result=new ArrayList<>();int begin=Math.max(1,page-2),end=Math.min(pages,begin+4);begin=Math.max(1,end-4);for(int p=begin;p<=end;p++)result.add(p);return result;}
 @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
  req.setCharacterEncoding("UTF-8");HttpSession session=req.getSession(false);
  String action=s(req.getParameter("action"));String expected=session==null?null:(String)session.getAttribute("qnaCsrf");
  if(expected==null||!expected.equals(req.getParameter("csrf"))){resp.sendError(403,"요청 인증 정보가 올바르지 않습니다.");return;}
  long id=lng(req.getParameter("id"));
  // 펼치기 조회수: 공개 글만 집계, 글 수정권한과는 무관
  if("view".equals(action)){
   try{resp.setStatus(dao.incrementView(id)==1?200:404);}catch(SQLException e){getServletContext().log("Q&A 조회수 변경 오류",e);resp.setStatus(500);}return;
  }
  String user=sessionUser(session);if(user==null||user.isEmpty()){resp.sendRedirect(req.getContextPath()+"/Login");return;}
  boolean isAdmin=admin(session);
  String category=s(req.getParameter("category"));String title=s(req.getParameter("title"));String content=s(req.getParameter("content"));
  try{
   if("ask".equals(action)){
    if(!validCategory(category)||title.isEmpty()||title.length()>160||content.isEmpty()||content.length()>5000){redirect(req,resp,"invalid",0);return;}
    long created=dao.create(user,category,title,content);redirect(req,resp,created>0?"created":"failed",created);return;
   }
   if("edit".equals(action)){
    if(id<1||!validCategory(category)||title.isEmpty()||title.length()>160||content.isEmpty()||content.length()>5000){redirect(req,resp,"invalid",id);return;}
    int updated=dao.update(id,user,isAdmin,category,title,content);redirect(req,resp,updated>0?"edited":"denied",id);return;
   }
   if("delete".equals(action)){
    int deleted=id>0?dao.delete(id,user,isAdmin):0;redirect(req,resp,deleted>0?"deleted":"denied",0);return;
   }
   if("answer".equals(action)){
    if(id<1||content.isEmpty()||content.length()>3000){redirect(req,resp,"invalid",id);return;}
    int added=dao.addAnswer(id,user,content);redirect(req,resp,added>0?"answered":"failed",id);return;
   }
   if("deleteAnswer".equals(action)){
    long answerId=lng(req.getParameter("answerId"));int deleted=answerId>0?dao.deleteAnswer(answerId,user,isAdmin):0;
    redirect(req,resp,deleted>0?"answerDeleted":"denied",id);return;
   }
  }catch(SQLException e){getServletContext().log("[QnaController] DB 저장 오류",e);redirect(req,resp,"failed",id);return;}
  resp.sendError(400,"올바르지 않은 요청입니다.");
 }
}
