package in.coempt.entity;


import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tbl_section_master")
@Data
public class SectionsMasterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String section_name;
    private String description;
private String  sub_section, no_of_subjects, no_of_patterns;
}
