package in.coempt.controller;

import in.coempt.entity.ExamSeries;
import in.coempt.entity.SectionUserMappingEntity;
import in.coempt.entity.SectionsMasterEntity;
import in.coempt.entity.User;
import in.coempt.repository.UserRepository;
import in.coempt.service.ExamSeriesService;
import in.coempt.service.SectionMasterService;
import in.coempt.service.SectionUserMappingService;
import in.coempt.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/examSeries")
public class ExamSeriesController {

    private final ExamSeriesService examSeriesService;
    private final UserRepository userRepository;
private final SectionMasterService sectionMasterService;
private final SectionUserMappingService sectionUserMappingService;
    @GetMapping("/dashboard")
    public String getActiveExamSeries(Model model) {

        UserDetails user=(UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User userEntity = userRepository.findByUserName(user.getUsername());
        SectionUserMappingEntity mappingEntity=
                sectionUserMappingService.getMappingDetailsByUserid(Math.toIntExact(userEntity.getId()));
        SectionsMasterEntity sectionsMaster=
                sectionMasterService.getMappingDetailsById(mappingEntity.getSectionId());
       List<ExamSeries> examSeriesList= examSeriesService.getAllActiveExamSeries();
       model.addAttribute("examSeriesList",examSeriesList);
       model.addAttribute("mappingEntity",mappingEntity);
       model.addAttribute("sectionsMaster",sectionsMaster);
        return "examSeriesDashBoard";
    }

        @GetMapping("/selectExamSeries")
        public String setExamSeriesInSession(@RequestParam("series_id") Integer seriesId, HttpSession session) {
            session.setAttribute("selectedExamSeriesId", seriesId);
            return "redirect:/subject/dashboard"; // Redirect to the dashboard or another page
        }

    }
