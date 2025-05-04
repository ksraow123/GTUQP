package in.coempt.service;

import in.coempt.entity.FacultyAppointment;
import in.coempt.entity.User;
import in.coempt.entity.UserData;
import in.coempt.vo.AppointmentVo;
import in.coempt.vo.IndividualAppointmentVo;
import in.coempt.vo.ProfileDetailsVo;
import in.coempt.vo.SessionDataVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AppointmentService {

    public void saveUserAppointment(UserData userData);


    void saveBulkAppointment(List<UserData> userDataList);

    List<UserData> getAllAppointmentDetails();

    UserData getAppointmentDetailsById(Long appointmentId);


    List<AppointmentVo> getAppointmentDetailsList(List<Long> selectedIds);

    List<AppointmentVo> getAppointmentDshBoard();

    UserData saveuserData(UserData userData);

    UserData getAppointmentDetailsByUserIdAndSubjectId(IndividualAppointmentVo appointmentVo, User user);

    List<UserData> checkSubjectAvailableByUserList(List<User> ulist, int subjectId);
    public List<AppointmentVo> getAppointmentDshBoard(String mobileNumber);

    public List<AppointmentVo> getAppointmentDshBoardBySection(String sectionId);


    List<UserData> getAppointmentDetailsByUserIdAndExamSeriesId(Integer userId ,Integer examSeriesId);

    public UserData getUserDataSubjectIdAndUserIdAndExamSeriesId(Integer subjectId, Integer userId,int examSeriesId);



    List<UserData> getAppointmentDetailsByUserId(Long userId,int examSeriesId);

    ProfileDetailsVo getMaxUserPlusOne(int roleId);

    public List<FacultyAppointment> getAppointsUploadFileStatus(Long userId);

    void deleteFacultyAppointmentDetails(Long userId);

    List<FacultyAppointment> saveBulkAppointments(MultipartFile file, SessionDataVo sessionDataVo);

    void deleteAppointmentDetails(Long id);

    void updateFacultyAppointmentDates(String courseId, String submissionDate);

    String getCourseNamesBySubjectCode(String subjectCode);
    String getSemestersBySubjectCode(String subjectCode);

    void updateSemesterWiseFacultyAppointmentDates(String courseId, String submissionDate, String semester);

    void updateSubjectWiseFacultyAppointmentDates(String courseId, String submissionDate, String subjectId);
}
