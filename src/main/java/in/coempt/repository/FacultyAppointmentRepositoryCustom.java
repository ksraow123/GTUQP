package in.coempt.repository;

import in.coempt.entity.FacultyAppointment;

import java.util.List;

public interface FacultyAppointmentRepositoryCustom {
    List<FacultyAppointment> callUspBulkAppointmentFromExcel(Long sectionUserId, Integer noOfSets, Integer examSeriesId);
}

