package in.coempt.repository;

import in.coempt.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BulkAppointmentRepository extends JpaRepository<UserData,Long>
{

    UserData findByUserIdAndSubjectId(int userId, int subjectId);
    List<UserData> findByUserIdInAndSubjectId(List<Integer> userIds, int subjectId);
}
