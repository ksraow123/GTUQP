package in.coempt.repository;

import in.coempt.entity.QpTemplateMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QpTemplateMasterRepository extends JpaRepository<QpTemplateMaster,Integer> {
    List<QpTemplateMaster> findBySubjectId(Integer subjectId);
}
