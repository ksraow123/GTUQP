package in.coempt.service;

import in.coempt.entity.Subjects;

import java.util.List;

public interface SubjectsService {

    public List<Subjects> getSubjectsByCourseIdAndRegulation(String courseId,String regulation);

    Subjects getSubjectById(String subjectId);

    Subjects getSubject_code(String subjectCode);

    List<Subjects> getSubjectsByCourseId(String courseId);

    List<Subjects> getSubjectsByCourseIdAndSectionId(String courseId, Integer sectionId);

    Subjects getSubjectCodeAndSectionId(String subjectCode, Integer sectionId);

    List<Subjects> getSubjectsByCourseIdList(List<String> courseIds);

    List<Subjects> getSubjectsByCourseIdAndSemester(String courseId, String semester);

    Subjects getSubjectCodeAndSectionIdAbdCourseId(String subjectCode, int sectionId, Long courseId);

    Subjects getSubject_codeAndCourseId(String subjectCode, Long courseId);
}
