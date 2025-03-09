package in.coempt.repository;

import in.coempt.entity.SmsEmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsEmailTemplateRepository extends JpaRepository<SmsEmailTemplate,Long> {




}
