package in.coempt.entity;


import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tbl_pattern_qno_instructions")
@Data
public class PatternQnoInstructions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String  english_instructions, gujarti_instructions;
    @Column(name="pattern_code")
    private String  patternCode;
    @Column(name="q_no")
    private String qNo;
}
