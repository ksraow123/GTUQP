package in.coempt.controller;

import in.coempt.entity.SectionUserMappingEntity;
import in.coempt.entity.Subjects;
import in.coempt.entity.User;
import in.coempt.repository.UserRepository;
import in.coempt.service.ExamSeriesService;
import in.coempt.service.SectionMasterService;
import in.coempt.service.SectionUserMappingService;
import in.coempt.service.SubjectsService;
import in.coempt.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SubjectsController {

private final SubjectsService subjectsService;

    private final UserRepository userRepository;
    private final SectionUserMappingService sectionUserMappingService;
    @GetMapping("/subjects/{courseId}")
    @ResponseBody
    public List<Subjects> getSubjectsByCourse(@PathVariable String courseId) {

        return subjectsService.getSubjectsByCourseId(courseId);
    }
    @GetMapping("/getSemestersAndSubjects")
    @ResponseBody
    public List<String> getSemestersAndSubjects(@RequestParam String courseId) {

        List<Subjects> s1= subjectsService.getSubjectsByCourseId(courseId);

        return s1.stream().map(Subjects::getSemester).sorted().distinct().collect(Collectors.toList());
    }

    @GetMapping("/getSubjectsByCourseAndSemester")
    @ResponseBody
    public List<Subjects> getSubjectsByCourseAndSemester(@RequestParam String courseId, @RequestParam String semester) {

        return subjectsService.getSubjectsByCourseIdAndSemester(courseId,semester);

    }
}