package in.coempt.service.impl;

import in.coempt.dao.AppointmentDao;
import in.coempt.entity.User;
import in.coempt.entity.UserData;
import in.coempt.repository.BulkAppointmentRepository;
import in.coempt.service.AppointmentService;
import in.coempt.vo.AppointmentVo;
import in.coempt.vo.IndividualAppointmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {


    @Autowired
    private BulkAppointmentRepository bulkAppointmentRepository;

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
        return bulkAppointmentRepository.getAppointmentDetailsByUserIdAndExamSeriesId(userId,examSeriesId);
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
