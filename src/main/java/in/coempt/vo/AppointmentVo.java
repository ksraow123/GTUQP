package in.coempt.vo;

import lombok.Data;

@Data
public class AppointmentVo {

    private int role_id,subject_id,college_id,id,user_id;
    private String email,user_name,first_name,last_name, status_date, current_status,college_code,college_name, is_appointment_sent,office_order_date, last_date_to_submit, no_of_sets;

private String syllabus,role,subject_code,subject_name, course_name, appointment_sent_date, mobile_no;


}
