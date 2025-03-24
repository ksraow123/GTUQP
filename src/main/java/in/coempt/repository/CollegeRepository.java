package in.coempt.repository;


import in.coempt.entity.CollegeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollegeRepository extends JpaRepository<CollegeEntity,Long> {
    CollegeEntity findByCollegeCode(String collegeCode);
}
