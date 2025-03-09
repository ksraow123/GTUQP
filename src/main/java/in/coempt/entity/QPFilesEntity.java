package in.coempt.entity;

import lombok.Data;

import javax.persistence.*;


@Entity
@Table(name = "tbl_qp_files")
@Data
public class QPFilesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="user_id")
    private Long userId;
    @Column(name="set_no")
    private int setNo;
    @Column(name="role_id")
    private int rollId;
    @Column(name="subject_id")
    private Long subjectId;
    private String qp_status, qp_status_date,remarks;
}
