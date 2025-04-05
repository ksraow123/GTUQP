package in.coempt.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "qp_set_bit_wise_questions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"exam_series_id", "subject_id", "q_desc"}))
@Data
public class BitwiseQuestions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_id", nullable = false)
    private int subjectId;

    @Column(name = "set_no")
    private int setNo;

    @Column(name = "qp_reviewer_id")
    private Integer qpReviewerId;

    @Column(name = "qp_setter_id")
    private Long qpSetterId;

    @Column(name = "exam_series_id", nullable = false)
    private int examSeriesId;

    @Column(name = "q_no")
    private Integer q_no;

    @Column(name = "q_desc")
    private String q_desc;

    private String q_solution;
    private String reviewer_comments;
    private String bit_no;
    private String level;
    private String instructions;
    private Integer marks;
    private String image_path;
    private String qp_setter_status;
    private String topic;
    private String qp_reviewer_status;
    private String last_updated_by;
}
