package in.coempt.service;

import in.coempt.entity.Course;

import java.util.List;

public interface CourseService {
   public List<Course> getAllCourses();
   public Course getCourseDetailsById(String courseId);
}
