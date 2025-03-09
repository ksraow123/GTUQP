package in.coempt.repository;

import in.coempt.entity.ProfileDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileDetailsRepository extends JpaRepository<ProfileDetailsEntity, Long> {



}
