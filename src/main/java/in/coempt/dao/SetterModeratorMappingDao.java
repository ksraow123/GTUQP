package in.coempt.dao;

import in.coempt.util.QueryUtil;
import in.coempt.vo.SetterModeratorMappingVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SetterModeratorMappingDao {

    public List<SetterModeratorMappingVo> setterModeratorMappingVoList() {

        QueryUtil<SetterModeratorMappingVo> queryUtil = new QueryUtil<>(SetterModeratorMappingVo.class);

        // Build the dynamic query with placeholders
        String query = "SELECT (select concat(first_name,' ',last_name) from users where id=t.setter_id ) as setter_name,(select concat(first_name,' ',last_name) from users where id=t.moderator_id ) as moderator_name ,(select user_name from users where id=t.moderator_id ) as moderator,assigned_date,\n" +
                "(select user_name from users where id=t.setter_id ) as qpsetter,(select concat(subject_code,'-',subject_name)\n" +
                "from tbl_subjects where id=t.subject_id) as subject_details FROM tbl_setter_moderator_mapping t; ";

        // Pass the list of IDs as parameters
        return queryUtil.list(query);

    }
}
