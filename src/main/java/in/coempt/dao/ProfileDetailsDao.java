package in.coempt.dao;

import in.coempt.util.QueryUtil;
import in.coempt.vo.ProfileDetailsVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProfileDetailsDao {
    public ProfileDetailsVo getProfileDetails(Long userId) {
        QueryUtil<ProfileDetailsVo> queryUtil = new QueryUtil<>(ProfileDetailsVo.class);
        return queryUtil.get("SELECT tp.faculty_type,tp.residential_address,u.user_name,tp.industry_experience,tp.id,u.mobile_no,u.email,u.first_name,u.last_name,account_no,bank_name," +
                "branch_details,branch_address,designation,ifsc_code,teaching_experience " +
                "FROM users u LEFT join tbl_profile_details tp on u.id=tp.user_id where u.id=?",userId);
    }



}