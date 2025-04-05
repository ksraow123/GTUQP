package in.coempt.vo;

import lombok.Data;

@Data
public class QPSetterDashBoardVo {
private int total_questions,pending_questions,setno,no_of_questions,subject_id, user_id,no_of_sets_reviewed, no_of_sets_approved,  no_of_sets, no_of_sets_uploaded, no_of_sets_forwarded;

    private  String lastDateStatus, setter_details,course_name, year, semester, subject_code, subject_name, user_name, role, old_qp_status, new_qp_status, qp_status_date, remarks, syllabus,qp_status,  submission_date;
}
