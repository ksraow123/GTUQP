package in.coempt.repository;

import in.coempt.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>{

    Course findByCourseCode(String courseCode);
}
