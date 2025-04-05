package in.coempt.controller;

import in.coempt.dao.QPInventoryDao;
import in.coempt.entity.QpInventoryEntity;
import in.coempt.repository.UserRepository;
import in.coempt.service.ProfileDetailsService;
import in.coempt.service.QpInventoryService;
import in.coempt.vo.ProfileDetailsVo;
import in.coempt.vo.QPInventoryVo;
import in.coempt.vo.SessionDataVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class QPFilesInventoryController {

    private final QPInventoryDao qpInventoryDao;
    private final QpInventoryService inventoryService;
    private final UserRepository userRepository;
    private final ProfileDetailsService profileDetailsService;
    @GetMapping("/QPInventorySummary")
    public String getQPInventoryFiles(Model model, HttpSession session) {
        SessionDataVo sessionDataVo=(SessionDataVo)session.getAttribute("sessionData");
        List<Integer> sectionIds = sessionDataVo.getSectionId();
        String sessionIds = sectionIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "(", ")"));
        List<QPInventoryVo> inventoryVoList=qpInventoryDao.getInventorySummary(sessionIds,sessionDataVo.getExamSeriesId());
for(QPInventoryVo qpInventoryVo:inventoryVoList){
    if(qpInventoryVo.getIs_used()==1){
        qpInventoryVo.setQpr_status("Ready");
        userRepository.findById(qpInventoryVo.getSetter_user_id())
                .ifPresent(userRecord -> {
                    ProfileDetailsVo profileDetails = profileDetailsService.getProfileDetails(userRecord.getId());
                    StringBuilder setterDetails = new StringBuilder();
                    setterDetails.append("<p>")
                            .append("<b>User Name:</b> ").append(userRecord.getUserName()).append("<br>\n")
                            .append("<b>Name:</b> ").append(userRecord.getFirstName()).append(" ").append(userRecord.getLastName()).append("<br>\n")
                            .append("<b>Mobile:</b> ").append(userRecord.getMobileNo()).append("<br>\n")
                            .append("<b>Email:</b> ").append(userRecord.getEmail()).append("<br>\n")
                            .append("<b>Designation:</b> ").append(profileDetails.getDesignation()).append("<br>\n")
                            .append("<b>Faculty Type:</b> ").append(profileDetails.getFaculty_type()).append("<br>\n")
                            .append("<b>Teaching Exp:</b> ").append(profileDetails.getTeaching_experience()).append("<br>\n")
                            .append("<b>Industry Exp:</b> ").append(profileDetails.getIndustry_experience()).append("<br>\n</p>");
                    qpInventoryVo.setSetter_details(setterDetails.toString());
                });

    }
}

        model.addAttribute("inventoryVoList", inventoryVoList);
        model.addAttribute("page","/inventory/inventorySummary");
        return "main";

    }
    @GetMapping("/getAvailableQpFiles/{subjectId}")

    public String getAvailableQpFiles(@PathVariable("subjectId") Long subjectId, Model model) {
        List<QPInventoryVo> viewSubjectQPFiles=qpInventoryDao.getAvailableQpFiles(subjectId);
        List<QPInventoryVo> viewAvailableQps= viewSubjectQPFiles.stream().filter(p->p.getIs_used()==0).collect(Collectors.toList());
        for (QPInventoryVo qpInventoryVo : viewAvailableQps) {
            userRepository.findById(qpInventoryVo.getSetter_user_id())
                    .ifPresent(userRecord -> {
                        ProfileDetailsVo profileDetails = profileDetailsService.getProfileDetails(userRecord.getId());
                        StringBuilder setterDetails = new StringBuilder();
                        setterDetails.append("<p>")
                                .append("<b>User Name:</b> ").append(userRecord.getUserName()).append("<br>\n")
                                .append("<b>Name:</b> ").append(userRecord.getFirstName()).append(" ").append(userRecord.getLastName()).append("<br>\n")
                                .append("<b>Mobile:</b> ").append(userRecord.getMobileNo()).append("<br>\n")
                                .append("<b>Email:</b> ").append(userRecord.getEmail()).append("<br>\n")
                                .append("<b>Designation:</b> ").append(profileDetails.getDesignation()).append("<br>\n")
                                .append("<b>Faculty Type:</b> ").append(profileDetails.getFaculty_type()).append("<br>\n")
                                .append("<b>Teaching Exp:</b> ").append(profileDetails.getTeaching_experience()).append("<br>\n")
                                .append("<b>Industry Exp:</b> ").append(profileDetails.getIndustry_experience()).append("<br>\n</p>");
                        qpInventoryVo.setSetter_details(setterDetails.toString());
                    });
        }
        model.addAttribute("viewAvailableQps", viewAvailableQps);
        return "fragments/availableQps :: availableQpsTable"; // Return Thymeleaf fragment

    }
    @GetMapping("/getUsedQpFiles/{subjectId}")
    public String getUsedQpFiles(@PathVariable("subjectId") Long subjectId, Model model) {
        List<QPInventoryVo> viewSubjectQPFiles=qpInventoryDao.getAvailableQpFiles(subjectId);
        List<QPInventoryVo> viewAvailableQps= viewSubjectQPFiles.stream().filter(p->p.getIs_used()==1).collect(Collectors.toList());
        for (QPInventoryVo qpInventoryVo : viewAvailableQps) {
            userRepository.findById(qpInventoryVo.getSetter_user_id())
                    .ifPresent(userRecord -> {
                        ProfileDetailsVo profileDetails = profileDetailsService.getProfileDetails(userRecord.getId());
                        StringBuilder setterDetails = new StringBuilder();
                        setterDetails.append("<p>")
                                .append("<b>User Name:</b> ").append(userRecord.getUserName()).append("<br>\n")
                                .append("<b>Name:</b> ").append(userRecord.getFirstName()).append(" ").append(userRecord.getLastName()).append("<br>\n")
                                .append("<b>Mobile:</b> ").append(userRecord.getMobileNo()).append("<br>\n")
                                .append("<b>Email:</b> ").append(userRecord.getEmail()).append("<br>\n")
                                .append("<b>Designation:</b> ").append(profileDetails.getDesignation()).append("<br>\n")
                                .append("<b>Faculty Type:</b> ").append(profileDetails.getFaculty_type()).append("<br>\n")
                                .append("<b>Teaching Exp:</b> ").append(profileDetails.getTeaching_experience()).append("<br>\n")
                                .append("<b>Industry Exp:</b> ").append(profileDetails.getIndustry_experience()).append("<br>\n</p>");
                        qpInventoryVo.setSetter_details(setterDetails.toString());
                    });
        }

        model.addAttribute("usedQps", viewSubjectQPFiles);
        return "fragments/usedQps :: usedQpsTable"; // Return the Thymeleaf fragment
    }@GetMapping("/getTotalQpFiles/{subjectId}")
    public String getTotalQpFiles(@PathVariable("subjectId") Long subjectId, Model model) {
        List<QPInventoryVo> viewSubjectQPFiles=qpInventoryDao.getAvailableQpFiles(subjectId);
        for (QPInventoryVo qpInventoryVo : viewSubjectQPFiles) {
            userRepository.findById(qpInventoryVo.getSetter_user_id())
                    .ifPresent(userRecord -> {
                        ProfileDetailsVo profileDetails = profileDetailsService.getProfileDetails(userRecord.getId());

                        StringBuilder setterDetails = new StringBuilder();
                        setterDetails.append("<p>")
                                .append("<b>User Name:</b> ").append(userRecord.getUserName()).append("<br>\n")
                                .append("<b>Name:</b> ").append(userRecord.getFirstName()).append(" ").append(userRecord.getLastName()).append("<br>\n")
                                .append("<b>Mobile:</b> ").append(userRecord.getMobileNo()).append("<br>\n")
                                .append("<b>Email:</b> ").append(userRecord.getEmail()).append("<br>\n")
                                .append("<b>Designation:</b> ").append(profileDetails.getDesignation()).append("<br>\n")
                                .append("<b>Faculty Type:</b> ").append(profileDetails.getFaculty_type()).append("<br>\n")
                                .append("<b>Teaching Exp:</b> ").append(profileDetails.getTeaching_experience()).append("<br>\n")
                                .append("<b>Industry Exp:</b> ").append(profileDetails.getIndustry_experience()).append("<br>\n</p>");
                        qpInventoryVo.setSetter_details(setterDetails.toString());
                    });
        }

       // model.addAttribute("totalQps", viewSubjectQPFiles);
        model.addAttribute("totalQps", viewSubjectQPFiles);
        return "fragments/totalQps :: totalQpsTable";
    }
    @PostMapping("/submitSelectedQp")
    public String submitSelectedQp(@RequestParam("selectedQp") Long selectedQpId, RedirectAttributes redirectAttributes, HttpSession session) {
        SessionDataVo sessionDataVo=(SessionDataVo)session.getAttribute("sessionData");
        QpInventoryEntity inventoryEntity=inventoryService.getInventorySummaryById(selectedQpId);
        inventoryEntity.setIs_used(1);
        LocalDateTime now = LocalDateTime.now();

        // Define the formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        // Format the date-time
        String formattedDate = now.format(formatter);
        inventoryEntity.setUsed_date(formattedDate);
        inventoryEntity.setUsedSeriesId(sessionDataVo.getExamSeriesId());
        inventoryService.saveQPInventoryFile(inventoryEntity);

        redirectAttributes.addFlashAttribute("successMessage", "QP successfully submitted!");

        return "redirect:/QPInventorySummary";
    }

}
