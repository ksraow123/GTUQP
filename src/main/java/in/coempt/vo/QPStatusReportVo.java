package in.coempt.vo;

import lombok.Data;

@Data
public class QPStatusReportVo {
    private String course_code, course_name, no_of_subjects, no_of_subjects_pending,appointments_sent, appointments_not_sent, no_of_subjects_submitted, no_of_subjects_not_submitted, no_of_total_subjects_pending, no_of_subjects_approved, no_of_subjects_rejected;
}
