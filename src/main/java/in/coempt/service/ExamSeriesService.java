package in.coempt.service;

import in.coempt.entity.ExamSeries;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ExamSeriesService {
    List<ExamSeries> getAllExamSeries();
    ExamSeries getExamSeriesById(Integer id);
    ExamSeries saveExamSeries(ExamSeries examSeries);
    void deleteExamSeries(Integer id);
}
