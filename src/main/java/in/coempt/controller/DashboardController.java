package in.coempt.controller;

import in.coempt.entity.*;
import in.coempt.repository.QpTemplateMasterRepository;
import in.coempt.repository.UserRepository;
import in.coempt.service.*;
import in.coempt.util.SecurityUtil;
import in.coempt.vo.QPSetterDashBoardVo;
import in.coempt.vo.SectionTeamDashBoard;
import in.coempt.vo.SessionDataVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {
    @Autowired
    private DashBoardService dashBoardService;
    @Autowired
    private BitwiseQuestionsService bitwiseQuestionsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QpTemplateMasterRepository qpTemplateMasterRepository;
    @Autowired
    private SubjectsService subjectsService;
    @Autowired
    private SectionUserMappingService sectionUserMappingService;
    @Autowired
    private CourseService courseService;
    @Autowired
   private QPFilesService qpFilesService;

    @GetMapping("/setterdashboard")
    // @Transactional
    public String userDashBoard(Model model, HttpSession httpSession) {
        try {
            UserDetails user = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
            User userEntity = userRepository.findByUserName(user.getUsername());

            List<QPSetterDashBoardVo> qpSetterDashBoardList = dashBoardService.getQPSetterDashBord(userEntity.getUserName(), userEntity.getId());
            List<QPSetterDashBoardVo> setwiseStatusReport = new ArrayList<>();
            for (QPSetterDashBoardVo qpSetterDashBoardVo : qpSetterDashBoardList) {
                try {
                    int noOfSets = qpSetterDashBoardVo.getNo_of_sets();
                    int subjectId = qpSetterDashBoardVo.getSubject_id();
                    //       Integer examSeriesId=   (Integer) httpSession.getAttribute("selectedExamSeriesId");
                    Subjects subject = subjectsService.getSubjectById(subjectId + "");
                    for (int setNo = 1; setNo <= noOfSets; setNo++) {
                        List<BitwiseQuestions> existingQuestions = bitwiseQuestionsService.getQpTemplateQuestions(userEntity.getId(), String.valueOf(subjectId), setNo);
                        List<BitwiseQuestions> questionsList = new ArrayList<>();

                        if (existingQuestions.isEmpty()) {
                            List<QpTemplateMaster> qpTemplateMasterList = qpTemplateMasterRepository.findByPatternCode(subject.getPatternCode());

                            for (QpTemplateMaster template : qpTemplateMasterList) {
                                BitwiseQuestions bitwiseQuestions = new BitwiseQuestions();
                                bitwiseQuestions.setQpSetterId(userEntity.getId());
                                bitwiseQuestions.setSubjectId(subjectId);
                                bitwiseQuestions.setQ_no(template.getQNo());
                                bitwiseQuestions.setLevel(template.getLevel());
                                bitwiseQuestions.setTopic(template.getTopic());
                                bitwiseQuestions.setBit_no(template.getBitNo());
                                bitwiseQuestions.setMarks(template.getMarks());
                                bitwiseQuestions.setSetNo(setNo);
                                bitwiseQuestions.setExamSeriesId(1);
                                questionsList.add(bitwiseQuestions);
                            }
                            if (!questionsList.isEmpty()) {
                                bitwiseQuestionsService.saveBitWiseQuestions(questionsList);
                            }
                        }
                    }
                    List<QPSetterDashBoardVo> setwiseDashBoardList = dashBoardService.getSetterStatusReport(1, subjectId, userEntity.getId());
                    setwiseStatusReport.addAll(setwiseDashBoardList);
                } catch (NumberFormatException e) {

                } catch (Exception e) {

                }
            }

            List<QPSetterDashBoardVo> setwiseDashBoard = dashBoardService.getSetWiseQPDashBoard(userEntity.getId());
            for(QPSetterDashBoardVo setterDashBoardVo:setwiseDashBoard){
                if(setterDashBoardVo.getQp_status()!=null) {
                    if (setterDashBoardVo.getQp_status().equalsIgnoreCase("Re-Submit")) {

                        QPFilesEntity qpFilesEntity = qpFilesService.getQPFilesByRollIdAndSubjectIdAndSetNoAndSetterId(1, setterDashBoardVo.getSubject_id() + "", 1, userEntity.getId());
                        setterDashBoardVo.setRemarks(qpFilesEntity.getRemarks());

                    }
                }
            }



            // model.addAttribute("qpSetterDashBoardList", qpSetterDashBoardList);
            model.addAttribute("setwiseDashBoard", setwiseDashBoard);
            model.addAttribute("setwiseStatusReport", setwiseStatusReport);
            model.addAttribute("page", "setterDashBoard");

            return "main";
        } catch (Exception e) {

            model.addAttribute("error", "An unexpected error occurred. Please try again later.");
        }
        return "errorPage";
    }


    @GetMapping("/moderatordashboard")
    public String moderatorDashBoard(Model model, HttpSession session) {
        Integer examSeriesId = (Integer) session.getAttribute("selectedExamSeriesId");
        UserDetails user = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User userEntity = userRepository.findByUserName(user.getUsername());
        SessionDataVo sessionDataVo = (SessionDataVo) session.getAttribute("sessionData");
        List<Course> courseList = courseService.getAllCourses().stream()
                .sorted(Comparator.comparing(Course::getCourse_name)) // Sort by name
                .distinct() // Remove duplicates (won't work well for objects)
                .collect(Collectors.toList());
        List<String> courseIds = courseList.stream()
                .map(course -> String.valueOf(course.getId())).sorted().distinct()
                .collect(Collectors.toList());
        List<Integer> sectionIds = sessionDataVo.getSectionId();
        courseList = courseList.stream().filter(a -> Math.toIntExact(a.getSection_id()) == sectionIds.get(0)).collect(Collectors.toList()).stream()
                .sorted(Comparator.comparing(Course::getCourse_name)) // Sort by name
                .distinct() // Remove duplicates (won't work well for objects)
                .collect(Collectors.toList());
        courseIds = courseList.stream()
                .map(course -> String.valueOf(course.getId())).sorted().distinct()
                .collect(Collectors.toList());
        List<SectionTeamDashBoard> qpSectionDashBoardList =
                dashBoardService.getSectionDashBoard(Math.toIntExact(userEntity.getId()), examSeriesId);
        for (SectionTeamDashBoard setterDashBoardVo : qpSectionDashBoardList) {
            userRepository.findById(setterDashBoardVo.getUser_id())
                    .ifPresent(usr -> setterDashBoardVo.setSetter_details(
                            String.join("\n", usr.getUserName(),
                                    usr.getFirstName() + " " + usr.getLastName(),
                                    usr.getEmail(),
                                    usr.getMobileNo())
                    ));
        }
        List<Subjects> subjectsList = subjectsService.getSubjectsByCourseIdList(courseIds);
        List<String> semesterList = subjectsList.stream().map(Subjects::getSemester).distinct().collect(Collectors.toList());
        model.addAttribute("coursesList", courseList);
        model.addAttribute("subjectsList", subjectsList);
        model.addAttribute("semesterList", semesterList);

        model.addAttribute("qpSectionDashBoardList", qpSectionDashBoardList);

        model.addAttribute("page", "moderatorDashBoard");
        return "main";

    }

    @PostMapping("/moderatorDashBoardFilters")
    public String moderatorDashBoardFilters(
            @RequestParam("course_id") String courseId,
            @RequestParam("subject_id") String subjectId,
            @RequestParam("semester") String semester,@RequestParam("qpStatus") String qpStatus,Model model, HttpSession session) {
        Integer examSeriesId = (Integer) session.getAttribute("selectedExamSeriesId");
        UserDetails user = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User userEntity = userRepository.findByUserName(user.getUsername());
        SessionDataVo sessionDataVo = (SessionDataVo) session.getAttribute("sessionData");
        List<Course> courseList = courseService.getAllCourses().stream()
                .sorted(Comparator.comparing(Course::getCourse_name)) // Sort by name
                .distinct() // Remove duplicates (won't work well for objects)
                .collect(Collectors.toList());

        List<String> courseIds = courseList.stream()
                .map(course -> String.valueOf(course.getId())).sorted().distinct()
                .collect(Collectors.toList());
        List<Integer> sectionIds = sessionDataVo.getSectionId();
        courseList = courseList.stream().filter(a -> Math.toIntExact(a.getSection_id()) == sectionIds.get(0)).collect(Collectors.toList()).stream()
                .sorted(Comparator.comparing(Course::getCourse_name)) // Sort by name
                .distinct() // Remove duplicates (won't work well for objects)
                .collect(Collectors.toList());;
        courseIds = courseList.stream()
                .map(course -> String.valueOf(course.getId())).sorted().distinct()
                .collect(Collectors.toList());
        List<SectionTeamDashBoard> qpSectionDashBoardList =
                dashBoardService.getSectionDashBoard(Math.toIntExact(userEntity.getId()), examSeriesId);
        for (SectionTeamDashBoard setterDashBoardVo : qpSectionDashBoardList) {
            userRepository.findById(setterDashBoardVo.getUser_id())
                    .ifPresent(usr -> setterDashBoardVo.setSetter_details(
                            String.join("\n", usr.getUserName(),
                                    usr.getFirstName() + " " + usr.getLastName(),
                                    usr.getEmail(),
                                    usr.getMobileNo())
                    ));
        }
        List<Subjects> subjectsList = subjectsService.getSubjectsByCourseIdList(courseIds);
        List<String> semesterList = subjectsList.stream().map(Subjects::getSemester).distinct().collect(Collectors.toList());
        model.addAttribute("coursesList", courseList);
        model.addAttribute("subjectsList", subjectsList);
        model.addAttribute("semesterList", semesterList);
        qpSectionDashBoardList.removeIf(a ->
                (!qpStatus.equalsIgnoreCase("All") && !a.getQp_status().equals(qpStatus)) ||
                        (!courseId.equalsIgnoreCase("All") && !a.getCourse_id().equals(Integer.valueOf(courseId))) ||
                        (!subjectId.equalsIgnoreCase("All") && !Integer.valueOf(a.getSubject_id()).equals(Integer.valueOf(subjectId))) ||
                        (!semester.equalsIgnoreCase("All") && !Integer.valueOf(a.getSemester()).equals(Integer.valueOf(semester)))
        );
        model.addAttribute("qpSectionDashBoardList", qpSectionDashBoardList);

        model.addAttribute("page", "moderatorDashBoard");
        return "main";

    }
}