package in.coempt.service.impl;

import in.coempt.dao.ProfileDetailsDao;
import in.coempt.service.ProfileDetailsService;
import in.coempt.vo.ProfileDetailsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileDetailsServiceImpl implements ProfileDetailsService {
    @Autowired
    ProfileDetailsDao profileDetailsDao;

    @Override
    public ProfileDetailsVo getProfileDetails(Long userId) {
        return profileDetailsDao.getProfileDetails(userId);
    }
}
