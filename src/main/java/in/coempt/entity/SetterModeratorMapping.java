
package in.coempt.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(
        name = "tbl_setter_moderator_mapping",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"subject_id", "setter_id"})
        }
)
@Data
public class SetterModeratorMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "moderator_id", nullable = false)
    private Long moderatorId;

    @Column(name = "setter_id", nullable = false)
    private Long setterId;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;
    private String assigned_date, assigned_by;
}
