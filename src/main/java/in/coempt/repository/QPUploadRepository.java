package in.coempt.repository;

import in.coempt.entity.QpUploadDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QPUploadRepository extends JpaRepository<QpUploadDetailsEntity, Long> {


    List<QpUploadDetailsEntity> findBySubjectId(int subjectId);

    QpUploadDetailsEntity findBySubjectIdAndSetNo(int subjectId, int setNo);
}
