package in.coempt.repository;

import in.coempt.entity.ExamSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamSeriesRepository extends JpaRepository<ExamSeries, Integer> {}
