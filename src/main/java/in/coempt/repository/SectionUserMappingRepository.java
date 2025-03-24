package in.coempt.repository;

import in.coempt.entity.SectionUserMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionUserMappingRepository
        extends JpaRepository<SectionUserMappingEntity, Integer>  {

    SectionUserMappingEntity findByUserId(int userId);

    SectionUserMappingEntity findBySectionIdAndRoleId(int sectionId, int roleId);
}
