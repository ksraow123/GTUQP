package in.coempt.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MenuHeaderBean  implements Serializable {
    private String id, headername, menuorder, bootstrapclass_hed, role_id;

}