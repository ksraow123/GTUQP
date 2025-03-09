package in.coempt.controller;

import in.coempt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping("/password")
public class PasswordController {
    @Autowired
    private UserService userService;

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
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot")
    public String processForgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
        userService.sendPasswordResetLink(email);
        redirectAttributes.addFlashAttribute("success", "Password reset link sent to your email.");
        return "redirect:/password/forgot";
    }

    @GetMapping("/resend/{email}")
    public String processResend(@PathVariable("email") String email, RedirectAttributes redirectAttributes) {
        userService.sendPasswordResetLink(email);
        redirectAttributes.addFlashAttribute("success", "Password reset link sent to your email.");
        return "redirect:/upload";
    }





    @GetMapping("/reset")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset")
    public String processResetPassword(@RequestParam String token,
                                       @RequestParam String newPassword,
                                       @RequestParam String confirmPassword,
                                       RedirectAttributes redirectAttributes) {
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
        return "redirect:/";
    }


}

