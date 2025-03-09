package in.coempt.service;

import in.coempt.entity.Subjects;

import java.util.List;

public interface SubjectsService {

    public List<Subjects> getSubjectsByCourseIdAndRegulation(String courseId,String regulation);

    Subjects getSubjectById(String subjectId);

    Subjects getSubject_code(String subjectCode);

    List<Subjects> getSubjectsByCourseId(String courseId);
}
