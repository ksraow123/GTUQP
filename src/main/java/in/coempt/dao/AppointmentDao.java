package in.coempt.dao;

import in.coempt.util.QueryUtil;
import in.coempt.vo.AppointmentVo;
import in.coempt.vo.ProfileDetailsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AppointmentDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<AppointmentVo> getAppointmentsById(int appointmentId) {
        QueryUtil<AppointmentVo> queryUtil = new QueryUtil<>(AppointmentVo.class);
        return queryUtil.list(" SELECT user_name,office_order_date,last_date_to_submit,no_of_sets FROM\n" +
                "    users tu,tbl_appointments_bulk tp,tbl_college tc where tc.id=tp.college_id and tu.id=tp.user_id and tp.id=5;\n" +
                "   ",appointmentId);
    }


    public List<AppointmentVo> getAppointmentDetailsList(List<Long> selectedIds) {
        if (selectedIds == null || selectedIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Convert the list into a comma-separated string for the IN clause
        String idPlaceholders = selectedIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        QueryUtil<AppointmentVo> queryUtil = new QueryUtil<>(AppointmentVo.class);

        // Build the dynamic query with placeholders
        String query = "SELECT tu.first_name,tu.last_name,tp.status_date, tp.current_status, tu.mobile_no,tp.is_appointment_sent,tp.college_id,tu.role_id,tp.subject_id,tp.id,tu.email,tu.user_name, tp.office_order_date, tp.last_date_to_submit, tp.no_of_sets " +
                "FROM users tu, tbl_appointments_bulk tp, tbl_college tc " +
                "WHERE tc.id = tp.college_id AND tu.id = tp.user_id " +
                "AND tp.id IN (" + idPlaceholders + ")";

        // Pass the list of IDs as parameters
        return queryUtil.list(query, selectedIds.toArray());
    }
    public List<AppointmentVo> getAppointmentDetailsList() {

        QueryUtil<AppointmentVo> queryUtil = new QueryUtil<>(AppointmentVo.class);

        // Build the dynamic query with placeholders
        String query = "SELECT tu.id as user_id,tsb.syllabus,tu.last_name,tr.role,tu.first_name,tc.college_code,tc.college_name,tsb.subject_code,tsb.subject_name,tcc.course_name,tp.college_id,tu.role_id,tp.subject_id,tp.id,tu.email,tu.user_name,tu.mobile_no,\n" +
                "                tp.status_date, tp.appointment_sent_date,tp.current_status, tp.is_appointment_sent,\n" +
                "                 tp.office_order_date, tp.last_date_to_submit, tp.no_of_sets\n" +
                "                FROM users tu, tbl_roles tr,tbl_appointments_bulk tp, tbl_college tc,tbl_subjects tsb,tbl_courses tcc\n" +
                "                WHERE tr.id=tu.role_id and tc.id = tp.college_id AND tu.id = tp.user_id and tsb.id=tp.subject_id and tcc.id=tsb.course_id ";

        // Pass the list of IDs as parameters
        return queryUtil.list(query);
    }
    public List<AppointmentVo> getAppointmentDetailsList(String mobileNumber) {

        QueryUtil<AppointmentVo> queryUtil = new QueryUtil<>(AppointmentVo.class);

        // Build the dynamic query with placeholders
        String query = "SELECT tu.id as user_id,tsb.syllabus,tu.last_name,tr.role,tu.first_name,tc.college_code,tc.college_name,tsb.subject_code,tsb.subject_name,tcc.course_name,tp.college_id,tu.role_id,tp.subject_id,tp.id,tu.email,tu.user_name,tu.mobile_no,\n" +
                "                tp.status_date, tp.appointment_sent_date,tp.current_status, tp.is_appointment_sent,\n" +
                "                 tp.office_order_date, tp.last_date_to_submit, tp.no_of_sets\n" +
                "                FROM users tu, tbl_roles tr,tbl_appointments_bulk tp, tbl_college tc,tbl_subjects tsb,tbl_courses tcc\n" +
                "                WHERE tr.id=tu.role_id and tc.id = tp.college_id AND tu.id = tp.user_id and tsb.id=tp.subject_id and tcc.id=tsb.course_id and tu.mobile_no=?";

        // Pass the list of IDs as parameters
        return queryUtil.list(query,mobileNumber);
    }

    public List<AppointmentVo> getAppointmentDshBoardBySection(String sectionId) {

        QueryUtil<AppointmentVo> queryUtil = new QueryUtil<>(AppointmentVo.class);

        // Build the dynamic query with placeholders
        String query = "SELECT tsb.pattern_code,tsb.course_id,tsb.year,tsb.semester,tu.id as user_id,\n" +
                "tsb.syllabus,tu.last_name,tr.role,tu.first_name,tc.college_code,tc.college_name,tsb.subject_code,\n" +
                "tsb.subject_name,tcc.course_name,tp.college_id,tu.role_id,tp.subject_id,tp.id,tu.email,tu.user_name,tu.mobile_no,\n" +
                "                tp.status_date, tp.appointment_sent_date,tp.current_status, tp.is_appointment_sent,\n" +
                "                 tp.office_order_date, tp.last_date_to_submit, tp.no_of_sets,\n" +
                "                 COALESCE((select qp_status from tbl_qp_files where role_id=2 and user_id=tp.user_id and subject_id=tp.subject_id),'PENDING')\n" +
                "                  as setter_status,\n" +
                "COALESCE((select qp_status from tbl_qp_files where role_id=1 and qp_setter_id=tp.user_id and subject_id=tp.subject_id),'NOT RECEIVED') as review_status\n" +
                "                FROM users tu, tbl_roles tr,tbl_appointments_bulk tp, tbl_college tc,tbl_subjects tsb,tbl_courses tcc\n" +
                "                WHERE tr.id=tu.role_id and tc.id = tp.college_id AND tu.id = tp.user_id\n" +
                " and tsb.id=tp.subject_id and tcc.id=tsb.course_id and tsb.section_id in "+sectionId+"";

        // Pass the list of IDs as parameters
        return queryUtil.list(query);
    }

    public ProfileDetailsVo getMaxUserPlusOne(int roleId) {
        QueryUtil<ProfileDetailsVo> queryUtil = new QueryUtil<>(ProfileDetailsVo.class);
        return queryUtil.get("SELECT max(substr(user_name,2,10))+1 as max_user FROM users where role_id=?",roleId);
    }

    public void deleteBySectionUserId(Long userId) {
        jdbcTemplate.update("delete from faculty_appointment where section_user_id=?", new Object[]{userId});
    }

    public void updateFacultyAppointmentDates(String courseId, String submissionDate) {
        jdbcTemplate.update("UPDATE tbl_appointments_bulk" +
                        "SET last_date_to_submit = ?\n" +
                        "WHERE subject_id IN (\n" +
                        "    SELECT id FROM tbl_subjects WHERE course_id = ?\n" +
                        ")\n" +
                        "AND NOT EXISTS (\n" +
                        "    SELECT 1\n" +
                        "    FROM tbl_qp_files q\n" +
                        "    WHERE q.subject_id = tbl_appointments_bulk.subject_id\n" +
                        "      AND q.user_id = tbl_appointments_bulk.user_id\n" +
                        "      AND q.role_id = 2\n" +
                        ")",
                new Object[]{submissionDate,courseId});
    }

    public String getCourseNamesBySubjectCode(String subjectCode) {
        return jdbcTemplate.queryForObject(
                "SELECT GROUP_CONCAT(distinct course_name) " +
                        "FROM tbl_subjects ts JOIN tbl_courses tc ON tc.id = ts.course_id " +
                        "WHERE ts.subject_code = ?",
                new Object[]{subjectCode},
                String.class
        );
    } public String getSemestersBySubjectCode(String subjectCode) {
        return jdbcTemplate.queryForObject(
                "SELECT GROUP_CONCAT(distinct semester_romanno) " +
                        "FROM tbl_subjects ts JOIN tbl_courses tc ON tc.id = ts.course_id " +
                        "WHERE ts.subject_code = ?",
                new Object[]{subjectCode},
                String.class
        );
    }


    public void updateSemesterWiseFacultyAppointmentDates(String courseId, String submissionDate, String semester) {
        jdbcTemplate.update("UPDATE tbl_appointments_bulk" +
                        "SET last_date_to_submit = ?\n" +
                        "WHERE subject_id IN (\n" +
                        "    SELECT id FROM tbl_subjects WHERE course_id = ? and semester=? \n" +
                        ")\n" +
                        "AND NOT EXISTS (\n" +
                        "    SELECT 1\n" +
                        "    FROM tbl_qp_files q\n" +
                        "    WHERE q.subject_id = tbl_appointments_bulk.subject_id\n" +
                        "      AND q.user_id = tbl_appointments_bulk.user_id\n" +
                        "      AND q.role_id = 2\n" +
                        ")",
                new Object[]{submissionDate,courseId,semester});

    }

    public void updateSubjectWiseFacultyAppointmentDates(String courseId, String submissionDate, String subjectId) {
        jdbcTemplate.update("UPDATE tbl_appointments_bulk" +
                        "SET last_date_to_submit = ?\n" +
                        "WHERE subject_id IN (\n" +
                        "    SELECT id FROM tbl_subjects WHERE course_id = ? and subject_id=? \n" +
                        ")\n" +
                        "AND NOT EXISTS (\n" +
                        "    SELECT 1\n" +
                        "    FROM tbl_qp_files q\n" +
                        "    WHERE q.subject_id = tbl_appointments_bulk.subject_id\n" +
                        "      AND q.user_id = tbl_appointments_bulk.user_id\n" +
                        "      AND q.role_id = 2\n" +
                        ")",
                new Object[]{submissionDate,courseId,subjectId});
    }
}
