package in.coempt.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tbl_subjects")
@Data
public class Subjects {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String exam_code,qp_instructions, subject_name, regulation, subject_type, added_by, added_date, is_active, max_marks,  year, semester, max_units, group_code, qp_set, print_code, publish_print;
    @Column(name = "course_id")
    private String courseId;
    @Column(name = "subject_code")
    private String subjectCode;
    private String syllabus;
    private Long section_user_id;
}
