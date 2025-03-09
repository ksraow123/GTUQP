package in.coempt.dao;

import in.coempt.entity.Appointment;
import in.coempt.util.QueryUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BulkAppointmentDao
{
    public List<Appointment> getAllAppointments() {
        QueryUtil<Appointment> queryUtil = new QueryUtil<>(Appointment.class);
        return queryUtil.list("SELECT tp.*,tp.order_no as orderNo,ttc.course_code,ts.subject_code,tr.role," +
                "tc.college_code FROM tbl_appointments tp,tbl_college tc ,tbl_roles tr," +
                "tbl_subjects ts , tbl_courses ttc where tp.subject_id=ts.id and " +
                "tp.course_id=ttc.id and tp.role_id=tr.id and tc.id=tp.college_id");
    }
}
