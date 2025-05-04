package in.coempt.entity;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "faculty_appointment")
@Data
public class FacultyAppointment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="section_user_id")
    private Long sectionUserId;

    private String user_id, staffname, collegecode, instituteaddress, course, coursename, email, contact, designation, department, totalexperience, subjectcode, lastdatetosubmit,  college_id, course_Id, subject_id,  record_status, user_coll_id_grp, lastdatetosubmit_ch;


}
