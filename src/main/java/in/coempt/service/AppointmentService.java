package in.coempt.service;

import in.coempt.entity.Appointment;
import in.coempt.entity.User;
import in.coempt.entity.UserData;
import in.coempt.vo.AppointmentVo;
import in.coempt.vo.IndividualAppointmentVo;

import java.util.List;

public interface AppointmentService {
    public void saveAppointment(Appointment appointment);
    public void saveUserAppointment(UserData userData);
    List<Appointment> getAllAppointments();

    Appointment getAppointmentById(Long id);

    public void cancelAppointmentById(Long id);

    Appointment getAppointmentDetails(String orderId);

    void saveBulkAppointment(List<UserData> userDataList);

    List<UserData> getAllAppointmentDetails();

    UserData getAppointmentDetailsById(Long appointmentId);


    List<AppointmentVo> getAppointmentDetailsList(List<Long> selectedIds);

    List<AppointmentVo> getAppointmentDshBoard();

    UserData saveuserData(UserData userData);

    UserData getAppointmentDetailsByUserIdAndSubjectId(IndividualAppointmentVo appointmentVo, User user);
}
