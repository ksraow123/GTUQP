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

    @Override
    public List<Subjects> getSubjectsByCourseIdAndSectionId(String courseId, Integer sectionId) {
        return subjectsRepository.findByCourseIdAndSectionId(courseId,sectionId);
    }

    @Override
    public Subjects getSubjectCodeAndSectionId(String subjectCode, Integer sectionId) {
        return subjectsRepository.findBySubjectCodeAndSectionId(subjectCode,sectionId);
    }

    @Override
    public List<Subjects> getSubjectsByCourseIdList(List<String> courseIds) {
        return subjectsRepository.findByCourseIdInOrderBySubjectCodeAsc(courseIds);
    }

    @Override
    public List<Subjects> getSubjectsByCourseIdAndSemester(String courseId, String semester) {
        return subjectsRepository.findByCourseIdAndSemester(courseId,semester);
    }

    @Override
    public Subjects getSubjectCodeAndSectionIdAbdCourseId(String subjectCode, int sectionId, Long courseId) {
        return subjectsRepository.findBySubjectCodeAndSectionIdAndCourseId(subjectCode,sectionId,courseId+"");
    }

    @Override
    public Subjects getSubject_codeAndCourseId(String subjectCode, Long courseId) {
        return subjectsRepository.findBySubjectCodeAndCourseId(subjectCode,courseId+"");
    }
    }

