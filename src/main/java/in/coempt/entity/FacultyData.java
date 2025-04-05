package in.coempt.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_faculty_data")
@Data
public class FacultyData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "mobile_number", unique = true)
    private String mobileNumber;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "college_code")
    private String collegeCode;

    @Column(name = "teaching_exp")
    private String teachingExp;

    @Column(name = "industry_exp")
    private String industryExp;

    @Column(name = "designation")
    private String designation;

    @Column(name = "faculty_type")
    private String facultyType;
    @Column(name = "department")
    private String department;
    @Column(name = "institute_address")
    private String instituteAddress;
    @Column(name = "course")
    private String course;
    @Column(name = "course_name")
    private String courseName;
    @Column(name = "total_exp")
    private String totalExp;

    @Column(name = "created_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;
}
