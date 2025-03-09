package in.coempt.service;

import in.coempt.entity.SetterModeratorMapping;
import in.coempt.vo.SetterModeratorMappingVo;

import java.util.List;

public interface SetterModeratorService {
    SetterModeratorMapping getReviewerDetails(Long setterId, Long subjectId);
    List<SetterModeratorMappingVo> getModeratorSetterMappingDetails();
}
