package dto;

// =========================================================
// 주제: 파일 역할
// 설명: GuideTourCourseDto 화면/계층 사이에서 필요한 데이터를 전달하는 DTO
// =========================================================


public class GuideTourCourseDto {
    private int courseId;
    private int tourId;
    private int courseOrder;
    private String courseName;
    private String courseDescription;
    private int durationMinutes;

    public int getCourseId() { return courseId; }
    public void setCourseId(int value) { this.courseId = value; }
    public int getTourId() { return tourId; }
    public void setTourId(int value) { this.tourId = value; }
    public int getCourseOrder() { return courseOrder; }
    public void setCourseOrder(int value) { this.courseOrder = value; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String value) { this.courseName = value; }
    public String getCourseDescription() { return courseDescription; }
    public void setCourseDescription(String value) { this.courseDescription = value; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int value) { this.durationMinutes = value; }
}
