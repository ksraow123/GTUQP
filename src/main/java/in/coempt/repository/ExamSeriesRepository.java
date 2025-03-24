package in.coempt.repository;

import in.coempt.entity.ExamSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamSeriesRepository extends JpaRepository<ExamSeries, Integer> {
    List<ExamSeries> findByIsActive(Integer b);
}
