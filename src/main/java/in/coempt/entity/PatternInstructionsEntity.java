package in.coempt.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tbl_pattern_instructions")
@Data
public class PatternInstructionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="pattern_code")
    private String patternCode;
    private String instructions;
}