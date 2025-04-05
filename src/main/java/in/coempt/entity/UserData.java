package in.coempt.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "tbl_appointments_bulk",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_user_subject_sets_role",
                columnNames = { "user_id", "subject_id", "role_id" }
        )
)
public class UserData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="user_id")
    private int userId;
    @Column(name="subject_id")
    private int subjectId;
    private int no_of_sets;
    private int role_id;
    @Column(name="exam_series_id")
    private int examSeriesId;
    private String office_order_date;
    private String last_date_to_submit;
    private String college_id;
    private String curriculam;
    private String status_date;
    private String current_status;
    private String is_appointment_sent;
    private String appointment_sent_date;
    private String faculty_type;
    private String qp_instruction;

   // private String staff_name,institute_address,course,course_name,email,contact,designation,department,total_exp;




}
