package in.coempt.service.impl;

import in.coempt.entity.ExamSeries;
import in.coempt.repository.ExamSeriesRepository;
import in.coempt.service.ExamSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamSeriesServiceImpl implements ExamSeriesService {
    @Autowired
    private ExamSeriesRepository examSeriesRepository;

    @Override
    public List<ExamSeries> getAllExamSeries() {
        return examSeriesRepository.findAll();
    }

    @Override
    public ExamSeries getExamSeriesById(Integer id) {
        return examSeriesRepository.findById(id).orElse(null);
    }

    @Override
    public ExamSeries saveExamSeries(ExamSeries examSeries) {
        return examSeriesRepository.save(examSeries);
    }

    @Override
    public void deleteExamSeries(Integer id) {
        examSeriesRepository.deleteById(id);
    }
}
