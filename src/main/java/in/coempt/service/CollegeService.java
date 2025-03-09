package in.coempt.service;

import in.coempt.entity.CollegeEntity;

import java.util.List;

public interface CollegeService {

    public List<CollegeEntity> getAllColleges();

    CollegeEntity getCollegeById(String collegeId);

    CollegeEntity getCollegeByCode(String collegeCode);
}
