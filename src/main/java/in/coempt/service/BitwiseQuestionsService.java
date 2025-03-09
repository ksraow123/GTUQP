package in.coempt.service;

import in.coempt.entity.BitwiseQuestions;

import java.util.List;

public interface BitwiseQuestionsService {
    List<BitwiseQuestions> getQpTemplateQuestions(Long setterId, String subjectId, int setNo);

    void saveBitWiseQuestions(List<BitwiseQuestions> questionsList);
}
