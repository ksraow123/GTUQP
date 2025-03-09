package in.coempt.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "menuheader")
@Data
public class Menuheader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String  headername, menuorder, bootstrapclass_hed;
}
