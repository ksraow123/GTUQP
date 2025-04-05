package in.coempt.repository;

import in.coempt.entity.FacultyData;
import in.coempt.entity.PatternInstructionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatternInstructionsRepository extends JpaRepository<PatternInstructionsEntity, Integer> {

        Optional<PatternInstructionsEntity> findByPatternCode(String patternCode);
    }



