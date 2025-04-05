package in.coempt.service;

import in.coempt.entity.ProfileDetailsEntity;
import in.coempt.vo.ProfileDetailsVo;

import java.util.Optional;

public interface ProfileDetailsService {
      public ProfileDetailsVo getProfileDetails(Long userId);
      public ProfileDetailsEntity getProfileDetailsByUserId(Long userId);

   public ProfileDetailsEntity save(ProfileDetailsEntity profileDetails);

    Optional<ProfileDetailsEntity> getFacultyByMobileNumber(String mobileNumber);
}
