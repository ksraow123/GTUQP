package in.coempt.vo;

import lombok.Data;

@Data
public class QPSetterDashBoardVo {
private int total_questions,pending_questions,setno,no_of_questions,subject_id, no_of_sets_reviewed, no_of_sets_approved,  no_of_sets, no_of_sets_uploaded, no_of_sets_forwarded;

    private  String syllabus,qp_status, subject_code, subject_name, course_name, year, semester,  submission_date;
}
