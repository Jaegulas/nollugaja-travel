package dto;

// =========================================================
// 주제: 파일 역할
// 설명: FoodDto 화면/계층 사이에서 필요한 데이터를 전달하는 DTO
// =========================================================


//=========================================================
//주제: 맛집 DTO
//설명: 현재 food 테이블 컬럼 + 화면 계산용 distance만 관리
//=========================================================
public class FoodDto{
 private int food_id;
 private String content_id;
 private String f_name;
 private String f_category;
 private String address;
 private String image;
 private String phone;
 private double latitude;
 private double longitude;
 private double distance;

 public FoodDto(){}

 public FoodDto(int food_id,String content_id,String f_name,String f_category,String address,String image,String phone,double latitude,double longitude,double distance){
  this.food_id=food_id;
  this.content_id=content_id;
  this.f_name=f_name;
  this.f_category=f_category;
  this.address=address;
  this.image=image;
  this.phone=phone;
  this.latitude=latitude;
  this.longitude=longitude;
  this.distance=distance;
 }

 public int getFood_id(){return food_id;}
 public void setFood_id(int food_id){this.food_id=food_id;}

 public String getContent_id(){return content_id;}
 public void setContent_id(String content_id){this.content_id=content_id;}

 public String getF_name(){return f_name;}
 public void setF_name(String f_name){this.f_name=f_name;}

 public String getF_category(){return f_category;}
 public void setF_category(String f_category){this.f_category=f_category;}

 public String getAddress(){return address;}
 public void setAddress(String address){this.address=address;}

 public String getImage(){return image;}
 public void setImage(String image){this.image=image;}

 public String getPhone(){return phone;}
 public void setPhone(String phone){this.phone=phone;}

 public double getLatitude(){return latitude;}
 public void setLatitude(double latitude){this.latitude=latitude;}

 public double getLongitude(){return longitude;}
 public void setLongitude(double longitude){this.longitude=longitude;}

 public double getDistance(){return distance;}
 public void setDistance(double distance){this.distance=distance;}

 @Override
 public String toString(){
  return "FoodDto [food_id="+food_id+", content_id="+content_id+", f_name="+f_name+", f_category="+f_category+", address="+address+", image="+image+", phone="+phone+", latitude="+latitude+", longitude="+longitude+", distance="+distance+"]";
 }
}
