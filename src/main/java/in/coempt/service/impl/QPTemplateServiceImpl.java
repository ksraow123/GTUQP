package in.coempt.service.impl;

import in.coempt.entity.BitwiseQuestions;
import in.coempt.entity.QPTemplate;
import in.coempt.repository.BitWiseQuestionsRepository;
import in.coempt.repository.QPTemplateRepository;
import in.coempt.service.QPTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QPTemplateServiceImpl implements QPTemplateService {
    @Autowired
    QPTemplateRepository qpTemplateRepository;

    @Autowired
    BitWiseQuestionsRepository bitWiseQuestionsRepository;

    @Override
    public List<QPTemplate> getQuestionTemplate(int subjectId) {
        return qpTemplateRepository.findBySubjectId(subjectId);
    }

    @Override
    public List<BitwiseQuestions> getBitWiseQuestionTemplate(int subjectId) {
        return  bitWiseQuestionsRepository.findBySubjectId(subjectId);
    }

    @Override
    public BitwiseQuestions getQuestionDetailsById(Long qid) {
        return  bitWiseQuestionsRepository.findById(qid).get();
    }

    @Override
    public void saveQuestion(BitwiseQuestions question) {
         bitWiseQuestionsRepository.save(question);
    }

    @Override
    public BitwiseQuestions getQuestionById(Long id) {
       return bitWiseQuestionsRepository.findById(id).get();
    }

    @Override
    public List<BitwiseQuestions> getQuestionsList(String subjectId,int setNo,Long setterid) {
        return bitWiseQuestionsRepository.findByQpSetterIdAndSubjectIdAndSetNo(setterid,Integer.parseInt(subjectId),setNo);
    }

    @Override
    public void saveAllQuestions(List<BitwiseQuestions> questionsList) {
        bitWiseQuestionsRepository.saveAll(questionsList); // ✅ Batch update
        }

    @Override
    public List<BitwiseQuestions> getBitWiseQuestionReviewerTemplate(int subjectId, int qpReviewerId) {
        return bitWiseQuestionsRepository.findBySubjectIdAndQpReviewerId(subjectId,qpReviewerId);
    }

    @Override
    public List<BitwiseQuestions> getReviewerQuestionsList(String subjectId, int setNo, Long reviewerId) {
        return bitWiseQuestionsRepository.findBySubjectIdAndQpReviewerIdAndSetNo(Integer.parseInt(subjectId), Math.toIntExact(reviewerId),setNo);
    }
}
