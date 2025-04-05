package in.coempt.controller;

import in.coempt.entity.FacultyData;
import in.coempt.entity.User;
import in.coempt.service.AppointmentService;
import in.coempt.service.FacultyDataService;
import in.coempt.service.UserService;
import in.coempt.vo.AppointmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/api/faculty")
public class FacultyDataController {

    @Autowired
    private FacultyDataService facultyDataService;
    @Autowired
    private  AppointmentService appointmentService;
    @Autowired
    private  UserService userService;
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
    public ResponseEntity<Map<String, Object>> getFacultyByMobileNumber(@PathVariable String mobileNumber) {

        User u1=userService.getUserByMobileNoAndRoleId(mobileNumber,2);


      //  Optional<FacultyData> facultyOptional = facultyDataService.getFacultyByMobileNumber(mobileNumber);
        List<AppointmentVo> appointmentVos = appointmentService.getAppointmentDshBoard(mobileNumber);

        if (u1==null) {  // ✅ Java 8 way to check if Optional is empty
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Faculty data not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }


        // ✅ Create structured JSON response
        Map<String, Object> response = new HashMap<>();
        response.put("faculty", u1);
        response.put("appointments", appointmentVos);

        return ResponseEntity.ok(response);
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

