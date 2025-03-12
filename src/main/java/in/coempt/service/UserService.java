package in.coempt.service;

import in.coempt.entity.User;

import java.util.List;

public interface UserService {
    public User saveUser(User user);

    User getUserByUserName(String orderId);
    public boolean changePassword(String username, String oldPassword, String newPassword);

    public boolean resetPassword(String token, String newPassword);
    public User sendPasswordResetLink(String email,String userName);

    User getUserByMobileNo(String mobileNo);

    User getUserById(String userId);

    User getUserByMobileNoAndEmailAndRoleId(String mobileNumber, String email, int roleId);

    List<User> getUserByMobileNoAndEmail(String mobileNumber, String email);
}
