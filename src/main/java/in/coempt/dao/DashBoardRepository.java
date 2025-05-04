package in.coempt.dao;

import in.coempt.util.QueryUtil;
import in.coempt.vo.MenuPage;
import in.coempt.vo.QPSetterDashBoardVo;
import in.coempt.vo.SectionTeamDashBoard;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DashBoardRepository {

    public List<QPSetterDashBoardVo> getSetterDashBoard(String userName,Long userId) {
        QueryUtil<QPSetterDashBoardVo> queryUtil = new QueryUtil<>(QPSetterDashBoardVo.class);
        return queryUtil.list("SELECT ts.pattern_code,ts.syllabus,qp.subject_id,ts.subject_code,ts.subject_name,course_name,year,semester,no_of_sets," +
                "last_date_to_submit as submission_date,\n" +
                "(SELECT count(id) FROM tbl_qp_files where subject_id=ts.id and user_id=? and qp_status='SUBMITTED') as no_of_sets_uploaded,\n" +
                "(SELECT count(id) FROM tbl_qp_files where subject_id=ts.id and user_id=? and qp_status='SUBMITTED') as no_of_sets_forwarded\n" +
                "FROM tbl_appointments_bulk qp,tbl_subjects ts,tbl_courses tc where tc.id=ts.course_id\n" +
                "and ts.id=qp.subject_id and qp.user_id=?", userId,userId,userId);
    }

    public List<QPSetterDashBoardVo> getModeratorDashBoard(String userName,Long userId) {
        QueryUtil<QPSetterDashBoardVo> queryUtil = new QueryUtil<>(QPSetterDashBoardVo.class);
        return queryUtil.list("SELECT ts.syllabus,qp.subject_id,ts.subject_code,ts.subject_name,course_name,year,semester,no_of_sets," +
                "last_date_to_submit as submission_date,\n" +
                        "(SELECT count(id) FROM tbl_qp_files where user_id=? and subject_id=ts.id and qp_status='PENDING') as no_of_sets_uploaded,\n" +
                        "(SELECT count(id) FROM tbl_qp_files where user_id=? and subject_id=ts.id and qp_status='APPROVED') as no_of_sets_forwarded\n" +
                        "FROM tbl_appointments_bulk qp,tbl_subjects ts,tbl_courses tc where tc.id=ts.course_id\n" +
                        "and ts.id=qp.subject_id and qp.user_id=?", userId,userId,userId);
    }
    public List<QPSetterDashBoardVo>    getSetterStatusReport(int seriesId,int subjectId,Long setterId) {
        QueryUtil<QPSetterDashBoardVo> queryUtil = new QueryUtil<>(QPSetterDashBoardVo.class);
        return queryUtil.list("SELECT tc.course_name,year,semester,subject_code,subject_name,user_name,role," +
                "old_qp_status,new_qp_status,qp_status_date,remarks \n" +
                "FROM tbl_qp_files_history th,tbl_subjects tb,tbl_roles tr,users tu,tbl_courses tc\n" +
                "where tu.id=th.user_id and tr.id=tu.role_id and tc.id=tb.course_id and  " +
                "th.subject_id=tb.id and th.exam_series_id=? and tb.id=? and th.qp_setter_id=?", seriesId,subjectId,setterId);
    }
 public List<SectionTeamDashBoard> getSectionDashBoard(Integer userId,Integer examSeriesId) {
        QueryUtil<SectionTeamDashBoard> queryUtil = new QueryUtil<>(SectionTeamDashBoard.class);
        return queryUtil.list("SELECT  (select group_concat(concat(q_no,'',bit_no))" +
                " from qp_set_bit_wise_questions where subject_id=t1.subject_id and" +
                " qp_setter_id=t1.user_id and qp_reviewer_id=? and qp_reviewer_status='REJECTED') as rejectedQnos, \n" +
                "    t1.user_id, \n" +
                "    t1.subject_id, \n" +
                "    t1.course_id, \n" +
                "    t1.year, \n" +
                "    t1.semester, \n" +
                "    t1.course_name, \n" +
                "    t1.subject_code, \n" +
                "    t1.subject_name,\n" +
                "    COALESCE(t2.qp_status, 'NOT RECEIVED') AS qp_status, \n" +
                "    t2.remarks, \n" +
                "    t2.qp_status_date," +
                "(SELECT count(qr.id) FROM tbl_qp_repository_details qr where qr.qp_files_ref_id=t2.id and is_used=1) as is_used \n" +
                "FROM (\n" +
                "    SELECT \n" +
                "        ts.course_id, \n" +
                "        ts.section_id, \n" +
                "        ts.id AS subject_id, \n" +
                "        tb.role_id, \n" +
                "        tb.user_id, \n" +
                "        tb.exam_series_id, \n" +
                "        ts.subject_code, \n" +
                "        ts.subject_name, \n" +
                "        ts.semester, \n" +
                "        tc.course_name, \n" +
                "        ts.year\n" +
                "    FROM tbl_appointments_bulk tb\n" +
                "    JOIN tbl_subjects ts ON tb.subject_id = ts.id\n" +
                "    JOIN tbl_courses tc ON tc.id = ts.course_id\n" +
                ") AS t1\n" +
                "LEFT JOIN tbl_qp_files t2 \n" +
                "    ON t1.subject_id = t2.subject_id \n" +
                "    AND t1.user_id = t2.qp_setter_id\n" +
               "     AND t2.role_id =1" +
                "    AND t1.exam_series_id = t2.exam_series_id\n" +
                "JOIN tbl_section_user_mapping tsum\n" +
                "    ON t1.section_id = tsum.section_id \n" +
                "WHERE tsum.user_id = ? and t1.exam_series_id=?\n" +
                "ORDER BY t1.subject_code;\n" +
                "\n",userId, userId,examSeriesId);
    }

    public List<QPSetterDashBoardVo> getSetWiseQPDashBoard(Long userId) {
        QueryUtil<QPSetterDashBoardVo> queryUtil = new QueryUtil<>(QPSetterDashBoardVo.class);
        return queryUtil.list("SELECT ts.pattern_code,qp.user_id,ts.syllabus, qp.subject_id, ts.subject_code,\n" +
                "    ts.subject_name,\n" +
                "    tc.course_name,\n" +
                "    ts.year,\n" +
                "    ts.semester,\n" +
                "    ns.n as setno, " +
                "(SELECT count(id) FROM qp_set_bit_wise_questions where  set_no=ns.n and subject_id=ts.id and  q_flag=1 and  q_desc is  null and qp_setter_id=qp.user_id) as pending_questions," +
                "(SELECT count(id) FROM qp_set_bit_wise_questions where  set_no=ns.n and subject_id=ts.id and q_flag =1 and qp_setter_id=qp.user_id) as total_questions,\n" +
                "(SELECT count(id) FROM qp_set_bit_wise_questions where  set_no=ns.n and subject_id=ts.id and q_flag=1 and q_desc is not null  and qp_setter_id=qp.user_id) as no_of_questions ,\n" +
                "(SELECT qp_status FROM tbl_qp_files where user_id=? and set_no=ns.n and subject_id=ts.id ) as qp_status ," +
               // "(SELECT remarks FROM tbl_qp_files where user_id=? and set_no=ns.n and subject_id=ts.id ) as remarks ,\n" +
                "  qp.last_date_to_submit AS submission_date,CASE WHEN last_date_to_submit >= CURRENT_DATE THEN 'Valid'\n" +
                "        ELSE 'Expired'\n" +
                "    END AS lastDateStatus\n" +
                "FROM tbl_appointments_bulk qp\n" +
                "JOIN tbl_subjects ts ON ts.id = qp.subject_id\n" +
                "JOIN tbl_courses tc ON tc.id = ts.course_id\n" +
                "JOIN numbers ns ON ns.n <= qp.no_of_sets\n" +
                "WHERE qp.user_id = ? ORDER BY qp.subject_id, ns.n", userId,userId);
    }
public List<QPSetterDashBoardVo> getSetWiseReviewerQPDashBoard(Long userId) {
        QueryUtil<QPSetterDashBoardVo> queryUtil = new QueryUtil<>(QPSetterDashBoardVo.class);
        return queryUtil.list("SELECT ts.syllabus, qp.subject_id, ts.subject_code,\n" +
                "    ts.subject_name,\n" +
                "    tc.course_name,\n" +
                "    ts.year,\n" +
                "    ts.semester,\n" +
                "    ns.n as setno," +
                "(SELECT count(id) FROM qp_set_bit_wise_questions where  set_no=ns.n and subject_id=ts.id and qp_reviewer_id=qp.user_id) as total_questions ," +
                "(SELECT count(id) FROM qp_set_bit_wise_questions where  set_no=ns.n and subject_id=ts.id and qp_reviewer_id=qp.user_id and qp_reviewer_status is null) as pending_questions ," +
                "(SELECT count(id) FROM qp_set_bit_wise_questions\n" +
                "    where  set_no=ns.n and subject_id=ts.id and qp_reviewer_id=qp.user_id and qp_reviewer_status='Approved') as no_of_questions ,\n" +
                "(SELECT qp_status FROM tbl_qp_files where user_id=? and set_no=ns.n and subject_id=ts.id)\n" +
                " as qp_status ,\n" +
                "  qp.last_date_to_submit AS submission_date\n" +
                "FROM tbl_appointments_bulk qp\n" +
                "JOIN tbl_subjects ts ON ts.id = qp.subject_id\n" +
                "JOIN tbl_courses tc ON tc.id = ts.course_id\n" +
                "JOIN numbers ns ON ns.n <= qp.no_of_sets\n" +
                "WHERE qp.user_id = ? ORDER BY qp.subject_id, ns.n", userId,userId);
    }

}
