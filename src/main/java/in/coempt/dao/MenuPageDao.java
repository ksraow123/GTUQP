package in.coempt.dao;

import in.coempt.util.QueryUtil;
import in.coempt.vo.MenuHeaderBean;
import in.coempt.vo.MenuPage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MenuPageDao {


    public List<MenuPage> getUserMenu(String userName) {
        QueryUtil<MenuPage> queryUtil = new QueryUtil<>(MenuPage.class);
        return queryUtil.list("SELECT * FROM menupage  where role_id in(select role_id from users where user_name=?) and isactive=?" +
                " order by menuorder", userName, 1);
    }

    public List<MenuHeaderBean> getMenuHeaders(String userName) {
        QueryUtil<MenuHeaderBean> queryUtil = new QueryUtil<MenuHeaderBean>(MenuHeaderBean.class);
        return queryUtil.list("select  mh.* from menuheader mh,menupage mp where mh.id=mp.headerid and  mp.isactive=1 " +
                "and mp.role_id in(select role_id from users where user_name=?)" +
                " group by mh.id order by mh.menuorder", userName);
    }
}
