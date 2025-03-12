package in.coempt.service.impl;

import in.coempt.dao.AppointmentDao;
import in.coempt.entity.Appointment;
import in.coempt.entity.User;
import in.coempt.entity.UserData;
import in.coempt.repository.AppointmentRepository;
import in.coempt.repository.BulkAppointmentRepository;
import in.coempt.service.AppointmentService;
import in.coempt.vo.AppointmentUpdateRequest;
import in.coempt.vo.AppointmentVo;
import in.coempt.vo.IndividualAppointmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private BulkAppointmentRepository bulkAppointmentRepository;

    @Autowired
    private AppointmentDao appointmentDao;
    @Override
    @Transactional
    public void saveAppointment(Appointment appointment) {
        appointmentRepository.save(appointment);
    }

    @Override
    public void saveUserAppointment(UserData userData) {
        bulkAppointmentRepository.save(userData);
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentDao.getAllAppointments();
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).get();
    }

    @Override
    public void cancelAppointmentById(Long id) {
         appointmentRepository.deleteById(id);
    }

    @Override
    public Appointment getAppointmentDetails(String orderId) {
       return appointmentRepository.findByOrderNo(orderId);
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
    public UserData saveuserData(UserData userData) {
      return   bulkAppointmentRepository.save(userData);
    }

    @Override
    public UserData getAppointmentDetailsByUserIdAndSubjectId(IndividualAppointmentVo appointmentVo, User user) {
        return   bulkAppointmentRepository.findByUserIdAndSubjectId(Math.toIntExact(user.getId()),appointmentVo.getSubject_id());
    }

    @Override
    public List<UserData> checkSubjectAvailableByUserList(List<User> ulist, int subjectId) {
       return bulkAppointmentRepository.findByUserIdInAndSubjectId(ulist.stream().map(u->Math.toIntExact(u.getId())).collect(Collectors.toList()),subjectId);
    }


}
