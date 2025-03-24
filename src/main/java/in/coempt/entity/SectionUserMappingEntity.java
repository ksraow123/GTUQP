package in.coempt.entity;


import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tbl_section_user_mapping")
@Data
public class SectionUserMappingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;
    @Column(name = "section_id")
    private Integer sectionId;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "role_id")
    private Integer roleId;
            private Integer is_active;

}
