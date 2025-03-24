package in.coempt.repository;

import in.coempt.entity.QpUploadDetailsEntity;
import in.coempt.entity.SectionUserMappingEntity;
import in.coempt.entity.SectionsMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionsMasterRepository extends JpaRepository<SectionsMasterEntity, Integer> {

}
