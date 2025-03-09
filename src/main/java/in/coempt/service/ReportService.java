package in.coempt.service;

import in.coempt.vo.AdminDashBoardVo;
import in.coempt.vo.RemunerationReportVo;

import java.util.List;

public interface ReportService {

    List<AdminDashBoardVo> getAdminDashBoard();
    List<AdminDashBoardVo> getSubjectWiseAdminDashBoard();

    List<RemunerationReportVo> getRemunerationReport();
}
