package in.coempt.service.impl;

import in.coempt.entity.User;
import in.coempt.entity.UserData;
import in.coempt.repository.UserRepository;
import in.coempt.service.UserService;
import in.coempt.util.SendMailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository repository;
    @Autowired
    private SendMailUtil sendMail;
    @Value("${app.url}")
    private String appUrl;
    @Override
    public User saveUser(User user) {
        return repository.save(user);
    }

    @Override
    public User getUserByUserName(String orderId) {
        return repository.findByUserName(orderId);
    }


    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = repository.findByUserName(username);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false; // Old password is incorrect
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);
        return true;
    }

    public String sendPasswordResetLink(String email,String userName) {
        String resetLink=null;
        Optional<User> user = repository.findByEmailAndUserName(email,userName);
if(user.isPresent()) {
    String token = UUID.randomUUID().toString();
    user.get().setResetToken(token);
    user.get().setTokenExpiry(LocalDateTime.now().plusMinutes(30)); // Token valid for 30 mins
    repository.save(user.get());
     resetLink = "password/reset?token=" + token;
}
        //sendMail.sendMail(user.getEmail(), "Password Reset Request",
             //   "Click the link to reset your password: " + resetLink);
        return resetLink;
    }

    @Override
    public User getUserByMobileNo(String mobileNo) {
       return  repository.findByMobileNo(mobileNo);
    }

    @Override
    public User getUserById(String userId) {
        return repository.findById(Long.parseLong(userId)).get();
    }

    @Override
    public User getUserByMobileNoAndEmailAndRoleId(String mobileNumber, String email, int roleId) {
        return repository.findByMobileNoAndEmailAndRoleId(mobileNumber,email,roleId);
    }

    @Override
    public List<User> getUserByMobileNoAndEmail(String mobileNumber, String email) {
        return  repository.findByMobileNoAndEmail(mobileNumber,email);
    }

    @Override
    public User getUserByMobileNoAndRoleId(String mobileNo, int roleId) {
        return  repository.findByMobileNoAndRoleId(mobileNo,roleId);
    }

    @Override
    public User getUserByEmailAndRoleId(String email, int roleId) {
        return  repository.findByEmailAndRoleId(email,roleId);
    }

    @Override
    public void deleteUserDetails(Long id) {
        repository.deleteById(id);
    }


    public boolean resetPassword(String token, String newPassword) {
        User user = repository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            return false; // Token expired
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); // Clear token
        user.setTokenExpiry(null);
        repository.save(user);
        return true;
    }
}
