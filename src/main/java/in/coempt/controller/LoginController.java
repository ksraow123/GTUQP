package in.coempt.controller;

import in.coempt.dao.UserDao;
import in.coempt.entity.User;
import in.coempt.repository.UserRepository;
import in.coempt.service.RolesService;
import in.coempt.service.UserService;
import in.coempt.util.MaskingUtil;
import in.coempt.util.SMSUtil;
import in.coempt.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

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
    public String showLoginPage(Model model) {

        return "login";  // Returns login.html
    }
    @GetMapping("/session-expired")
    public String sessionExpired() {

        return "session-expired";
    }
        @GetMapping("/send-otp")
        public String sendOtp(HttpSession session,Model model) {
            UserDetails userData = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
            User user = userService.getUserByUserName(userData.getUsername());
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = user.getEmail(); // Assuming email is used for authentication
          String otp=  rolesService.generateOtp(email);
            session.setAttribute("email", email);
            model.addAttribute("otpRequired",true);
            model.addAttribute("otp",otp);
            String maskedEmail = MaskingUtil.maskEmail(user.getEmail());
            String maskedMobile = MaskingUtil.maskMobile(user.getMobileNo());

            String message = "OTP was sent to your registered email id: " + maskedEmail +
                    " and mobile number: " + maskedMobile;


            model.addAttribute("sentto",message);
            smsUtil.sendSms1(user.getMobileNo(),otp+" is the OTP for login in SOQPRS application All the best GTU EXAMOE","1507166265631183761");

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
    }

