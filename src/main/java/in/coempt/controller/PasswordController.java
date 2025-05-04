package in.coempt.controller;

import in.coempt.dao.UserDao;
import in.coempt.entity.User;
import in.coempt.service.UserService;
import in.coempt.vo.UserMacIdsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping("/password")
public class PasswordController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;

    @GetMapping("/change")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("page","changepwd");
        return "main";
    }

    @PostMapping("/change")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match.");
            return "redirect:/password/change";
        }

        boolean success = userService.changePassword(principal.getName(), oldPassword, newPassword);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Old password is incorrect.");
            return "redirect:/password/change";
        }

        redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
        return "redirect:/password/change";
    }

    @GetMapping("/forgot")
    public String showForgotPasswordForm(HttpSession session,Model model) {
        String macId= (String) session.getAttribute("macid");
        model.addAttribute("macid",macId);
        List<UserMacIdsVo> userMacIdsVos=userDao.getMacIds(macId);
        if(userMacIdsVos.size()>0){
            model.addAttribute("userName",userMacIdsVos.get(0).getCenter_code());
        }
        return "forgot-password";
    }

    @PostMapping("/forgot")
    public String processForgotPassword(@RequestParam String userName,@RequestParam String email, RedirectAttributes redirectAttributes,HttpSession session,Model model) {
        String resetLink = userService.sendPasswordResetLink(email, userName);
        String macId= (String) session.getAttribute("macid");
        model.addAttribute("macid",macId);
        List<UserMacIdsVo> userMacIdsVos=userDao.getMacIds(macId);
        if(userMacIdsVos.size()>0){
            model.addAttribute("userName",userMacIdsVos.get(0).getCenter_code());
        }
        if (resetLink != null){
            redirectAttributes.addFlashAttribute("success", "Set the New Password");
            return "redirect:/"+resetLink;
    }
       else {
            redirectAttributes.addFlashAttribute("success", "User name/email not found");
            return "redirect:/password/forgot";
        }
    }

    @GetMapping("/resend/{email}/{userName}")
    public String processResend(@PathVariable("email") String email,@PathVariable("userName") String userName, RedirectAttributes redirectAttributes) {
      // String user= userService.sendPasswordResetLink(email,userName);
        String resetLink = userService.sendPasswordResetLink(email, userName);
        if (resetLink != null){
            redirectAttributes.addFlashAttribute("success", "Set the New Password");
            return resetLink;

        }
        else{
            redirectAttributes.addFlashAttribute("success", "User name not found");
        }
        return "redirect:/upload";
    }
    @GetMapping("/reset")
    public String showResetPasswordForm(@RequestParam String token, Model model,HttpSession session) {
        model.addAttribute("token", token);
        String macId= (String) session.getAttribute("macid");
        model.addAttribute("macid",macId);
        List<UserMacIdsVo> userMacIdsVos=userDao.getMacIds(macId);
        if(userMacIdsVos.size()>0){
            model.addAttribute("userName",userMacIdsVos.get(0).getCenter_code());
        }
        return "reset-password";
    }

    @PostMapping("/reset")
    public String processResetPassword(@RequestParam String token,
                                       @RequestParam String newPassword,
                                       @RequestParam String confirmPassword,
                                       RedirectAttributes redirectAttributes,HttpSession session) {
        String macId= (String) session.getAttribute("macid");
        redirectAttributes.addFlashAttribute("macid",macId);
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/password/reset?token=" + token;
        }

        boolean success = userService.resetPassword(token, newPassword);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired token.");
            return "redirect:/password/reset?token=" + token;
        }

        redirectAttributes.addFlashAttribute("success", "Password reset successfully!");
        return "redirect:/?macid="+macId;
    }


}

