package in.coempt.controller;

import in.coempt.dao.UserDao;
import in.coempt.entity.User;
import in.coempt.repository.UserRepository;
import in.coempt.service.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@CrossOrigin(origins = "*")
public class LoginController {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserDao userDao;
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
    public String showLoginPage() {

        return "login";  // Returns login.html
    }
    @GetMapping("/session-expired")
    public String sessionExpired() {

        return "session-expired";
    }
}
