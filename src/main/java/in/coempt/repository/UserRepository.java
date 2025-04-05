package in.coempt.repository;

import in.coempt.entity.User;
import in.coempt.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	//Query("SELECT u FROM User u WHERE u.userName = ?1 and u.isActive=1")
	public User findByUserNameAndIsActive(String userName,int isaActive);

	public User findByUserName(String orderId);
	Optional<User> findByEmail(String email);
	Optional<User> findByResetToken(String resetToken);

    User findByMobileNo(String mobileNo);

	User findByMobileNoAndEmailAndRoleId(String mobileNumber, String email, int roleId);

    List<User> findByMobileNoAndEmail(String mobileNumber, String email);

	Optional<User> findByEmailAndUserName(String email, String userName);

	User findByMobileNoAndRoleId(String mobileNo, int roleId);

	User findByEmailAndRoleId(String email, int roleId);
}
