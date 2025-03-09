package in.coempt.repository;

import in.coempt.entity.BitwiseQuestions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BitWiseQuestionsRepository extends JpaRepository<BitwiseQuestions,Long> {


    List<BitwiseQuestions> findBySubjectId(int subjectId);
    List<BitwiseQuestions> findBySubjectIdAndSetNo(int subjectId,int setNo);

    List<BitwiseQuestions> findBySubjectIdAndQpReviewerId(int subjectId, int qpReviewerId);

    List<BitwiseQuestions> findByQpSetterIdAndSubjectIdAndSetNo(Long setterId, int subjectId, int setNo);

    List<BitwiseQuestions> findBySubjectIdAndQpReviewerIdAndSetNo(int subjectId, int reviewerId, int setNo);
}
