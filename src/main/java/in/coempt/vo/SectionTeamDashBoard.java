package in.coempt.vo;

import lombok.Data;

@Data
public class SectionTeamDashBoard {
private Long qp_setter_id,user_id;
    private Integer course_id;
    private String remarks,setter_details,subject_id,year,semester,course_name,syllabus, subject_code, subject_name, qp_status, qp_status_date;
}
