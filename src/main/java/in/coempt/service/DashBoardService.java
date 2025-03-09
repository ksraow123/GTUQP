package in.coempt.service;

import in.coempt.vo.QPSetterDashBoardVo;

import java.util.List;

public interface DashBoardService {
    public List<QPSetterDashBoardVo> getQPSetterDashBord(String userName, Long userId);

    public List<QPSetterDashBoardVo> getQPModeratorDashBord(String userName, Long userId);

    public List<QPSetterDashBoardVo> getSetWiseQPDashBoard(Long userId);

    List<QPSetterDashBoardVo> getSetWiseReviewerQPDashBoard(String userName, Long id);
}
