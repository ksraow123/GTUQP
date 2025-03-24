package in.coempt.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tbl_profile_details",uniqueConstraints = @UniqueConstraint(
        name = "user_id",
        columnNames = { "user_id" } ))
@Data
public class ProfileDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String account_no;
    private String ifsc_code;
    private String bank_name;
    private String   branch_details,branch_address;
    private String designation;
    private String teaching_experience;
    private Long user_id;
    private String industry_experience;
    private String residential_address;

}
