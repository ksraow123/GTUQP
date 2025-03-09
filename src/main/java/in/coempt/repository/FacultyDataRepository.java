package in.coempt.repository;


import in.coempt.entity.FacultyData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FacultyDataRepository extends JpaRepository<FacultyData, Integer> {
    Optional<FacultyData> findByMobileNumber(String mobileNumber);
}

