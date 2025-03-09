package in.coempt.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="qp_set_bit_wise_questions")
@Data
public class BitwiseQuestions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="subject_id")
    private int subjectId;

    @Column(name="set_no")
    private int setNo;

    @Column(name="qp_reviewer_id")
    private Integer qpReviewerId;

    @Column(name="qp_setter_id")
    private Long qpSetterId;

    private Integer q_no;
    private String q_solution,reviewer_comments, language,bit_no,level, instructions, marks, q_desc, image_path, qp_setter_status,topic, qp_reviewer_status,last_updated_by;

}
