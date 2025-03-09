package in.coempt.controller;

import in.coempt.entity.FacultyData;
import in.coempt.service.FacultyDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api/faculty")
public class FacultyDataController {

    @Autowired
    private FacultyDataService facultyDataService;

    @GetMapping("/getAllFaculty")
    public String getAllFaculties(Model model) {
        List<FacultyData> facultyDataList =facultyDataService.getAllFaculties();
        model.addAttribute("facultyDataList",facultyDataList);
        model.addAttribute("page","facultyData");
        return "main";

    }

    @GetMapping("/{id}")
    @ResponseBody
    public FacultyData getFacultyById(@PathVariable Integer id) {
        return facultyDataService.getFacultyById(id).get();
    }

    @GetMapping("/mobile/{mobileNumber}")
    @ResponseBody
    public FacultyData getFacultyByMobileNumber(@PathVariable String mobileNumber) {
        return facultyDataService.getFacultyByMobileNumber(mobileNumber).get();
    }


    @DeleteMapping("/{id}")
    public String deleteFaculty(@PathVariable Integer id) {
        facultyDataService.deleteFaculty(id);
        return "Faculty with ID " + id + " deleted successfully!";
    }
    @PostMapping("/upload")
    public String uploadCSV(@RequestParam("file") MultipartFile file, Model model,RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            model.addAttribute("err","Please upload a CSV file!");
            List<FacultyData> facultyDataList =facultyDataService.getAllFaculties();
            model.addAttribute("facultyDataList",facultyDataList);
            model.addAttribute("page","facultyData");
            return "main";
        }

        ArrayList<Object> arrayList= facultyDataService.saveCSV(file);
        redirectAttributes.addFlashAttribute("successList", arrayList.get(0));
        redirectAttributes.addFlashAttribute("failureList", arrayList.get(1));
        return "redirect:/api/faculty/getAllFaculty";
    }


}

