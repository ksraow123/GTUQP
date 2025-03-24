package in.coempt.service.impl;

import in.coempt.entity.SectionsMasterEntity;
import in.coempt.repository.SectionsMasterRepository;
import in.coempt.service.SectionMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SectionMasterServiceImpl implements SectionMasterService {
    private final SectionsMasterRepository sectionsMasterRepository;

    @Override
    public SectionsMasterEntity getMappingDetailsById(Integer id) {
        return sectionsMasterRepository.findById(id).get();
    }
}
