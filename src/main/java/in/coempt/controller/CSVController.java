package in.coempt.controller;

import in.coempt.entity.*;
import in.coempt.repository.FacultyAppointmentRepository;
import in.coempt.repository.UserRepository;
import in.coempt.service.*;
import in.coempt.util.SecurityUtil;
import in.coempt.vo.AppointmentVo;
import in.coempt.vo.SessionDataVo;
import in.coempt.vo.SetterModeratorMappingVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class CSVController {

    private final CSVService csvService;
    private final AppointmentService appointmentService;
    private final SetterModeratorService setterModeratorService;
    private final CourseService courseService;

    private final FacultyAppointmentRepository facultyAppointmentRepository;

    private final SubjectsService subjectsService;
    @GetMapping("/upload")
    public String showUploadForm(Model model, HttpSession session) {
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
                .map(course -> String.valueOf(course.getId())).distinct()
                .collect(Collectors.toList());
                    if (sessionDataVo.getRoleId() == 1) {

                courseList = courseList.stream().filter(a -> Math.toIntExact(a.getSection_id()) == sectionIds.get(0)).collect(Collectors.toList());
                courseIds = courseList.stream()
                        .map(course -> String.valueOf(course.getId())).distinct()
                        .collect(Collectors.toList());
            }
        List<Subjects> subjectsList=subjectsService.getSubjectsByCourseIdList(courseIds);
        List<String> semesterList=subjectsList.stream().map(Subjects::getSemester).distinct().collect(Collectors.toList());
        model.addAttribute("coursesList", courseList);
        model.addAttribute("subjectsList", subjectsList);
        model.addAttribute("semesterList", semesterList);


        List<FacultyAppointment> f1= facultyAppointmentRepository.findBySectionUserId(sessionDataVo.getUserId());
        model.addAttribute("uploadFileResults", f1);
       // model.addAttribute("appointmentList",appointmentService.getAppointmentDshBoardBySection(sessionIds));


        model.addAttribute("page","uploadNew");
        return "main";

    }
    @PostMapping("/uploadFilters")
    public String showUploadFormFilters(
            @RequestParam("course_id") String courseId,
            @RequestParam("review_status") String review_status,
            @RequestParam("submit_status") String submit_status,
            @RequestParam("subject_id") String subjectId,
            @RequestParam("semester") String semester,@RequestParam("appointment_type") String appointment_type,Model model, HttpSession session) {
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
                .map(course -> String.valueOf(course.getId())).distinct()
                .collect(Collectors.toList());
        if(sessionDataVo.getRoleId()==1) {


                courseList = courseList.stream().filter(a -> Math.toIntExact(a.getSection_id()) == sectionIds.get(0)).collect(Collectors.toList()).stream()
                        .sorted(Comparator.comparing(Course::getCourse_name)) // Sort by name
                        .distinct() // Remove duplicates (won't work well for objects)
                        .collect(Collectors.toList());
                courseIds = courseList.stream()
                        .map(course -> String.valueOf(course.getId())).sorted().distinct()
                        .collect(Collectors.toList());

        }
        List<Subjects> subjectsList=subjectsService.getSubjectsByCourseIdList(courseIds);
        List<String> semesterList=subjectsList.stream().map(Subjects::getSemester).sorted().distinct().collect(Collectors.toList());
        model.addAttribute("coursesList", courseList);
        model.addAttribute("subjectsList", subjectsList);
        model.addAttribute("semesterList", semesterList);
        List<AppointmentVo> adminDashBoardVoList=  appointmentService.getAppointmentDshBoardBySection(sessionIds);

        adminDashBoardVoList.removeIf(a ->
                (!submit_status.equalsIgnoreCase("All")&&!a.getSetter_status().equals(submit_status)||
                !review_status.equalsIgnoreCase("All")&&!a.getReview_status().equals(review_status)||
                        !appointment_type.equalsIgnoreCase("All")&&!a.getCurrent_status().equals(appointment_type)||
                        !courseId.equalsIgnoreCase("All") && a.getCourse_id() != Integer.valueOf(courseId)) ||
                        (!subjectId.equalsIgnoreCase("All") && Integer.valueOf(a.getSubject_id()) != Integer.valueOf(subjectId)) ||
                        (!semester.equalsIgnoreCase("All") && Integer.valueOf(a.getSemester()) != Integer.valueOf(semester))
        );

        model.addAttribute("appointmentList",adminDashBoardVoList);
        model.addAttribute("page","uploadNew");
        return "main";

    }
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload.");
            model.addAttribute("page","uploadNew");
            return "main";
        }

        try {
            ArrayList<Object> arrayList= csvService.saveCSV(file);
            redirectAttributes.addFlashAttribute("successList", arrayList.get(0));
            redirectAttributes.addFlashAttribute("failureList", arrayList.get(1));
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to upload the file, check the column headings as per .csv file  "+ e.getMessage());
        }
//model.addAttribute("page","upload");
        return "redirect:/upload";
    }

    @GetMapping("/setterModeratorMapping")
    public String setterModeratorMapping(Model model) {
        List<SetterModeratorMappingVo> list=setterModeratorService.getModeratorSetterMappingDetails();
        model.addAttribute("setterModeratorMappingList",list);
        model.addAttribute("page","mappingFileUpload");
        return "main";
    }

}
