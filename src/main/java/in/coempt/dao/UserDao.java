package in.coempt.dao;

import in.coempt.vo.UserMacIdsVo;
import in.coempt.vo.UserVo;
import in.coempt.util.QueryUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao {


    public List<UserVo> getAllUser() {
        QueryUtil<UserVo> queryUtil = new QueryUtil<>(UserVo.class);
        return queryUtil.list("SELECT * FROM users");
    }

    public List<UserMacIdsVo> getMacIds(String macid) {
        QueryUtil<UserMacIdsVo> queryUtil = new QueryUtil<>(UserMacIdsVo.class);
        return queryUtil.list("SELECT * FROM tbl_center_macids where system_macid=?",macid);

    }
}
