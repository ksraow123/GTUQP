package in.coempt.dao;

import in.coempt.util.QueryUtil;
import in.coempt.vo.AppointmentVo;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AppointmentDao {



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
        String query = "SELECT tsb.course_id,tsb.year,tsb.semester,tu.id as user_id,tsb.syllabus,tu.last_name,tr.role,tu.first_name,tc.college_code,tc.college_name,tsb.subject_code,tsb.subject_name,tcc.course_name,tp.college_id,tu.role_id,tp.subject_id,tp.id,tu.email,tu.user_name,tu.mobile_no,\n" +
                "                tp.status_date, tp.appointment_sent_date,tp.current_status, tp.is_appointment_sent,\n" +
                "                 tp.office_order_date, tp.last_date_to_submit, tp.no_of_sets\n" +
                "                FROM users tu, tbl_roles tr,tbl_appointments_bulk tp, tbl_college tc,tbl_subjects tsb,tbl_courses tcc\n" +
                "                WHERE tr.id=tu.role_id and tc.id = tp.college_id AND tu.id = tp.user_id" +
                " and tsb.id=tp.subject_id and tcc.id=tsb.course_id and tsb.section_id in "+sectionId+"";

        // Pass the list of IDs as parameters
        return queryUtil.list(query);
    }
}
