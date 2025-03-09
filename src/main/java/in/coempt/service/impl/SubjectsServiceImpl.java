package in.coempt.service.impl;

import in.coempt.entity.Subjects;
import in.coempt.repository.SubjectsRepository;
import in.coempt.service.SubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectsServiceImpl implements SubjectsService {
    @Autowired
    private SubjectsRepository subjectsRepository;
    @Override
    public List<Subjects> getSubjectsByCourseIdAndRegulation(String courseId,String regulation) {
        return subjectsRepository.findByCourseIdAndRegulation(courseId,regulation);
    }
    @Override
    public Subjects getSubjectById(String subjectId) {
        return subjectsRepository.findById(Long.parseLong(subjectId)).get();
    }

    @Override
    public Subjects getSubject_code(String subjectCode) {
        return subjectsRepository.findBySubjectCode(subjectCode);
    }

    @Override
    public List<Subjects> getSubjectsByCourseId(String courseId) {
        return subjectsRepository.findByCourseId(courseId);
    }
}
