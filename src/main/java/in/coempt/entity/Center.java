package in.coempt.entity;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "tbl_centers")
public class Center {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob
    @Column(name = "center_name")
    private String centerName;

    @Column(name = "center_capacity")
    private String centerCapacity;

    @Column(name = "from_roll_numbers")
    private String fromRollNumbers;

    @Column(name = "to_roll_numbers")
    private String toRollNumbers;

    // Getters and Setters
}
