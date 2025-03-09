package in.coempt.repository;

import in.coempt.entity.QPFilesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QPFilesRepository extends JpaRepository<QPFilesEntity,Long> {


    QPFilesEntity findByUserIdAndSubjectIdAndSetNo(Long userId, long subjectId, int setNo);
}
