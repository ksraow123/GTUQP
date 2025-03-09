package in.coempt.entity;

import lombok.Data;

import javax.persistence.*;


@Entity
@Table(name = "qp_set_qp_template")
@Data
public class QPTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String  instructions;
    @Column(name="subject_id")
    private int subjectId;
    private int question_no, total_no_of_bits, no_of_bits_answer,marks;

}
