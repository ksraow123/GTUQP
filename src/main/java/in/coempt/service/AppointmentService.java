package in.coempt.service;

import in.coempt.entity.User;
import in.coempt.entity.UserData;
import in.coempt.vo.AppointmentVo;
import in.coempt.vo.IndividualAppointmentVo;

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

}
