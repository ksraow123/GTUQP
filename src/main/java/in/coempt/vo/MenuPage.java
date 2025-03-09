package in.coempt.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class MenuPage implements Serializable {

    private int id;
    private String url;
    private String itemname;
    private int isactive;
    private String purpose;
    private String modifieddate;
    private String role;
    private String menunorder;
    private String bootstrap_sub;
    private String bootstrapclass_sub;
    private String headerid;
}