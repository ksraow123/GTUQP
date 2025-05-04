package in.coempt.service.impl;

import in.coempt.dao.ProfileDetailsDao;
import in.coempt.entity.ProfileDetailsEntity;
import in.coempt.repository.ProfileDetailsRepository;
import in.coempt.service.ProfileDetailsService;
import in.coempt.vo.ProfileDetailsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileDetailsServiceImpl implements ProfileDetailsService {
    @Autowired
    ProfileDetailsDao profileDetailsDao;

    @Autowired
    ProfileDetailsRepository profileDetailsRepository;

    @Override
    public ProfileDetailsVo getProfileDetails(Long userId) {
        return profileDetailsDao.getProfileDetails(userId);
    }

    @Override
    public ProfileDetailsEntity getProfileDetailsByUserId(Long userId) {
        return profileDetailsRepository.findByUserId(userId);
    }

    @Override
    public ProfileDetailsEntity save(ProfileDetailsEntity profileDetails) {
        return profileDetailsRepository.save(profileDetails);

    }

    @Override
    public Optional<ProfileDetailsEntity> getFacultyByMobileNumber(String mobileNumber) {
        return profileDetailsRepository.findByContact(mobileNumber);
    }

    @Override
    public Optional<ProfileDetailsEntity> getFindById(Long userId) {
        return profileDetailsRepository.findById(userId);
    }

    @Override
    public void deleteProfileDetails(Long id) {
         profileDetailsRepository.deleteById(id);
    }
}
