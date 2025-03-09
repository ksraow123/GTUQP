package in.coempt.controller;

import in.coempt.entity.Appointment;
import in.coempt.entity.ProfileDetailsEntity;
import in.coempt.entity.User;
import in.coempt.repository.ProfileDetailsRepository;
import in.coempt.repository.RolesRepository;
import in.coempt.repository.UserRepository;
import in.coempt.service.ProfileDetailsService;
import in.coempt.util.SecurityUtil;
import in.coempt.vo.ProfileDetailsVo;
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

import java.util.List;

@Controller
public class ProfileDetailsController {
@Autowired
private ProfileDetailsService profileDetailsService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileDetailsRepository profileDetailsRepository;


    @GetMapping("/viewProfile")
    public String getProfileDetails(Model model) {
        UserDetails user = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User userEntity = userRepository.findByUserName(user.getUsername());

        ProfileDetailsVo profileDetailsVos = profileDetailsService.getProfileDetails(userEntity.getId());

        if (profileDetailsVos == null) {
            profileDetailsVos = new ProfileDetailsVo(); // Avoid null object issues
        }

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