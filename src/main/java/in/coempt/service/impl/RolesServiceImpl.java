package in.coempt.service.impl;

import in.coempt.entity.Roles;
import in.coempt.entity.User;
import in.coempt.repository.RolesRepository;
import in.coempt.service.RolesService;
import in.coempt.util.SendMailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RolesServiceImpl implements RolesService {
    @Autowired
    public RolesRepository repository;
    @Autowired
    private  SendMailUtil sendMail;
    private final Map<String, String> otpStorage = new HashMap<>();


    @Override
    public List<Roles> getAllRoles() {
        return repository.findAll();
    }
    public String generateOtp(User user) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit OTP
        otpStorage.put(user.getEmail(), otp);
        String emailBody="Dear User,\n" +
                "\n" +
                otp+" is the login OTP for GTU SOQPRS.\n" +
                "\n" +
                "\n" +
                "Thanks,\n" +
                "Admin\n" +
                "SOQPRS - GTU.";
        sendMail.sendMail(user.getEmail(), "OTP for login in GTU QP Application",emailBody);
       if(user.getAlternate_email()!=null) {
           sendMail.sendBulkMail(Arrays.asList(user.getAlternate_email().split(",")), "OTP for login in GTU QP Application", emailBody);
       }
        return otp;
    }
    @Override
    public Roles getRoleDetails(int roleId) {
        return repository.findById(Long.valueOf(roleId)).get();
    }

    public boolean validateOtp(String email, String otp) {
        return otp.equals(otpStorage.get(email));
    }
}
