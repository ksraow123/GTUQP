package in.coempt.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
		name = "users",
		uniqueConstraints = @UniqueConstraint(columnNames = {"role_id", "email", "mobile_no"})
)
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_name", nullable = false, unique = true, length = 80)
	private String userName;

	@Column(name = "email", nullable = false, length = 80)
	private String email;

	@Column(nullable = false, length = 64)
	private String password;

	@Column(name = "first_name", nullable = false, length = 80)
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "role_id", nullable = false, length = 20)
	private int roleId;

	@Column(name = "is_active", nullable = false, length = 20)
	private int isActive;

	private String resetToken;

	@Column(name = "mobile_no", nullable = false, length = 20)
	private String mobileNo;

	private String alternate_mobile;
	private String alternate_email;

	private LocalDateTime tokenExpiry;
}
