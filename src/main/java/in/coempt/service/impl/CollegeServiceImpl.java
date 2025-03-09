package in.coempt.service.impl;

import in.coempt.entity.CollegeEntity;
import in.coempt.repository.CollegeRepository;
import in.coempt.service.CollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollegeServiceImpl implements CollegeService {
    @Autowired
    private CollegeRepository collegeRepository;
    @Override
    public List<CollegeEntity> getAllColleges() {
        return collegeRepository.findAll();
    }

    @Override
    public CollegeEntity getCollegeById(String collegeId) {
        return collegeRepository.findById(Long.parseLong(collegeId)).get();
    }

    @Override
    public CollegeEntity getCollegeByCode(String collegeCode) {
        return collegeRepository.findByCollegeCode(collegeCode);
    }
}
