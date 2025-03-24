package in.coempt.service;

import in.coempt.vo.AdminDashBoardVo;
import in.coempt.vo.RemunerationReportVo;

import java.util.List;

public interface ReportService {

    List<AdminDashBoardVo> getAdminDashBoard(Integer userId);
    List<AdminDashBoardVo> getSubjectWiseAdminDashBoard(Integer userId,String sessionIds);

    List<RemunerationReportVo> getRemunerationReport(Integer userId,String sectionIds);
}
