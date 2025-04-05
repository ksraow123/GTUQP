package in.coempt.dao;

import in.coempt.util.QueryUtil;
import in.coempt.vo.AdminDashBoardVo;
import in.coempt.vo.RemunerationReportVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReportDao {
    public List<AdminDashBoardVo> getAdminDashBoard(Integer userId) {
        QueryUtil<AdminDashBoardVo> queryUtil = new QueryUtil<>(AdminDashBoardVo.class);

//        return queryUtil.list("SELECT DISTINCT c.id as course_id,c.course_code,c.course_name,s.year,s.semester, h.subject_id,s.subject_code,s.subject_name,h.no_of_sets,\n" +
//                "d.set_no,d.qp_setter_id,\n" +
//                "(SELECT CONCAT(user_name,' - ', first_name,' ',last_name) FROM users WHERE id=d.qp_setter_id)setter_name,\n" +
//                "CASE WHEN IFNULL(qp_setter_status,'')='' THEN 'Pending' ELSE qp_setter_status END qp_setter_status,\n" +
//                "CASE WHEN IFNULL(qp_reviewer_status,'')='' THEN 'Pending' ELSE qp_reviewer_status END qp_reviewer_status\n" +
//                "FROM tbl_appointments_bulk h,qp_set_bit_wise_questions d,tbl_subjects s,tbl_courses c\n" +
//                "WHERE h.subject_id=d.subject_id AND h.user_id=d.qp_setter_id AND h.role_id=2\n" +
//                "AND h.subject_id=s.id AND s.course_id=c.id");

//
//
        return queryUtil.list("SELECT \n" +
                "    ts.course_id,\n" +
                "    ts.id AS subject_id,\n" +
                "    tc.course_name,\n" +
                "    ts.year,\n" +
                "    ts.semester,\n" +
                "    ts.subject_code,\n" +
                "    tc.section_id,\n" +
                "    ts.subject_name," +
                "    ts.pattern_code,\n" +
                "    COALESCE(u1.user_name, 'NOT ASSIGNED') AS assigned_setters,\n" +
                "    qf_setter.qp_status AS setter_status,\n" +
                "    qf_section.qp_status AS section_team_status,\n" +
                "    qf_approved.qp_status AS forward_to_repo_status\n" +
                "FROM tbl_subjects ts\n" +
                "JOIN tbl_courses tc ON ts.course_id = tc.id\n" +
                "LEFT JOIN tbl_appointments_bulk tab ON tab.subject_id = ts.id\n" +
                "LEFT JOIN users u1 ON u1.id = tab.user_id AND u1.role_id = 2\n" +
                "LEFT JOIN tbl_qp_files qf_setter ON qf_setter.subject_id = ts.id AND qf_setter.role_id = 2\n" +
                "LEFT JOIN users u2 ON u2.id = qf_setter.user_id\n" +
                "LEFT JOIN tbl_qp_files qf_section ON qf_section.subject_id = ts.id AND qf_section.role_id = 1\n" +
                "LEFT JOIN users u3 ON u3.id = qf_section.user_id\n" +
                "LEFT JOIN tbl_qp_files qf_approved ON qf_approved.subject_id = ts.id AND qf_approved.role_id = 1 AND qf_approved.qp_status = 'APPROVED'\n" +
                "LEFT JOIN users u4 ON u4.id = qf_approved.user_id group by ts.course_id, ts.id, u1.user_name \n"+
                "ORDER BY ts.course_id, ts.id, u1.user_name, qf_setter.qp_status, qf_section.qp_status, qf_approved.qp_status;\n");
// return queryUtil.list("SELECT ts.course_id,ts.id as subject_id,course_name,year,semester,subject_code,tc.section_id,\n" +
//                "subject_name,(SELECT group_concat(user_name) FROM tbl_appointments_bulk tb,\n" +
//                "users u where u.id=tb.user_id and u.role_id=2 and subject_id=ts.id) as assigned_setters,\n" +
//                "(SELECT group_concat(qp_status) FROM tbl_qp_files tb,users u where u.id=tb.user_id and tb.role_id=2 and subject_id=ts.id)\n" +
//                "as setter_status,(SELECT group_concat(qp_status) FROM tbl_qp_files tb,users u where u.id=tb.user_id and\n" +
//                "tb.role_id=1 and subject_id=ts.id) as section_team_status,\n" +
//                "(SELECT group_concat(qp_status) FROM tbl_qp_files tb,users u where u.id=tb.user_id\n" +
//                "and tb.role_id=1 and subject_id=ts.id and qp_status='APPROVED') as forward_to_repo_status\n" +
//                " FROM tbl_subjects ts, tbl_courses tc where ts.course_id=tc.id");
//

    }

    public List<AdminDashBoardVo> getSubjectWiseAdminDashBoard(Integer userId,String sessionIds) {
        QueryUtil<AdminDashBoardVo> queryUtil = new QueryUtil<>(AdminDashBoardVo.class);
        return queryUtil.list("SELECT ts.pattern_code,ts.course_id,ts.id as subject_id,course_name,year,semester,subject_code,subject_name," +
                "(SELECT count(tb.id) FROM tbl_appointments_bulk tb,users u where u.id=tb.user_id and u.role_id=2 and subject_id=ts.id) as assigned_setters,\n" +
                "(SELECT count(tb.id) FROM tbl_qp_files tb,users u where u.id=tb.user_id and tb.role_id=2\n" +
                "and subject_id=ts.id and qp_status='SUBMITTED')\n" +
                "as setter_completed,\n" +
                "(SELECT count(tb.id) FROM tbl_qp_files tb,users u where u.id=tb.user_id and tb.role_id=2\n" +
                "and subject_id=ts.id and qp_status in('PENDING','Re-Submit'))\n" +
                "as setter_pending,\n" +
                "(SELECT count(tb.id) FROM tbl_qp_files tb,users u where\n" +
                "u.id=tb.user_id and tb.role_id=1 and subject_id=ts.id and qp_status='APPROVED') as moderator_completed,\n" +
                "(SELECT count(tb.id) FROM tbl_qp_files tb,users u where\n" +
                "u.id=tb.user_id and tb.role_id=1 and subject_id=ts.id and qp_status='REJECTED') as moderator_rejected,\n" +
                "(SELECT count(tb.id) FROM tbl_qp_files tb,users u where\n" +
                "u.id=tb.user_id and tb.role_id=1 and subject_id=ts.id and qp_status in('PENDING','Re-Submit')) as moderator_pending,\n" +
                "\n" +
                "(select count(tp.id) from tbl_qp_repository_details tp where tp.subject_id=ts.id and tp.is_used=1) as forward_to_repo_count\n" +
                "\n" +
                " FROM tbl_subjects ts, tbl_courses tc where ts.course_id=tc.id and\n" +
                "ts.section_id in "+sessionIds+"");


    }

    public List<RemunerationReportVo> getRemunerationReport(Integer userId,String sectionIds) {
        QueryUtil<RemunerationReportVo> queryUtil = new QueryUtil<>(RemunerationReportVo.class);
        return queryUtil.list("SELECT pr.designation,pr.faculty_type,ur.user_name,ur.first_name,ur.last_name,ur.mobile_no,tg.college_code,tg.college_name,tb.subject_id,ts.subject_code,ts.subject_name,ts.course_id,ts.semester,\n" +
                "pr.account_no, pr.bank_name, pr.branch_details,(select count(id) from tbl_qp_files tq where tq.user_id=ur.id and tq.subject_id=tb.subject_id and tq.qp_status='SUBMITTED') as setscompleted,\n" +
                "pr.designation, pr.ifsc_code, pr.teaching_experience, pr.industry_experience, pr.branch_address, pr.residential_address\n" +
                " FROM tbl_appointments_bulk tb left join tbl_profile_details pr on tb.user_id=pr.user_id,tbl_subjects ts,tbl_courses tc ,tbl_college tg,users ur\n" +
                " where ur.id=tb.user_id and tb.subject_id=ts.id and tc.id=ts.course_id and tg.id=tb.college_id and ts.section_id in "+sectionIds+ " order by tg.college_code;");

    }
}
