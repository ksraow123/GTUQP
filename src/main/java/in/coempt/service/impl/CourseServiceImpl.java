package in.coempt.service.impl;

import in.coempt.entity.Course;
import in.coempt.repository.CourseRepository;
import in.coempt.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public Course getCourseDetailsById(String courseId) {
        return courseRepository.findById(Long.parseLong(courseId)).get();
    }
}
