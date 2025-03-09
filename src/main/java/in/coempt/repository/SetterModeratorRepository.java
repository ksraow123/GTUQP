package in.coempt.repository;

import in.coempt.entity.SetterModeratorMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SetterModeratorRepository extends JpaRepository<SetterModeratorMapping, Long> {
    SetterModeratorMapping findBySetterIdAndSubjectId(Long setterId, Long subjectId);
}
