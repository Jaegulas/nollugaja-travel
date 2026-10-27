package service;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dao.FoodDao;
import dto.FoodDto;
import util.TourApiUtil;

// =========================================================
// 주제: 파일 역할
// 설명: FoodService의 핵심 비즈니스 로직과 데이터 가공을 담당하는 서비스
// =========================================================


public class FoodService{
 private FoodDao foodDAO=new FoodDao();

 //=========================================================
 //주제: 주변 음식점 조회
 //설명: TourAPI 기본 목록 + DB 기본정보만 병합
 //=========================================================
 public List<FoodDto> getNearbyFoods(double latitude,double longitude){
  List<FoodDto> foodList=new ArrayList<>();
  JsonElement itemElement=TourApiUtil.getNearbyFoods(latitude,longitude);
  if(itemElement==null||itemElement.isJsonNull())return foodList;
  if(itemElement.isJsonArray()){
   JsonArray items=itemElement.getAsJsonArray();
   for(JsonElement element:items){
    FoodDto food=convertToFoodDTO(element);
    mergeDbBasic(food);
    fixCategory(food);
    foodList.add(food);
   }
  }else if(itemElement.isJsonObject()){
   FoodDto food=convertToFoodDTO(itemElement);
   mergeDbBasic(food);
   fixCategory(food);
   foodList.add(food);
  }
  return foodList;
 }

 //=========================================================
 //주제: 기존 호출 호환용
 //설명: 혹시 다른 코드에서 getFoodList를 부르면 같은 결과 반환
 //=========================================================
 public List<FoodDto> getFoodList(double latitude,double longitude){
  return getNearbyFoods(latitude,longitude);
 }

 //=========================================================
 //주제: JSON 음식점 1개를 FoodDto로 변환
 //설명: 현재 food 테이블에 남은 기본 값만 사용
 //=========================================================
 private FoodDto convertToFoodDTO(JsonElement element){
  JsonObject item=element.getAsJsonObject();
  FoodDto food=new FoodDto();
  String title=getString(item,"title");
  String cat3=getString(item,"cat3");
  food.setContent_id(getString(item,"contentid"));
  food.setF_name(title);
  food.setF_category(convertCategoryName(cat3,title));
  food.setAddress(getString(item,"addr1"));
  String apiImage=getString(item,"firstimage");
  if(isEmpty(apiImage))apiImage=getString(item,"firstimage2");
  food.setImage(apiImage);
  food.setPhone(getString(item,"tel"));
  food.setLatitude(getDouble(item,"mapy"));
  food.setLongitude(getDouble(item,"mapx"));
  food.setDistance(getDouble(item,"dist"));
  return food;
 }

 //=========================================================
 //주제: DB 기본정보 병합
 //설명: DB에 저장된 이미지/전화번호/주소/카테고리만 보정
 //=========================================================
 private void mergeDbBasic(FoodDto food){
  if(food==null||isEmpty(food.getContent_id()))return;
  FoodDto dbFood=foodDAO.selectDetailByContentId(food.getContent_id());
  if(dbFood==null)return;
  
//=========================================================
//주제: DB 음식점 ID 연결
//설명: API 음식점과 DB에 저장된 음식점의 food_id 연결
//=========================================================
food.setFood_id(dbFood.getFood_id());
  
  if(!isEmpty(dbFood.getF_category())){
   String dbCategory=dbFood.getF_category().trim();
   if(dbCategory.startsWith("A05"))dbCategory=convertCategoryName(dbCategory,food.getF_name());
   food.setF_category(dbCategory);
  }
  if(!isEmpty(dbFood.getAddress()))food.setAddress(dbFood.getAddress());
  if(!isEmpty(dbFood.getPhone()))food.setPhone(dbFood.getPhone());
  if(isEmpty(food.getImage())&&!isEmpty(dbFood.getImage()))food.setImage(dbFood.getImage());
  if(dbFood.getLatitude()!=0)food.setLatitude(dbFood.getLatitude());
  if(dbFood.getLongitude()!=0)food.setLongitude(dbFood.getLongitude());
 }

 //=========================================================
 //주제: 카테고리 최종 정리
 //설명: A050 코드가 화면에 그대로 나오지 않게 변환
 //=========================================================
 private void fixCategory(FoodDto food){
  if(food==null)return;
  String category=food.getF_category();
  if(isEmpty(category)){food.setF_category("맛집");return;}
  category=category.trim();
  if(category.startsWith("A05"))food.setF_category(convertCategoryName(category,food.getF_name()));
 }

 //=========================================================
 //주제: 음식점 카테고리 변환
 //설명: 이름 기반으로 세부 분류를 보기 좋게 표시
 //=========================================================
 private String convertCategoryName(String cat3,String title){
  String safeTitle=title!=null?title:"";
  if(safeTitle.contains("밀면"))return "한식 · 밀면";
  if(safeTitle.contains("국밥"))return "한식 · 국밥";
  if(safeTitle.contains("냉면"))return "한식 · 냉면";
  if(safeTitle.contains("완당"))return "한식 · 완당";
  if(safeTitle.contains("갈비")||safeTitle.contains("한우")||safeTitle.contains("고기"))return "한식 · 고기";
  if(safeTitle.contains("치킨")||safeTitle.contains("통닭"))return "치킨";
  if(safeTitle.contains("초밥")||safeTitle.contains("스시"))return "일식 · 초밥";
  if(safeTitle.contains("라멘")||safeTitle.contains("우동"))return "일식 · 면";
  if(safeTitle.contains("카페")||safeTitle.contains("커피"))return "카페 · 디저트";
  if(safeTitle.contains("빵")||safeTitle.contains("베이커리"))return "카페 · 베이커리";
  if(cat3==null)return "맛집";
  switch(cat3){
   case "A05020100":return "한식";
   case "A05020200":return "양식 · 분식";
   case "A05020300":return "일식";
   case "A05020400":return "중식";
   case "A05020500":return "아시아 음식";
   case "A05020600":return "채식 전문점";
   case "A05020700":return "이색 음식점";
   case "A05020900":return "카페 · 디저트";
   default:return "맛집";
  }
 }

 // =========================================================
 // 주제: getString 처리
 // 설명: 조건에 맞는 데이터를 조회하여 호출한 코드에 반환
 // =========================================================
 private String getString(JsonObject item,String key){
  if(item==null||!item.has(key)||item.get(key).isJsonNull())return "";
  return item.get(key).getAsString();
 }

 private double getDouble(JsonObject item,String key){
  if(item==null||!item.has(key)||item.get(key).isJsonNull())return 0.0;
  try{return item.get(key).getAsDouble();}catch(Exception e){return 0.0;}
 }

 // =========================================================
 // 주제: isEmpty 처리
 // 설명: 입력값 또는 현재 상태가 조건에 맞는지 검사
 // =========================================================
 private boolean isEmpty(String text){
  return text==null||text.trim().isEmpty()||"null".equalsIgnoreCase(text.trim())||"undefined".equalsIgnoreCase(text.trim());
 }
}
