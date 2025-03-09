package in.coempt.repository;

import in.coempt.entity.QPTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QPTemplateRepository extends JpaRepository<QPTemplate,Long>{


    List<QPTemplate> findBySubjectId(int subjectId);
}
