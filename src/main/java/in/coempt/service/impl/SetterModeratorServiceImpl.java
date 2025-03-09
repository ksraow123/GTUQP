package in.coempt.service.impl;

import in.coempt.dao.SetterModeratorMappingDao;
import in.coempt.entity.SetterModeratorMapping;
import in.coempt.repository.SetterModeratorRepository;
import in.coempt.service.SetterModeratorService;
import in.coempt.vo.SetterModeratorMappingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetterModeratorServiceImpl implements SetterModeratorService {
    @Autowired
    private SetterModeratorRepository moderatorRepository;

    @Autowired
    private SetterModeratorMappingDao setterModeratorMappingDao;

    @Override
    public SetterModeratorMapping getReviewerDetails(Long setterId, Long subjectId) {
        return moderatorRepository.findBySetterIdAndSubjectId(setterId,subjectId);
    }

    @Override
    public List<SetterModeratorMappingVo> getModeratorSetterMappingDetails() {
        return setterModeratorMappingDao.setterModeratorMappingVoList();
    }
}
