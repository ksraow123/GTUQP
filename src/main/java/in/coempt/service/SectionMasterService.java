package in.coempt.service;


import in.coempt.entity.SectionUserMappingEntity;
import in.coempt.entity.SectionsMasterEntity;


public interface SectionMasterService {
    SectionsMasterEntity getMappingDetailsById(Integer id);
}
