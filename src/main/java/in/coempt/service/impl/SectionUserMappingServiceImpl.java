package in.coempt.service.impl;

import in.coempt.entity.SectionUserMappingEntity;
import in.coempt.repository.SectionUserMappingRepository;
import in.coempt.service.SectionUserMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SectionUserMappingServiceImpl implements SectionUserMappingService {
    private final SectionUserMappingRepository userMappingRepository;
    @Override
    public SectionUserMappingEntity getMappingDetailsByUserid(int userId) {
        return userMappingRepository.findByUserId(userId);
    }

    @Override
    public SectionUserMappingEntity getMappingDetailsBySectionIdAndRoleId(int sectionId, int roleId) {
        return userMappingRepository.findBySectionIdAndRoleId(sectionId,roleId);
    }
}
