package in.coempt.service.impl;

import in.coempt.entity.BitwiseQuestions;
import in.coempt.repository.BitWiseQuestionsRepository;
import in.coempt.service.BitwiseQuestionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BitwiseQuestionsServiceImpl implements BitwiseQuestionsService {
    @Autowired
  private BitWiseQuestionsRepository bitWiseQuestionsRepository;
    @Override
    public List<BitwiseQuestions> getQpTemplateQuestions(Long setterId, String subjectId, int setNo) {
        return bitWiseQuestionsRepository.findByQpSetterIdAndSubjectIdAndSetNo(setterId,Integer.parseInt(subjectId),setNo);
    }

    @Override
    public void saveBitWiseQuestions(List<BitwiseQuestions> questionsList) {
        bitWiseQuestionsRepository.saveAll(questionsList);
    }
}
