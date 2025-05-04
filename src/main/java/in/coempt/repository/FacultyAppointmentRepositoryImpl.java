package in.coempt.repository;

import in.coempt.entity.FacultyAppointment;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.List;

@Repository
public class FacultyAppointmentRepositoryImpl implements FacultyAppointmentRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<FacultyAppointment> callUspBulkAppointmentFromExcel(Long sectionUserId, Integer noOfSets, Integer examSeriesId) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("usp_bullk_appointment_from_excel", FacultyAppointment.class);

        // Register IN parameters (assuming all are of type String)
        query.registerStoredProcedureParameter(1, Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, Integer.class, ParameterMode.IN);

        // Set parameter values
        query.setParameter(1, sectionUserId);
        query.setParameter(2, noOfSets);
        query.setParameter(3, examSeriesId);

        // Execute and return results
        return query.getResultList();
    }
}
