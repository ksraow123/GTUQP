package in.coempt.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="qp_upload_details")
public class QpUploadDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String  reviewer_comments,qp_forwarded_date,qp_file,qp_setter_status, qp_setter_uploaded_date,  qp_reviewer_status, qp_reviewed_date;
    @Column(name="subject_id")
    private int subjectId;
    @Column(name="set_no")
    private int setNo;
    private Integer qp_setter_id, qp_reviewer_id;
}
