package in.coempt.service;


import in.coempt.entity.BitwiseQuestions;
import in.coempt.entity.QPTemplate;

import java.util.List;

public interface QPTemplateService {
    List<QPTemplate> getQuestionTemplate(int subjectId);

    List<BitwiseQuestions> getBitWiseQuestionTemplate(int i);

    BitwiseQuestions getQuestionDetailsById(Long qid);

    void saveQuestion(BitwiseQuestions question);

    BitwiseQuestions getQuestionById(Long id);

    List<BitwiseQuestions> getQuestionsList(String subjectId, int setNo,Long setterid);

    void saveAllQuestions(List<BitwiseQuestions> questionsList);

    List<BitwiseQuestions> getBitWiseQuestionReviewerTemplate(int subjectId, int id);

    List<BitwiseQuestions> getReviewerQuestionsList(String subjectId, int setNo, Long reviewerId);
}
