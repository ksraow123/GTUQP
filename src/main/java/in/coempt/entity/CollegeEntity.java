package in.coempt.entity;


import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tbl_college")
@Data
public class CollegeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String  college_name, college_address;
    @Column(name="college_code")
    private String collegeCode;
}
