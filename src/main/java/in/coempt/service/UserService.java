package in.coempt.service;

import in.coempt.entity.User;
import in.coempt.entity.UserData;

import java.util.List;

public interface UserService {
    public User saveUser(User user);

    User getUserByUserName(String orderId);
    public boolean changePassword(String username, String oldPassword, String newPassword);

    public boolean resetPassword(String token, String newPassword);
    public String sendPasswordResetLink(String email,String userName);

    User getUserByMobileNo(String mobileNo);

    User getUserById(String userId);

    User getUserByMobileNoAndEmailAndRoleId(String mobileNumber, String email, int roleId);

    List<User> getUserByMobileNoAndEmail(String mobileNumber, String email);

    User getUserByMobileNoAndRoleId(String mobileNo, int roleId);

    User getUserByEmailAndRoleId(String email, int i);

    void deleteUserDetails(Long id);
}
