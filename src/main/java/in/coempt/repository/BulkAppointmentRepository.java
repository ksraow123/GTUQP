package in.coempt.repository;

import in.coempt.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BulkAppointmentRepository extends JpaRepository<UserData,Long>
{

    UserData findByUserIdAndSubjectId(int userId, int subjectId);
}
