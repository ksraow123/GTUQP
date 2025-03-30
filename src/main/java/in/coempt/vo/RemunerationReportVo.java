package in.coempt.vo;

import lombok.Data;

@Data
public class RemunerationReportVo {
    private int course_id;
    private String subject_id,semester,branch_address,user_name, name, role, subject_code, subject_name, no_of_sets, no_of_sets_completed, account_no, ifsc_code, bank_name, branch_details;
    private String  faculty_type,first_name, last_name, mobile_no, college_code, college_name,  designation,setsCompleted,  teaching_experience, industry_experience, residential_address;

}
