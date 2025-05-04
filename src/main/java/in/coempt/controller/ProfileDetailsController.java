package in.coempt.controller;

import in.coempt.entity.*;
import in.coempt.repository.ProfileDetailsRepository;
import in.coempt.repository.UserRepository;
import in.coempt.service.AppointmentService;
import in.coempt.service.CollegeService;
import in.coempt.service.FacultyDataService;
import in.coempt.service.ProfileDetailsService;
import in.coempt.util.SecurityUtil;
import in.coempt.vo.ProfileDetailsVo;
import in.coempt.vo.SessionDataVo;
import in.coempt.vo.UserProfileForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ProfileDetailsController {
    @Autowired
    private ProfileDetailsService profileDetailsService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollegeService collegeService;
    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ProfileDetailsRepository profileDetailsRepository;

    @Autowired
    private FacultyDataService facultyDataService;

    @GetMapping("/viewProfile")
    public String getProfileDetails(Model model, HttpSession session) {
        UserDetails user = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User userEntity = userRepository.findByUserName(user.getUsername());
        SessionDataVo sessionDataVo = (SessionDataVo) session.getAttribute("sessionData");
        ProfileDetailsVo profileDetailsVos = profileDetailsService.getProfileDetails(userEntity.getId());
        String collegeDetails = " ";
        if (profileDetailsVos == null) {
            profileDetailsVos = new ProfileDetailsVo(); // Avoid null object issues
        }
        if (userEntity.getRoleId() == 2) {
            List<UserData> userDataList = appointmentService.getAppointmentDetailsByUserIdAndExamSeriesId(Math.toIntExact(userEntity.getId()), sessionDataVo.getExamSeriesId());
            CollegeEntity collegeEntity = collegeService.getCollegeById(userDataList.get(0).getCollege_id());
            collegeDetails = collegeEntity.getCollegeCode() + "-" + collegeEntity.getCollege_name();

        }
        model.addAttribute("collegeDetails", collegeDetails);
        model.addAttribute("profileDetails", profileDetailsVos);
        model.addAttribute("page", "profileDetails");
        return "main";
    }

    @PostMapping("/saveProfile")
    public String saveProfile(Model model, ProfileDetailsVo profileDetailsVo, RedirectAttributes redirectAttributes) {
        UserDetails user = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User userEntity = userRepository.findByUserName(user.getUsername());
        ProfileDetailsEntity profileDetailsEntity = profileDetailsRepository.findByUserId(userEntity.getId());
        profileDetailsEntity.setOther_designation(profileDetailsVo.getOther_designation());
        profileDetailsEntity.setStaff_code(profileDetailsVo.getStaff_code());
        profileDetailsEntity.setAcc_type(profileDetailsVo.getAcc_type());
        profileDetailsEntity.setMiddle_name(profileDetailsVo.getMiddle_name());
        profileDetailsEntity.setInstitute_type(profileDetailsVo.getInstitute_type());
        profileDetailsEntity.setFaculty_type(profileDetailsVo.getFaculty_type());
        profileDetailsEntity.setResidential_address(profileDetailsVo.getResidential_address());
        profileDetailsEntity.setBranch_details(profileDetailsVo.getBranch_details());
        profileDetailsEntity.setBranch_address(profileDetailsVo.getBranch_address());
        profileDetailsEntity.setEmail(profileDetailsVo.getEmail());
        profileDetailsEntity.setContact(profileDetailsVo.getMobile_no());
        profileDetailsEntity.setTeaching_experience(profileDetailsVo.getTeaching_experience());
        profileDetailsEntity.setDesignation(profileDetailsVo.getDesignation());
        profileDetailsEntity.setIndustry_experience(profileDetailsVo.getIndustry_experience());
        profileDetailsEntity.setIfsc_code(profileDetailsVo.getIfsc_code());
        profileDetailsEntity.setLastName(profileDetailsVo.getLastName());
        profileDetailsEntity.setAccount_no(profileDetailsVo.getAccount_no());
        profileDetailsEntity.setBank_name(profileDetailsVo.getBank_name());


        profileDetailsRepository.save(profileDetailsEntity);
        redirectAttributes.addFlashAttribute("msg", "profile details saved successfully");
        return "redirect:/roles";
    }

    @GetMapping("/api/bank/getDetails/{ifsc}")
    @ResponseBody
    public String getBankDetails(@PathVariable("ifsc") String ifscCode) {
        String url = "https://ifsc.razorpay.com/" + ifscCode; // Example: https://ifsc.razorpay.com/SBIN0005943
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        } catch (Exception e) {
            return "Invalid IFSC Code or API issue";
        }
    }


    @PostMapping("/updateProfileForm")
    @Transactional
    public ResponseEntity<String> updateProfile(@ModelAttribute UserProfileForm form) {
        User user = userRepository.findByUserName(form.getUser_name());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        String[] names = form.getStaff_name().split(" ", 2);
        user.setFirstName(names[0]);
        user.setLastName(names.length > 1 ? names[1] : "");
        user.setMobileNo(form.getMobile_no());
        user.setEmail(form.getEmail_id());
        userRepository.save(user);

        ProfileDetailsEntity profileDetails = profileDetailsService.getProfileDetailsByUserId(user.getId());
        profileDetails.setContact(form.getMobile_no());
        profileDetails.setEmail(form.getEmail_id());
        profileDetails.setCollege_id(String.valueOf(form.getCollege_id()));
        profileDetailsService.save(profileDetails);

        List<UserData> appointments = appointmentService.getAppointmentDetailsByUserId(user.getId(), 1);
        appointments.forEach(a -> a.setCollege_id(String.valueOf(form.getCollege_id())));
        appointmentService.saveBulkAppointment(appointments);

        return ResponseEntity.ok("Profile updated successfully");
    }

}