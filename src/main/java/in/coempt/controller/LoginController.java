package in.coempt.controller;

import in.coempt.dao.UserDao;
import in.coempt.entity.User;
import in.coempt.repository.UserRepository;
import in.coempt.service.RolesService;
import in.coempt.service.UserService;
import in.coempt.util.MaskingUtil;
import in.coempt.util.SMSUtil;
import in.coempt.util.SecurityUtil;
import in.coempt.vo.UserMacIdsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@CrossOrigin(origins = "*")
public class LoginController {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;
    @Autowired
    private SMSUtil smsUtil;
@Autowired
private RolesService rolesService;
    @GetMapping("")
    public String viewHomePage() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", rolesService.getAllRoles());
        return "signup_form";
    }

    @PostMapping("/process_register")
    public String processRegister(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setIsActive(1);
        userRepo.save(user);

        return "register_success";
    }

    @GetMapping("/")
    public String showLoginPage(@RequestParam(value = "macid",required = false) String macid , Model model, HttpSession httpSession) {

        httpSession.setAttribute("macid",macid);
        List<UserMacIdsVo> userMacIdsVos=userDao.getMacIds(macid);
        if(userMacIdsVos.size()>0){
            model.addAttribute("userName",userMacIdsVos.get(0).getCenter_code());
        }
        return "login";  // Returns login.html
    }
    @GetMapping("/session-expired")
    public String sessionExpired() {

        return "session-expired";
    }
    @GetMapping("/error")
    public String error() {

        return "error";
    }
        @GetMapping("/send-otp")
        public String sendOtp(HttpSession session,Model model) {

        String macId= (String) session.getAttribute("macid");
        UserDetails userData = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
            User user = userService.getUserByUserName(userData.getUsername());
            String[] alternateMobileNumbers=null;
            if(user.getAlternate_mobile()!=null){
                alternateMobileNumbers=user.getAlternate_mobile().split(",");
            }

            if(user.getRoleId()==1){
                if(macId==null||macId.equalsIgnoreCase("")){
                    return "redirect:/logoutApi";
                }
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = user.getEmail(); // Assuming email is used for authentication
          String otp=  rolesService.generateOtp(user);
            session.setAttribute("email", email);
            model.addAttribute("otpRequired",true);
            model.addAttribute("otp",otp);
            String maskedEmail = MaskingUtil.maskEmail(user.getEmail());
            String maskedMobile = MaskingUtil.maskMobile(user.getMobileNo());

            String message = "OTP was sent to your registered email id: " + maskedEmail +
                    " and mobile number: " + maskedMobile;


            model.addAttribute("sentto",message);
           smsUtil.sendSms1(user.getMobileNo(),otp+" is the OTP for login in SOQPRS application All the best GTU EXAMOE","1507166265631183761");
            if(alternateMobileNumbers!=null) {
             smsUtil.sendBulkSms(alternateMobileNumbers, otp + " is the OTP for login in SOQPRS application All the best GTU EXAMOE", "1507166265631183761");
            }
            return "login";
    }


        @PostMapping("/verify-otp")
        public String verifyOtp(@RequestParam String otp, HttpSession session, Model model) {
            String email = (String) session.getAttribute("email");
            if (email == null || !rolesService.validateOtp(email, otp)) {
                model.addAttribute("error", "Invalid OTP. Try again.");
                model.addAttribute("otpRequired",true);
                return "login";
            }

            session.setAttribute("authenticatedUser", email); // OTP verified, user is fully authenticated
            return "redirect:/roles";
        }

    @GetMapping("/logoutApi")
    public String logoutApi() {

        return "session-expired";
    }


}

