package in.coempt.repository;

import in.coempt.entity.FacultyAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacultyAppointmentRepository extends JpaRepository<FacultyAppointment,Long> {


    List<FacultyAppointment> findBySectionUserId(long userId);

    void deleteBySectionUserId(long userId);
}
