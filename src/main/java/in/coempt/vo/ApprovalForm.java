package in.coempt.vo;


import com.sun.istack.NotNull;
import lombok.Data;

@Data
public class ApprovalForm {

    @NotNull
    private Long qpointId;

    @NotNull
    private String approved;

    private String comments;
}

