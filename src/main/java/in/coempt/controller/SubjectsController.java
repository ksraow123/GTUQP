package in.coempt.controller;

import in.coempt.entity.Subjects;
import in.coempt.service.SubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin(origins = "*")
public class SubjectsController {
@Autowired
private SubjectsService subjectsService;

    @GetMapping("/subjects/{courseId}")
    @ResponseBody
    public List<Subjects> getSubjectsByCourse(@PathVariable String courseId) {
        return subjectsService.getSubjectsByCourseId(courseId);
    }
}
