package in.coempt.repository;

import in.coempt.entity.ProfileDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileDetailsRepository extends JpaRepository<ProfileDetailsEntity, Long> {


    ProfileDetailsEntity findByUserId(Long userId);

    Optional<ProfileDetailsEntity> findByContact(String mobileNumber);
}
