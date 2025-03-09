package in.coempt.controller;

import in.coempt.entity.Appointment;
import in.coempt.service.ReportService;
import in.coempt.util.QueryUtil;
import in.coempt.vo.AdminDashBoardVo;
import in.coempt.vo.RemunerationReportVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ReportsController {
@Autowired
private ReportService reportService;
@GetMapping("/adminDashboard")
public String getAdminDashBoard(Model model) {

List<AdminDashBoardVo> adminDashBoardVoList=reportService.getAdminDashBoard();
    model.addAttribute("adminDashBoardVoList",adminDashBoardVoList);
    model.addAttribute("page","/report/admindashboard");
    return "main";
}

    @GetMapping("/subject/dashboard")
    public String getSubjectDashBoard(Model model) {

        List<AdminDashBoardVo> adminDashBoardVoList=reportService.getSubjectWiseAdminDashBoard();
        model.addAttribute("adminDashBoardVoList",adminDashBoardVoList);
        model.addAttribute("page","/report/subjectdashboard");
        return "main";
    }
 @GetMapping("/subject/remunerationReport")
    public String getRemunerationReport(Model model) {
        List<RemunerationReportVo> remunerationReports=reportService.getRemunerationReport();
        model.addAttribute("remunerationReports",remunerationReports);

        model.addAttribute("page","/report/remuneration");
        return "main";
    }

}
