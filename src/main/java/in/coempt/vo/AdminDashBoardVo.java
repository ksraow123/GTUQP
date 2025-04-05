package in.coempt.vo;

import lombok.Data;

@Data
public class AdminDashBoardVo {
private String course_code, course_name, year, semester, subject_id, subject_code, subject_name, no_of_sets, set_no, qp_setter_id, setter_name, moderator_id, reviewer_name, qp_setter_status, qp_reviewer_status;
    private String  pattern_code,setter_pending,assigned_setters, assigned_moderators, setter_status, moderator_status, section_team_status, forward_to_repo_status;
private int  course_id,section_id,qp_set, no_of_setters, no_of_moderators, setter_completed, moderator_completed, moderator_rejected, moderator_pending,forward_to_repo_count;
}
