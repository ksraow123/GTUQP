package in.coempt.repository;

import in.coempt.entity.Subjects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectsRepository  extends JpaRepository<Subjects, Long> {
   // @Query(value = "SELECT * FROM tbl_subjects WHERE course_id = ?1", nativeQuery = true)
    List<Subjects> findByCourseIdAndRegulation(String courseId,String regulation);

    Subjects findBySubjectCode(String subjectCode);

    List<Subjects> findByCourseId(String courseId);
}
