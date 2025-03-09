package in.coempt.dao;

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
}
