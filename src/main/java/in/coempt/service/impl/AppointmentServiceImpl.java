package in.coempt.service.impl;

import in.coempt.dao.AppointmentDao;
import in.coempt.entity.*;
import in.coempt.repository.BulkAppointmentRepository;
import in.coempt.repository.FacultyAppointmentRepository;
import in.coempt.repository.FacultyAppointmentRepositoryCustom;
import in.coempt.service.AppointmentService;
import in.coempt.util.SecurityUtil;
import in.coempt.vo.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private FacultyAppointmentRepositoryCustom customRepo;

    @Autowired
    private BulkAppointmentRepository bulkAppointmentRepository;

    @Autowired
    private FacultyAppointmentRepository facultyAppointmentRepository;
    @Autowired
    private AppointmentDao appointmentDao;


    @Override
    public void saveUserAppointment(UserData userData) {
        bulkAppointmentRepository.save(userData);
    }

    @Override
    public void saveBulkAppointment(List<UserData> userDataList) {
        bulkAppointmentRepository.saveAll(userDataList);
    }

    @Override
    public List<UserData> getAllAppointmentDetails() {
        return bulkAppointmentRepository.findAll();
    }

    @Override
    public UserData getAppointmentDetailsById(Long appointmentId) {
        return bulkAppointmentRepository.findById(appointmentId).get();
    }

    @Override
    public List<AppointmentVo> getAppointmentDetailsList(List<Long> selectedIds) {
        return appointmentDao.getAppointmentDetailsList(selectedIds);
    }

    @Override
    public List<AppointmentVo> getAppointmentDshBoard() {
        return appointmentDao.getAppointmentDetailsList();
    }

    @Override
    public List<AppointmentVo> getAppointmentDshBoard(String mobileNumber) {
        return appointmentDao.getAppointmentDetailsList(mobileNumber);
    }

    @Override
    public List<AppointmentVo> getAppointmentDshBoardBySection(String sectionId) {
        return appointmentDao.getAppointmentDshBoardBySection(sectionId);
    }

    @Override
    public List<UserData> getAppointmentDetailsByUserIdAndExamSeriesId(Integer userId, Integer examSeriesId) {
        return bulkAppointmentRepository.getAppointmentDetailsByUserIdAndExamSeriesId(userId, examSeriesId);
    }

    @Override
    public UserData saveuserData(UserData userData) {
        return bulkAppointmentRepository.save(userData);
    }

    @Override
    public UserData getAppointmentDetailsByUserIdAndSubjectId(IndividualAppointmentVo appointmentVo, User user) {
        return bulkAppointmentRepository.findByUserIdAndSubjectId(Math.toIntExact(user.getId()), appointmentVo.getSubject_id());
    }

    @Override
    public List<UserData> checkSubjectAvailableByUserList(List<User> ulist, int subjectId) {
        return bulkAppointmentRepository.findByUserIdInAndSubjectId(ulist.stream().map(u -> Math.toIntExact(u.getId())).collect(Collectors.toList()), subjectId);
    }

    public UserData getUserDataSubjectIdAndUserIdAndExamSeriesId(Integer subjectId, Integer userId, int examSeriesId) {
        return bulkAppointmentRepository.findByUserIdAndSubjectIdAndExamSeriesId(userId, subjectId, examSeriesId);
    }


    @Override
    public List<UserData> getAppointmentDetailsByUserId(Long userId, int examSeriesId) {
        return bulkAppointmentRepository.findByUserIdAndExamSeriesId(Math.toIntExact(userId), 1);
    }

    @Override
    public ProfileDetailsVo getMaxUserPlusOne(int roleId) {
        return appointmentDao.getMaxUserPlusOne(roleId);
    }

    @Override
    public List<FacultyAppointment> getAppointsUploadFileStatus(Long userId) {
        return facultyAppointmentRepository.findBySectionUserId(userId);
    }

    @Override
    public void deleteFacultyAppointmentDetails(Long userId) {
        appointmentDao.deleteBySectionUserId(userId);
    }

    @Override
    public List<FacultyAppointment> saveBulkAppointments(MultipartFile file, SessionDataVo sessionDataVo) {
        List<FacultyAppointment> appointmentList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            for (CSVRecord record : csvParser) {
                FacultyAppointment facultyAppointment = new FacultyAppointment();
                facultyAppointment.setCollegecode(record.get("College Code").trim());
                facultyAppointment.setSubjectcode(record.get("subject code").trim());
                facultyAppointment.setStaffname(record.get("Staff Name").trim());
                facultyAppointment.setContact(record.get("Contact").trim());
                facultyAppointment.setLastdatetosubmit(record.get("last date to submit").trim());
                facultyAppointment.setEmail(record.get("Email").trim());
                facultyAppointment.setCourse(record.get("Course").trim());
                facultyAppointment.setInstituteaddress(record.get("Institute address").trim());
                facultyAppointment.setCoursename(record.get("Course Name").trim());
                facultyAppointment.setDesignation(record.get("Designation").trim());
                facultyAppointment.setDepartment(record.get("Department").trim());
                facultyAppointment.setTotalexperience(record.get("Total Experience").trim());
                facultyAppointment.setSectionUserId(sessionDataVo.getUserId());

                appointmentList.add(facultyAppointment);
            }
            facultyAppointmentRepository.saveAll(appointmentList);

            //call procedure
            return customRepo.callUspBulkAppointmentFromExcel(sessionDataVo.getUserId(), 1, sessionDataVo.getExamSeriesId());
      // return facultyAppointmentRepository.findBySectionUserId(sessionDataVo.getUserId());
        } catch (IOException e) {
            // Log the error and optionally rethrow as a runtime exception
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
        }

    }

    @Override
    public void deleteAppointmentDetails(Long id) {
        bulkAppointmentRepository.deleteById(id);
    }

    @Override
    public void updateFacultyAppointmentDates(String courseId, String submissionDate) {

            appointmentDao.updateFacultyAppointmentDates(courseId,submissionDate);

    }

    @Override
    public String getCourseNamesBySubjectCode(String subjectCode) {
        return appointmentDao.getCourseNamesBySubjectCode(subjectCode);
    } @Override
    public String getSemestersBySubjectCode(String subjectCode) {
        return appointmentDao.getSemestersBySubjectCode(subjectCode);
    }

    @Override
    public void updateSemesterWiseFacultyAppointmentDates(String courseId, String submissionDate, String semester) {
        appointmentDao.updateSemesterWiseFacultyAppointmentDates(courseId,submissionDate,semester);
    }

    @Override
    public void updateSubjectWiseFacultyAppointmentDates(String courseId, String submissionDate, String subjectId) {
        appointmentDao.updateSubjectWiseFacultyAppointmentDates(courseId,submissionDate,subjectId);
    }
}