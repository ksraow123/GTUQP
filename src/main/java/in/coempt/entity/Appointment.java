package in.coempt.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tbl_appointments")
@Data
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int role_id;
    @Column(nullable = false, unique = true, length = 45)
    private String mobile_number;
    private String name;
    @Column(nullable = false, unique = true, length = 80)
    private String email;
    private String college_id;
    private String course_id;
    private String subject_id;
    private String order_date;
    private String submission_date;
    @Column(name="order_no",nullable = false, unique = true, length = 80)
    private String orderNo;
    private String current_status;
    private String status_date;
    private String no_of_sets;
    private String regulation;
@Transient
    private String course_code,subject_code,role,college_code;

}
