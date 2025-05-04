package in.coempt.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tbl_qp_template_master")
@Data
public class QpTemplateMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "pattern_code")
    private String patternCode;

    @Column(name = "q_no")
    private String qNo;

    @Column(name = "bit_no")
    private String bitNo;

    @Column(name = "topic")
    private String topic;

    @Column(name = "level")
    private String level;
    @Column(name = "q_order")
    private Integer qorder;

    @Column(name = "marks")
    private String marks;
    private int q_flag;
    private String instructions;
}