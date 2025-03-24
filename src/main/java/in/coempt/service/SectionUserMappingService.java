package in.coempt.service;

import in.coempt.entity.SectionUserMappingEntity;

public interface SectionUserMappingService {
    SectionUserMappingEntity getMappingDetailsByUserid(int userId);

    SectionUserMappingEntity getMappingDetailsBySectionIdAndRoleId(int sectionId, int i);
}
