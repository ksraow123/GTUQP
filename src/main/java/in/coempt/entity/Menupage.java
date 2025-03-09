package in.coempt.entity;

import lombok.Data;

import javax.persistence.*;


@Entity
@Table(name = "menupage")
@Data
public class Menupage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String  url, itemname, isactive, purpose, modifieddate, menuorder, bootstrapclass_sub, role_id, headerid;
}
