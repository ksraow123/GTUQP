package in.coempt.service;

import in.coempt.entity.Roles;
import in.coempt.entity.User;

import java.util.List;

public interface RolesService {

    public List<Roles> getAllRoles();
    public String generateOtp(User user);

    Roles getRoleDetails(int roleId);
    public boolean validateOtp(String email, String otp);

}
