package in.coempt.controller;

import in.coempt.entity.CollegeEntity;
import in.coempt.entity.ProfileDetailsEntity;
import in.coempt.entity.User;
import in.coempt.entity.UserData;
import in.coempt.repository.ProfileDetailsRepository;
import in.coempt.repository.UserRepository;
import in.coempt.service.AppointmentService;
import in.coempt.service.CollegeService;
import in.coempt.service.ProfileDetailsService;
import in.coempt.service.impl.AppointmentServiceImpl;
import in.coempt.util.SecurityUtil;
import in.coempt.vo.ProfileDetailsVo;
import in.coempt.vo.SessionDataVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

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
    private  AppointmentService appointmentService;

    @Autowired
    private ProfileDetailsRepository profileDetailsRepository;


    @GetMapping("/viewProfile")
    public String getProfileDetails(Model model, HttpSession session) {
        UserDetails user = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User userEntity = userRepository.findByUserName(user.getUsername());
        SessionDataVo sessionDataVo = (SessionDataVo) session.getAttribute("sessionData");
        ProfileDetailsVo profileDetailsVos = profileDetailsService.getProfileDetails(userEntity.getId());
String collegeDetails=" ";
        if (profileDetailsVos == null) {
            profileDetailsVos = new ProfileDetailsVo(); // Avoid null object issues
        }
        if(userEntity.getRoleId()==2){
            List<UserData> userDataList=appointmentService.getAppointmentDetailsByUserIdAndExamSeriesId(Math.toIntExact(userEntity.getId()),sessionDataVo.getExamSeriesId());
            CollegeEntity collegeEntity=collegeService.getCollegeById(userDataList.get(0).getCollege_id());
            collegeDetails=collegeEntity.getCollegeCode()+"-"+collegeEntity.getCollege_name();

        }
        model.addAttribute("collegeDetails", collegeDetails);
        model.addAttribute("profileDetails", profileDetailsVos);
model.addAttribute("page","profileDetails");
    return "main";
    }

    @PostMapping("/saveProfile")
    public String saveProfile(Model model,ProfileDetailsVo profileDetailsVo){
        UserDetails user = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User userEntity = userRepository.findByUserName(user.getUsername());

        // ProfileDetailsEntity profileDetailsEntity=profileDetailsRepository.findById(1L).get();
        ProfileDetailsEntity detailsEntity=new ProfileDetailsEntity();
        BeanUtils.copyProperties(profileDetailsVo, detailsEntity);
        detailsEntity.setUser_id(userEntity.getId());
        profileDetailsRepository.save(detailsEntity);
        return "redirect:/viewProfile";
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
    }