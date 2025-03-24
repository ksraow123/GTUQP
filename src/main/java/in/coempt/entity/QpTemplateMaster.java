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

    @Column(name = "q_no", length = 45)
    private Integer qNo;

    @Column(name = "bit_no", length = 45)
    private String bitNo;

    @Column(name = "topic", length = 45)
    private String topic;

    @Column(name = "level", length = 45)
    private String level;

    @Column(name = "marks", length = 45)
    private String marks;
}