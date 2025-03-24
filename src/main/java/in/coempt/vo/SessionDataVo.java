package in.coempt.vo;


import lombok.Data;

import java.util.List;
@Data
public class SessionDataVo {

    private Long userId;
    private List<Integer> sectionId;
    private Integer examSeriesId;
    private Long roleId;
}
