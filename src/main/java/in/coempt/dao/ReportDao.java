package in.coempt.dao;

import in.coempt.util.QueryUtil;
import in.coempt.vo.AdminDashBoardVo;
import in.coempt.vo.QPStatusReportVo;
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
                "    u1.id as qp_setter_id,\n" +
                "    COALESCE(u1.user_name, 'NOT ASSIGNED') AS assigned_setters,\n" +
                "    qf_setter.qp_status AS setter_status,\n" +
                "    qf_section.qp_status AS section_team_status,\n" +
                "    qf_approved.qp_status AS forward_to_repo_status\n" +
                "FROM tbl_subjects ts\n" +
                "JOIN tbl_courses tc ON ts.course_id = tc.id\n" +
                "LEFT JOIN tbl_appointments_bulk tab ON tab.subject_id = ts.id\n" +
                "LEFT JOIN users u1 ON u1.id = tab.user_id AND u1.role_id = 2\n" +
                "LEFT JOIN tbl_qp_files qf_setter ON qf_setter.subject_id = ts.id AND qf_setter.role_id = 2  AND qf_setter.user_id = tab.user_id \n" +
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
        return queryUtil.list("SELECT\n" +
                "    tb.id AS appointment_id,ts.course_id,ts.semester,tb.subject_id,\n" +
                "    pr.college_id,\n" +
                "    pr.designation,\n" +
                "    pr.institute_type,\n" +
                "    ur.user_name,\n" +
                "    ur.first_name,\n" +
                "    ur.last_name,\n" +
                "    ur.email,\n" +
                "    ur.mobile_no,\n" +
                "    tg.college_code,\n" +
                "    tg.college_name,\n" +
                "    pr.account_no,\n" +
                "    pr.bank_name,\n" +
                "    pr.branch_details,\n" +
                "    CASE\n" +
                "        WHEN COALESCE(sets.setscompleted, 0) = 0 THEN 'Pending'\n" +
                "        ELSE 'Submitted'\n" +
                "    END AS status,\n" +
                "    CASE\n" +
                "        WHEN COALESCE(sets.setscompleted, 0) = 0 THEN 0\n" +
                "        ELSE (COALESCE(sets.setscompleted, 0) * tc.remuneration_amount)\n" +
                "    END AS remuneration_amount,\n" +
                "    pr.ifsc_code,\n" +
                "    pr.teaching_experience,\n" +
                "    pr.industry_experience,\n" +
                "    pr.branch_address,\n" +
                "    pr.residential_address,ts.subject_code,ts.subject_name \n" +
                "FROM\n" +
                "    tbl_appointments_bulk tb\n" +
                "    LEFT JOIN tbl_profile_details pr ON tb.user_id = pr.user_id\n" +
                "    INNER JOIN tbl_subjects ts ON tb.subject_id = ts.id\n" +
                "    INNER JOIN tbl_courses tc ON ts.course_id = tc.id\n" +
                "    INNER JOIN tbl_college tg ON tb.college_id = tg.id\n" +
                "    INNER JOIN users ur ON ur.id = tb.user_id\n" +
                "    LEFT JOIN (\n" +
                "        SELECT\n" +
                "            user_id,\n" +
                "            subject_id,\n" +
                "            COUNT(id) AS setscompleted\n" +
                "        FROM\n" +
                "            tbl_qp_files\n" +
                "        WHERE\n" +
                "            qp_status = 'SUBMITTED'\n" +
                "            AND role_id = 2\n" +
                "        GROUP BY\n" +
                "            user_id, subject_id\n" +
                "    ) sets ON sets.user_id = tb.user_id AND sets.subject_id = tb.subject_id\n" +
                "WHERE\n" +
                "    ts.section_id IN "+sectionIds+"\n" +
                "GROUP BY\n" +
                "    ur.user_name,tb.subject_id\n" +
                "ORDER BY\n" +
                "    tg.college_code");

    }public List<QPStatusReportVo> getQPStatusReport(String sectionIds) {
        QueryUtil<QPStatusReportVo> queryUtil = new QueryUtil<>(QPStatusReportVo.class);
        return queryUtil.list("SELECT\n" +
                "    tc.course_code,\n" +
                "    tc.course_name,\n" +
                "    COUNT(DISTINCT tb.id) AS no_of_subjects,\n" +
                "      COUNT(DISTINCT CASE\n" +
                "        WHEN tb.id NOT IN (\n" +
                "            SELECT subject_id FROM tbl_appointments_bulk\n" +
                "        ) THEN tb.id\n" +
                "    END) AS no_of_total_subjects_pending,\n"+
                "    COUNT(DISTINCT CASE WHEN tk.current_status = 'Sent' THEN tk.id END) AS appointments_sent,\n" +
                "    COUNT(DISTINCT CASE WHEN tk.current_status = 'Not Sent' THEN tk.id END) AS appointments_not_sent,\n" +
                "    COUNT(DISTINCT CASE WHEN tp.role_id = 2 THEN tp.id END) AS no_of_subjects_submitted,\n" +
                "    COUNT(DISTINCT CASE\n" +
                "        WHEN tk.current_status = 'Sent'\n" +
                "             AND (tk.user_id, tk.subject_id) NOT IN (\n" +
                "                 SELECT user_id, subject_id FROM tbl_qp_files WHERE role_id = 2\n" +
                "             )\n" +
                "        THEN tk.id\n" +
                "    END) AS no_of_subjects_not_submitted,\n" +
                "    COUNT(DISTINCT CASE\n" +
                "        WHEN tp.role_id = 1 AND tp.qp_status = 'PENDING' THEN tp.id\n" +
                "    END) AS no_of_subjects_pending,\n" +
                "    COUNT(DISTINCT CASE\n" +
                "        WHEN tp.role_id = 1 AND tp.qp_status = 'APPROVED' THEN tp.id\n" +
                "    END) AS no_of_subjects_approved,\n" +
                "    COUNT(DISTINCT CASE\n" +
                "        WHEN tp.role_id = 1 AND tp.qp_status = 'REJECTED' THEN tp.id\n" +
                "    END) AS no_of_subjects_rejected\n" +
                "FROM\n" +
                "    tbl_courses tc\n" +
                "Inner JOIN tbl_subjects tb ON tb.course_id = tc.id\n" +
                "LEFT JOIN tbl_appointments_bulk tk ON tk.subject_id = tb.id\n" +
                "LEFT JOIN tbl_qp_files tp ON tp.subject_id = tb.id where tc.section_id in "+sectionIds+ "\n" +
                "GROUP BY tc.id WITH ROLLUP;");

    }

    public List<RemunerationReportVo> getSetterRemunerationReport(int userId) {
        QueryUtil<RemunerationReportVo> queryUtil = new QueryUtil<>(RemunerationReportVo.class);
        return queryUtil.list("SELECT\n" +
                "    tb.id AS appointment_id,ts.course_id,ts.semester,tb.subject_id,\n" +
                "    pr.college_id,\n" +
                "    pr.designation,\n" +
                "    pr.institute_type,\n" +
                "    ur.user_name,\n" +
                "    ur.first_name,\n" +
                "    ur.last_name,\n" +
                "    ur.email,\n" +
                "    ur.mobile_no,\n" +
                "    tg.college_code,\n" +
                "    tg.college_name,\n" +
                "    pr.account_no,\n" +
                "    pr.bank_name,\n" +
                "    pr.branch_details,\n" +
                "    CASE\n" +
                "        WHEN COALESCE(sets.setscompleted, 0) = 0 THEN 'Pending'\n" +
                "        ELSE 'Submitted'\n" +
                "    END AS status,\n" +
                "    CASE\n" +
                "        WHEN COALESCE(sets.setscompleted, 0) = 0 THEN 0\n" +
                "        ELSE (COALESCE(sets.setscompleted, 0) * tc.remuneration_amount)\n" +
                "    END AS remuneration_amount,\n" +
                "    pr.ifsc_code,\n" +
                "    pr.teaching_experience,\n" +
                "    pr.industry_experience,\n" +
                "    pr.branch_address,\n" +
                "    pr.residential_address,ts.subject_code,ts.subject_name \n" +
                "FROM\n" +
                "    tbl_appointments_bulk tb\n" +
                "    LEFT JOIN tbl_profile_details pr ON tb.user_id = pr.user_id\n" +
                "    INNER JOIN tbl_subjects ts ON tb.subject_id = ts.id\n" +
                "    INNER JOIN tbl_courses tc ON ts.course_id = tc.id\n" +
                "    INNER JOIN tbl_college tg ON tb.college_id = tg.id\n" +
                "    INNER JOIN users ur ON ur.id = tb.user_id\n" +
                "    LEFT JOIN (\n" +
                "        SELECT\n" +
                "            user_id,\n" +
                "            subject_id,\n" +
                "            COUNT(id) AS setscompleted\n" +
                "        FROM\n" +
                "            tbl_qp_files\n" +
                "        WHERE\n" +
                "            qp_status = 'SUBMITTED'\n" +
                "            AND role_id = 2\n" +
                "        GROUP BY\n" +
                "            user_id, subject_id\n" +
                "    ) sets ON sets.user_id = tb.user_id AND sets.subject_id = tb.subject_id\n" +
            "WHERE ur.id="+userId+" GROUP BY ur.user_name,tb.subject_id ORDER BY tg.college_code");



    }
}
