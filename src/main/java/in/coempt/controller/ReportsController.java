package in.coempt.controller;

import in.coempt.entity.Course;
import in.coempt.entity.Subjects;
import in.coempt.repository.UserRepository;
import in.coempt.service.CourseService;
import in.coempt.service.ReportService;
import in.coempt.service.SectionUserMappingService;
import in.coempt.service.SubjectsService;
import in.coempt.vo.AdminDashBoardVo;
import in.coempt.vo.RemunerationReportVo;
import in.coempt.vo.SessionDataVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ReportsController {
    private final UserRepository userRepository;
    private final CourseService courseService;
    private final SectionUserMappingService sectionUserMappingService;
    private final SubjectsService subjectsService;
private final ReportService reportService;
@GetMapping("/adminDashboard")
public String getAdminDashBoard(Model model,HttpSession session) {
    SessionDataVo sessionDataVo=(SessionDataVo)session.getAttribute("sessionData");
    List<AdminDashBoardVo> adminDashBoardVoList=reportService.getAdminDashBoard(Math.toIntExact(sessionDataVo.getUserId()));
    List<Course> courseList=courseService.getAllCourses().stream()
            .sorted(Comparator.comparing(Course::getCourse_name)) // Sort by name
            .distinct() // Remove duplicates (won't work well for objects)
            .collect(Collectors.toList());
    List<String> courseIds = courseList.stream()
            .map(course -> String.valueOf(course.getId())).sorted().distinct()
            .collect(Collectors.toList());

    if (sessionDataVo.getRoleId() == 1) {
        List<Integer> sectionIds = sessionDataVo.getSectionId();
        courseList= courseList.stream().filter(a -> Math.toIntExact(a.getSection_id()) == sectionIds.get(0)).collect(Collectors.toList()).stream()
                .sorted(Comparator.comparing(Course::getCourse_name)) // Sort by name
                .distinct() // Remove duplicates (won't work well for objects)
                .collect(Collectors.toList());
        courseIds=courseList.stream()
                .map(course -> String.valueOf(course.getId())).sorted().distinct()
                .collect(Collectors.toList());

        if (!sectionIds.isEmpty()) {
            int sectionId = sectionIds.get(0);
            adminDashBoardVoList.removeIf(a -> a.getSection_id() != sectionId);
        }
    }
    List<Subjects> subjectsList=subjectsService.getSubjectsByCourseIdList(courseIds);
    List<String> semesterList=subjectsList.stream().map(Subjects::getSemester).distinct().collect(Collectors.toList());
    model.addAttribute("coursesList", courseList);
    model.addAttribute("subjectsList", subjectsList);
    model.addAttribute("semesterList", semesterList);
    model.addAttribute("adminDashBoardVoList",adminDashBoardVoList);
    model.addAttribute("page","/report/admindashboard");
    return "main";
}

    @PostMapping("/adminDashboard")
    public String getAdminDashBoardAfter(
            @RequestParam("course_id") String courseId,
            @RequestParam("subject_id") String subjectId,
            @RequestParam("semester") String semester,
            Model model,
            HttpSession session) {

        SessionDataVo sessionDataVo = (SessionDataVo) session.getAttribute("sessionData");
        List<Course> courseList=courseService.getAllCourses().stream()
                .sorted(Comparator.comparing(Course::getCourse_name)) // Sort by name
                .distinct() // Remove duplicates (won't work well for objects)
                .collect(Collectors.toList());
        List<String> courseIds = courseList.stream()
                .map(course -> String.valueOf(course.getId())).distinct()
                .collect(Collectors.toList());
        if (sessionDataVo == null) {
            model.addAttribute("error", "Session expired. Please log in again.");
            return "redirect:/login";
        }

        List<AdminDashBoardVo> adminDashBoardVoList = reportService.getAdminDashBoard(Math.toIntExact(sessionDataVo.getUserId()));

        if (sessionDataVo.getRoleId() == 1) {
            List<Integer> sectionIds = sessionDataVo.getSectionId();
            courseList= courseList.stream().filter(a -> Math.toIntExact(a.getSection_id()) == sectionIds.get(0)).collect(Collectors.toList()).stream()
                    .sorted(Comparator.comparing(Course::getCourse_name)) // Sort by name
                    .distinct() // Remove duplicates (won't work well for objects)
                    .collect(Collectors.toList());
            courseIds=courseList.stream()
                    .map(course -> String.valueOf(course.getId())).distinct()
                    .collect(Collectors.toList());
            if (!sectionIds.isEmpty()) {
                int sectionId = sectionIds.get(0);
                adminDashBoardVoList.removeIf(a -> a.getSection_id() != sectionId);
            }
        }

        // Apply filters based on the request parameters
        adminDashBoardVoList.removeIf(a ->
                (!courseId.equalsIgnoreCase("All") && a.getCourse_id() != Integer.valueOf(courseId)) ||
                        (!subjectId.equalsIgnoreCase("All") && Integer.valueOf(a.getSubject_id()) != Integer.valueOf(subjectId)) ||
                        (!semester.equalsIgnoreCase("All") && Integer.valueOf(a.getSemester()) != Integer.valueOf(semester))
        );
        List<Subjects> subjectsList=subjectsService.getSubjectsByCourseIdList(courseIds);
        List<String> semesterList=subjectsList.stream().map(Subjects::getSemester).distinct().collect(Collectors.toList());
        model.addAttribute("coursesList", courseList);
        model.addAttribute("semesterList", semesterList);
        model.addAttribute("subjectsList", subjectsList);
        model.addAttribute("adminDashBoardVoList", adminDashBoardVoList);
        model.addAttribute("page", "/report/admindashboard");

        return "main";
    }


    @GetMapping("/subject/dashboard")
    public String getSubjectDashBoard(Model model, HttpSession session) {
        SessionDataVo sessionDataVo=(SessionDataVo)session.getAttribute("sessionData");
        List<Integer> sectionIds = sessionDataVo.getSectionId();
        String sessionIds = sectionIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "(", ")"));
        List<Course> courseList=courseService.getAllCourses().stream()
                .sorted(Comparator.comparing(Course::getCourse_name)) // Sort by name
                .distinct() // Remove duplicates (won't work well for objects)
                .collect(Collectors.toList());
        List<String> courseIds = courseList.stream()
                .map(course -> String.valueOf(course.getId())).sorted().distinct()
                .collect(Collectors.toList());

        List<AdminDashBoardVo> adminDashBoardVoList=reportService.getSubjectWiseAdminDashBoard(Math.toIntExact(sessionDataVo.getUserId()),sessionIds);
            if (sessionDataVo.getRoleId() == 1) {

                courseList = courseList.stream().filter(a -> Math.toIntExact(a.getSection_id()) == sectionIds.get(0)).collect(Collectors.toList());
                courseIds = courseList.stream()
                        .map(course -> String.valueOf(course.getId())).sorted().distinct()
                        .collect(Collectors.toList());
            }

        List<Subjects> subjectsList=subjectsService.getSubjectsByCourseIdList(courseIds);
        List<String> semesterList=subjectsList.stream().map(Subjects::getSemester).sorted().distinct().collect(Collectors.toList());
        model.addAttribute("coursesList", courseList);
        model.addAttribute("subjectsList", subjectsList);
        model.addAttribute("semesterList", semesterList);
        model.addAttribute("adminDashBoardVoList",adminDashBoardVoList);
        model.addAttribute("page","/report/subjectdashboard");
        return "main";
    }


    @PostMapping("/subject/dashboard")
    public String getSubjectDashBoardAfter( @RequestParam("course_id") String courseId,
                                            @RequestParam("subject_id") String subjectId,
                                            @RequestParam("semester") String semester,Model model, HttpSession session) {
        SessionDataVo sessionDataVo=(SessionDataVo)session.getAttribute("sessionData");
        List<Integer> sectionIds = sessionDataVo.getSectionId();
        String sessionIds = sectionIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "(", ")"));
        List<Course> courseList=courseService.getAllCourses().stream()
                .sorted(Comparator.comparing(Course::getCourse_name)) // Sort by name
                .distinct() // Remove duplicates (won't work well for objects)
                .collect(Collectors.toList());
        List<String> courseIds = courseList.stream()
                .map(course -> String.valueOf(course.getId())).sorted().distinct()
                .collect(Collectors.toList());
        if (sessionDataVo.getRoleId() == 1) {
            courseList = courseList.stream().filter(a -> Math.toIntExact(a.getSection_id()) == sectionIds.get(0)).collect(Collectors.toList());
            courseIds = courseList.stream()
                    .map(course -> String.valueOf(course.getId())).sorted().distinct()
                    .collect(Collectors.toList());
        }

        List<Subjects> subjectsList=subjectsService.getSubjectsByCourseIdList(courseIds);
        List<String> semesterList=subjectsList.stream().map(Subjects::getSemester).sorted().distinct().collect(Collectors.toList());
        List<AdminDashBoardVo> adminDashBoardVoList=reportService.getSubjectWiseAdminDashBoard(Math.toIntExact(sessionDataVo.getUserId()),sessionIds);

        adminDashBoardVoList.removeIf(a ->
                (!courseId.equalsIgnoreCase("All") && a.getCourse_id() != Integer.valueOf(courseId)) ||
                        (!subjectId.equalsIgnoreCase("All") && Integer.valueOf(a.getSubject_id()) != Integer.valueOf(subjectId)) ||
                        (!semester.equalsIgnoreCase("All") && Integer.valueOf(a.getSemester()) != Integer.valueOf(semester))
        );

        model.addAttribute("coursesList", courseList);
        model.addAttribute("subjectsList", subjectsList);
        model.addAttribute("semesterList", semesterList);
        model.addAttribute("adminDashBoardVoList",adminDashBoardVoList);
        model.addAttribute("page","/report/subjectdashboard");
        return "main";
    }





 @GetMapping("/subject/remunerationReport")
    public String getRemunerationReport(Model model,HttpSession session) {

     SessionDataVo sessionDataVo=(SessionDataVo)session.getAttribute("sessionData");
     List<Integer> sectionIds = sessionDataVo.getSectionId();
     String sessionIds = sectionIds.stream()
             .map(String::valueOf)
             .collect(Collectors.joining(",", "(", ")"));
     List<Course> courseList=courseService.getAllCourses().stream()
             .sorted(Comparator.comparing(Course::getCourse_name)) // Sort by name
             .distinct() // Remove duplicates (won't work well for objects)
             .collect(Collectors.toList());
     List<String> courseIds = courseList.stream()
             .map(course -> String.valueOf(course.getId())).sorted().distinct()
             .collect(Collectors.toList());
     if (sessionDataVo.getRoleId() == 1) {
         courseList = courseList.stream().filter(a -> Math.toIntExact(a.getSection_id()) == sectionIds.get(0)).collect(Collectors.toList());
         courseIds = courseList.stream()
                 .map(course -> String.valueOf(course.getId())).sorted().distinct()
                 .collect(Collectors.toList());
     }

     List<Subjects> subjectsList=subjectsService.getSubjectsByCourseIdList(courseIds);
     List<String> semesterList=subjectsList.stream().map(Subjects::getSemester).sorted().distinct().collect(Collectors.toList());
     model.addAttribute("coursesList", courseList);
     model.addAttribute("subjectsList", subjectsList);
     model.addAttribute("semesterList", semesterList);
        List<RemunerationReportVo> remunerationReports=reportService.getRemunerationReport(Math.toIntExact(sessionDataVo.getUserId()),sessionIds);
        model.addAttribute("remunerationReports",remunerationReports);

        model.addAttribute("page","/report/remuneration");
        return "main";
    }
    @PostMapping("/subject/remunerationReport")
    public String getRemunerationReportAfter( @RequestParam("course_id") String courseId,
                                              @RequestParam("subject_id") String subjectId,
                                              @RequestParam("semester") String semester,Model model, HttpSession session) {

        SessionDataVo sessionDataVo=(SessionDataVo)session.getAttribute("sessionData");
        List<Integer> sectionIds = sessionDataVo.getSectionId();
        String sessionIds = sectionIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "(", ")"));
        List<Course> courseList=courseService.getAllCourses().stream()
                .sorted(Comparator.comparing(Course::getCourse_name)) // Sort by name
                .distinct() // Remove duplicates (won't work well for objects)
                .collect(Collectors.toList());
        List<String> courseIds = courseList.stream()
                .map(course -> String.valueOf(course.getId())).sorted().distinct()
                .collect(Collectors.toList());
        if (sessionDataVo.getRoleId() == 1) {
            courseList = courseList.stream().filter(a -> Math.toIntExact(a.getSection_id()) == sectionIds.get(0)).collect(Collectors.toList());
            courseIds = courseList.stream()
                    .map(course -> String.valueOf(course.getId())).sorted().distinct()
                    .collect(Collectors.toList());
        }

        List<Subjects> subjectsList=subjectsService.getSubjectsByCourseIdList(courseIds);
        List<String> semesterList=subjectsList.stream().map(Subjects::getSemester).sorted().distinct().collect(Collectors.toList());
        model.addAttribute("coursesList", courseList);
        model.addAttribute("subjectsList", subjectsList);
        model.addAttribute("semesterList", semesterList);
        List<RemunerationReportVo> remunerationReports=reportService.getRemunerationReport(Math.toIntExact(sessionDataVo.getUserId()),sessionIds);
        remunerationReports.removeIf(a ->
                (!courseId.equalsIgnoreCase("All") && a.getCourse_id() != Integer.valueOf(courseId)) ||
                        (!subjectId.equalsIgnoreCase("All") && Integer.valueOf(a.getSubject_id()) != Integer.valueOf(subjectId)) ||
                        (!semester.equalsIgnoreCase("All") && Integer.valueOf(a.getSemester()) != Integer.valueOf(semester))
        );


        model.addAttribute("remunerationReports",remunerationReports);


        model.addAttribute("page","/report/remuneration");
        return "main";
    }
}
