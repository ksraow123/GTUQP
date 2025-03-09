package in.coempt.controller;

import in.coempt.entity.ExamSeries;
import in.coempt.service.ExamSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/examSeries")
public class ExamSeriesController {
    @Autowired
    private ExamSeriesService examSeriesService;

    @GetMapping
    public List<ExamSeries> getAllExamSeries() {
        return examSeriesService.getAllExamSeries();
    }

    @GetMapping("/{id}")
    public ExamSeries getExamSeriesById(@PathVariable Integer id) {
        return examSeriesService.getExamSeriesById(id);
    }

    @PostMapping
    public ExamSeries createExamSeries(@RequestBody ExamSeries examSeries) {
        return examSeriesService.saveExamSeries(examSeries);
    }

    @DeleteMapping("/{id}")
    public void deleteExamSeries(@PathVariable Integer id) {
        examSeriesService.deleteExamSeries(id);
    }
}
