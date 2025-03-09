package in.coempt.service.impl;

import in.coempt.dao.ReportDao;
import in.coempt.service.ReportService;
import in.coempt.vo.AdminDashBoardVo;
import in.coempt.vo.RemunerationReportVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportDao reportDao;
    @Override
    public List<AdminDashBoardVo> getAdminDashBoard() {
        return reportDao.getAdminDashBoard();
    }
    @Override
    public List<AdminDashBoardVo> getSubjectWiseAdminDashBoard() {
        return reportDao.getSubjectWiseAdminDashBoard();
    }

    @Override
    public List<RemunerationReportVo> getRemunerationReport() {

        return reportDao.getRemunerationReport();

    }
}
