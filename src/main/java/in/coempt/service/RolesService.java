package in.coempt.service;

import in.coempt.entity.Roles;

import java.util.List;

public interface RolesService {

    public List<Roles> getAllRoles();
    public String generateOtp(String email);

    Roles getRoleDetails(int roleId);
    public boolean validateOtp(String email, String otp);

}
