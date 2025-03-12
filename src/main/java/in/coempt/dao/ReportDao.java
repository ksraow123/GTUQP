package in.coempt.dao;

import in.coempt.util.QueryUtil;
import in.coempt.vo.AdminDashBoardVo;
import in.coempt.vo.RemunerationReportVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReportDao {
    public List<AdminDashBoardVo> getAdminDashBoard() {
        QueryUtil<AdminDashBoardVo> queryUtil = new QueryUtil<>(AdminDashBoardVo.class);
        return queryUtil.list("SELECT DISTINCT c.`course_code`,c.`course_name`,s.year,s.semester, h.`subject_id`,s.`subject_code`,s.`subject_name`,h.`no_of_sets`,\n" +
                "d.`set_no`,d.`qp_setter_id`,(SELECT CONCAT(first_name,' ',last_name) FROM users WHERE id=d.`qp_setter_id`)setter_name,\n" +
                "m.`moderator_id`,(SELECT CONCAT(first_name,' ',last_name) FROM users WHERE id=m.`moderator_id`)reviewer_name,\n" +
                "CASE WHEN IFNULL(qp_setter_status,'')='' THEN 'Pending' ELSE qp_setter_status END qp_setter_status,\n" +
                "CASE WHEN IFNULL(qp_reviewer_status,'')='' THEN 'Pending' ELSE qp_reviewer_status END qp_reviewer_status\n" +
                "FROM tbl_appointments_bulk h LEFT OUTER JOIN tbl_setter_moderator_mapping m\n" +
                "ON h.`subject_id`=m.`subject_id` AND h.`user_id`=m.`setter_id`\n" +
                ",qp_set_bit_wise_questions d,tbl_subjects s,tbl_courses c\n" +
                "WHERE h.subject_id=d.subject_id AND h.user_id=d.`qp_setter_id` AND h.role_id=2\n" +
                "AND h.subject_id=s.id AND s.course_id=c.id");


    }

    public List<AdminDashBoardVo> getSubjectWiseAdminDashBoard() {
        QueryUtil<AdminDashBoardVo> queryUtil = new QueryUtil<>(AdminDashBoardVo.class);
        return queryUtil.list("SELECT course_name,year,semester,subject_code,(select sum(no_of_sets) from tbl_appointments_bulk" +
                " where subject_id=ts.id and role_id=2) as qp_set,\n" +
                "subject_name,(SELECT count(user_name) FROM tbl_appointments_bulk tb,\n" +
                "users u where u.id=tb.user_id and u.role_id=2 and subject_id=ts.id) as no_of_setters,\n" +
                "(SELECT count(user_name) FROM tbl_appointments_bulk tb,users u\n" +
                "where u.id=tb.user_id and u.role_id=3 and subject_id=ts.id)  as no_of_moderators,\n" +
                "(SELECT count(tb.id) FROM tbl_qp_files tb,users u where u.id=tb.user_id and tb.role_id=2\n" +
                "and subject_id=ts.id and qp_status='FORWARDED')\n" +
                "as setter_completed,(SELECT count(tb.id) FROM tbl_qp_files tb,users u where\n" +
                "u.id=tb.user_id and tb.role_id=3 and subject_id=ts.id and qp_status='APPROVED') as moderator_completed,\n" +
                "(SELECT count(tb.id) FROM tbl_qp_files tb,users u where\n" +
                "u.id=tb.user_id and tb.role_id=3 and subject_id=ts.id and qp_status='REJECTED') as moderator_rejected,\n" +
                "(SELECT count(tb.id) FROM tbl_qp_files tb,users u where\n" +
                "u.id=tb.user_id and tb.role_id=3 and subject_id=ts.id and qp_status='PENDING') as moderator_pending,\n" +
                "\n" +
                "(SELECT count(qp_status) FROM tbl_qp_files tb,users u where u.id=tb.user_id\n" +
                "and tb.role_id=1 and subject_id=ts.id and qp_status='APPROVED') as forward_to_repo_status\n" +
                "\n" +
                " FROM tbl_subjects ts, tbl_courses tc where ts.course_id=tc.id");


    }

    public List<RemunerationReportVo> getRemunerationReport() {
        QueryUtil<RemunerationReportVo> queryUtil = new QueryUtil<>(RemunerationReportVo.class);
        return queryUtil.list("SELECT tu.user_name,concat(first_name,'',last_name) as name,role,subject_code,subject_name,(SELECT sum(no_of_sets) as no_of_sets FROM tbl_appointments_bulk ttb where ttb.subject_id=tb.id and ttb.user_id=tu.id and ttb.role_id=tu.role_id) as no_of_sets,\n" +
                "count(tq.id) as no_of_sets_completed,account_no,ifsc_code,bank_name,branch_details,branch_address FROM users tu,tbl_subjects tb,tbl_roles tr,tbl_profile_details tpr,tbl_qp_files tq\n" +
                " where tq.subject_id=tb.id and tr.id=tu.role_id and tpr.user_id=tu.id\n" +
                " and tq.user_id=tu.id and tq.qp_status in('FORWARDED','APPROVED') group by tb.id,tu.id;");

    }
}
