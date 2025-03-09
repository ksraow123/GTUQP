package in.coempt.vo;

import lombok.Data;

@Data
public class AdminDashBoardVo {

    private String course_name, year, semester, subject_code, subject_name, assigned_setters, assigned_moderators, setter_status, moderator_status, section_team_status, forward_to_repo_status;
private int  qp_set, no_of_setters, no_of_moderators, setter_completed, moderator_completed, moderator_rejected, moderator_pending,forward_to_repo_count;
}
