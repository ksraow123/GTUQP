package in.coempt.repository;

import in.coempt.entity.QPFilesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QPFilesRepository extends JpaRepository<QPFilesEntity,Long> {


    QPFilesEntity findByUserIdAndSubjectIdAndSetNoAndQpSetterId(Long userId, long subjectId, int setNo,Long setterId);

    QPFilesEntity findByRollIdAndSubjectIdAndSetNoAndQpSetterId(int roleId, long  subjectId, int setNo,Long setterId);
}
