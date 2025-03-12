package in.coempt.dao;

import in.coempt.util.QueryUtil;
import in.coempt.vo.MenuPage;
import in.coempt.vo.QPSetterDashBoardVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DashBoardRepository {

    public List<QPSetterDashBoardVo> getSetterDashBoard(String userName,Long userId) {
        QueryUtil<QPSetterDashBoardVo> queryUtil = new QueryUtil<>(QPSetterDashBoardVo.class);
        return queryUtil.list("SELECT ts.syllabus,qp.subject_id,ts.subject_code,ts.subject_name,course_name,year,semester,no_of_sets," +
                "last_date_to_submit as submission_date,\n" +
                "(SELECT count(id) FROM tbl_qp_files where subject_id=ts.id and user_id=? and qp_status='FORWARDED') as no_of_sets_uploaded,\n" +
                "(SELECT count(id) FROM tbl_qp_files where subject_id=ts.id and user_id=? and qp_status='FORWARDED') as no_of_sets_forwarded\n" +
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

    public List<QPSetterDashBoardVo> getSetWiseQPDashBoard(Long userId) {
        QueryUtil<QPSetterDashBoardVo> queryUtil = new QueryUtil<>(QPSetterDashBoardVo.class);
        return queryUtil.list("SELECT ts.syllabus, qp.subject_id, ts.subject_code,\n" +
                "    ts.subject_name,\n" +
                "    tc.course_name,\n" +
                "    ts.year,\n" +
                "    ts.semester,\n" +
                "    ns.n as setno, " +
                "(SELECT count(id) FROM qp_set_bit_wise_questions where  set_no=ns.n and subject_id=ts.id and q_desc is  null and qp_setter_id=qp.user_id) as pending_questions," +
                "(SELECT count(id) FROM qp_set_bit_wise_questions where  set_no=ns.n and subject_id=ts.id  and qp_setter_id=qp.user_id) as total_questions,\n" +
                "(SELECT count(id) FROM qp_set_bit_wise_questions where  set_no=ns.n and subject_id=ts.id and q_desc is not null  and qp_setter_id=qp.user_id) as no_of_questions ,\n" +
                "(SELECT qp_status FROM tbl_qp_files where user_id=? and set_no=ns.n and subject_id=ts.id ) as qp_status ,\n" +
                "  qp.last_date_to_submit AS submission_date\n" +
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
