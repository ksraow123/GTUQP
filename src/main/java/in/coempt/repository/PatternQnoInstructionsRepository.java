package in.coempt.repository;

import in.coempt.entity.PatternQnoInstructions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PatternQnoInstructionsRepository extends JpaRepository<PatternQnoInstructions, Long> {
    List<PatternQnoInstructions> findByPatternCode(String patternCode);
}
