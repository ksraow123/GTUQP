package in.coempt.service.impl;

import in.coempt.entity.*;
import in.coempt.repository.SetterModeratorRepository;
import in.coempt.repository.UserDataRepository;
import in.coempt.repository.UserRepository;
import in.coempt.service.*;
import in.coempt.util.SecurityUtil;
import in.coempt.vo.FacultyDataDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CSVServiceImpl implements CSVService {

    private final SubjectsService subjectsService;
    private final UserDataRepository userDataRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final CollegeService collegeService;
    private final UserService userService;
    private final AppointmentService appointmentService;
    private final SetterModeratorRepository moderatorRepository;
    private final UserRepository userRepository;
    private final SectionUserMappingService sectionUserMappingService;
    private final FacultyDataService facultyDataService;
    private final ProfileDetailsService profileDetailsService;
    @Transactional
    public ArrayList<Object> saveCSV(MultipartFile file) {
        List<FacultyDataDTO> successList = new ArrayList<>();
        List<FacultyDataDTO> failureList = new ArrayList<>();
        List<UserData> userList = new ArrayList<>();

        UserDetails userDetails = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User userEntity = userRepository.findByUserName(userDetails.getUsername());
        int sectionId = 0;
        if (userEntity.getRoleId() == 1) {
            SectionUserMappingEntity mappingEntity = sectionUserMappingService.getMappingDetailsByUserid(Math.toIntExact(userEntity.getId()));
            sectionId = mappingEntity.getSectionId();

        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            for (CSVRecord record : csvParser) {
                User udata = userService.getUserByMobileNoAndRoleId(record.get("Contact").trim(), 2);
                if (udata == null) {
                    String customPassword = RandomUtils.nextLong(10000, 99999) + "";
                    Subjects subjects = null;
                    if (userEntity.getRoleId() == 1) {
                        subjects = subjectsService.getSubjectCodeAndSectionId(record.get("subject code").trim(), sectionId);
                    }
                    if (userEntity.getRoleId() == 3) {

                        subjects = subjectsService.getSubject_code(record.get("subject code").trim());
                    }
                    UserData userData = new UserData();
                    User user = new User();
                    user.setFirstName(record.get("Staff Name").trim());
                    //  user.setLastName(data.getLastName());
                    user.setIsActive(1);
                    user.setMobileNo(record.get("Contact").trim());
                    user.setEmail(record.get("Email").trim());
                    user.setUserName(generateUserName(2));
                    user.setRoleId(2);
                    user.setPassword(passwordEncoder.encode(customPassword));
                    User usr=userService.saveUser(user);

                    ProfileDetailsEntity profileDetails=new ProfileDetailsEntity();
                    profileDetails.setUserId(user.getId());
                    profileDetails.setContact(record.get("Contact").trim());
                    profileDetails.setInstitute_address(record.get("Institute address").trim());
                    profileDetails.setCourse(record.get("Course").trim());
                    profileDetails.setCourse_name(record.get("Course Name").trim());
                    profileDetails.setDesignation(record.get("Designation").trim());
                    profileDetails.setDepartment(record.get("Department").trim());
                    profileDetails.setTotal_exp(record.get("Total Experience").trim());
                    profileDetailsService.save(profileDetails);
                    userData.setNo_of_sets(1);
                    userData.setUserId(Math.toIntExact(user.getId()));
                    CollegeEntity collegeEntity = collegeService.getCollegeByCode(record.get("College Code").trim());
                    userData.setCollege_id(String.valueOf(collegeEntity.getId()));
                    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                    LocalDate date = LocalDate.parse(record.get("last date to submit").trim(), inputFormatter);
                    String formattedDate = date.format(outputFormatter);

                    userData.setLast_date_to_submit(formattedDate);
                    LocalDateTime now = LocalDateTime.now();
                    // Define the formatter
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    // Format the date-time
                    String officeorderDate = now.format(formatter);
                    userData.setOffice_order_date(officeorderDate);
                    userData.setCurrent_status("Appointment Sent");
                    userData.setStatus_date(officeorderDate);
                    userData.setAppointment_sent_date(officeorderDate);
                    userData.setNo_of_sets(1);
                    userData.setSubjectId(Math.toIntExact(subjects.getId()));
                    userData.setExamSeriesId(1);
                    userData.setRole_id(2);
                    UserData u1 =appointmentService.getUserDataSubjectIdAndUserIdAndExamSeriesId(Math.toIntExact(subjects.getId()), Math.toIntExact(usr.getId()),1);
                    if(u1==null) {
                        userList.add(userData);
                        FacultyDataDTO facultyDataDTO = new FacultyDataDTO();
                        facultyDataDTO.setFirstName(record.get("Staff Name").trim());
                        facultyDataDTO.setCollegeCode(record.get("College Code").trim());
                        facultyDataDTO.setSubjectCode(record.get("subject code").trim());
                        facultyDataDTO.setLastDateToSubmit(record.get("last date to submit").trim());
                        facultyDataDTO.setRemarks("success");
                        facultyDataDTO.setMobileNo(record.get("Contact").trim());
                        facultyDataDTO.setEmail(record.get("Email").trim());
                        successList.add(facultyDataDTO);
                    }
                    else{
                        FacultyDataDTO facultyDataDTO=new FacultyDataDTO();
                        facultyDataDTO.setFirstName(record.get("Staff Name").trim());
                        facultyDataDTO.setCollegeCode(record.get("College Code").trim());
                        facultyDataDTO.setSubjectCode(record.get("subject code").trim());
                        facultyDataDTO.setLastDateToSubmit(record.get("last date to submit").trim());
                        facultyDataDTO.setRemarks("Subject already assigned");
                        facultyDataDTO.setMobileNo(record.get("Contact").trim());
                        facultyDataDTO.setEmail(record.get("Email").trim());
                        failureList.add(facultyDataDTO);
                    }
                }

                if (udata != null) {

                    User udataMail = userService.getUserByEmailAndRoleId(record.get("Email").trim(), 2);

                    if (udataMail != null) {
                        if (udata.getId() == udataMail.getId()) {
                            Subjects subjects = null;
                            if (userEntity.getRoleId() == 1) {
                                subjects = subjectsService.getSubjectCodeAndSectionId(record.get("subject code").trim(), sectionId);
                            }
                            if (userEntity.getRoleId() == 3) {
                                subjects = subjectsService.getSubject_code(record.get("subject code").trim());
                            }
                            UserData userData = new UserData();
                            userData.setNo_of_sets(1);
                            userData.setUserId(Math.toIntExact(udata.getId()));
                            CollegeEntity collegeEntity = collegeService.getCollegeByCode(record.get("College Code").trim());
                            userData.setCollege_id(String.valueOf(collegeEntity.getId()));
                            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                            LocalDate date = LocalDate.parse(record.get("last date to submit").trim(), inputFormatter);
                            String formattedDate = date.format(outputFormatter);
                            userData.setLast_date_to_submit(formattedDate);
                            LocalDateTime now = LocalDateTime.now();
                            // Define the formatter
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                            // Format the date-time
                            String officeorderDate = now.format(formatter);
                            userData.setOffice_order_date(officeorderDate);
                            userData.setCurrent_status("Appointment Sent");
                            userData.setStatus_date(officeorderDate);
                            userData.setAppointment_sent_date(officeorderDate);
                            userData.setNo_of_sets(1);
                            userData.setSubjectId(Math.toIntExact(subjects.getId()));
                            userData.setExamSeriesId(1);
                            userData.setRole_id(2);
                            UserData u1 = appointmentService.getUserDataSubjectIdAndUserIdAndExamSeriesId(Math.toIntExact(subjects.getId()), Math.toIntExact(udata.getId()), 1);
                            if (u1 == null) {
                                userList.add(userData);
                                FacultyDataDTO facultyDataDTO = new FacultyDataDTO();
                                facultyDataDTO.setFirstName(record.get("Staff Name").trim());
                                facultyDataDTO.setCollegeCode(record.get("College Code").trim());
                                facultyDataDTO.setSubjectCode(record.get("subject code").trim());
                                facultyDataDTO.setLastDateToSubmit(record.get("last date to submit").trim());
                                facultyDataDTO.setRemarks("success");
                                facultyDataDTO.setMobileNo(record.get("Contact").trim());
                                facultyDataDTO.setEmail(record.get("Email").trim());
                                successList.add(facultyDataDTO);
                            } else {
                                FacultyDataDTO facultyDataDTO = new FacultyDataDTO();
                                facultyDataDTO.setFirstName(record.get("Staff Name").trim());
                                facultyDataDTO.setCollegeCode(record.get("College Code").trim());
                                facultyDataDTO.setSubjectCode(record.get("subject code").trim());
                                facultyDataDTO.setLastDateToSubmit(record.get("last date to submit").trim());
                                facultyDataDTO.setRemarks("Subject already assigned");
                                facultyDataDTO.setMobileNo(record.get("Contact").trim());
                                facultyDataDTO.setEmail(record.get("Email").trim());
                                failureList.add(facultyDataDTO);
                            }
                        }
                        else{
                            FacultyDataDTO facultyDataDTO = new FacultyDataDTO();
                            facultyDataDTO.setFirstName(record.get("Staff Name").trim());
                            facultyDataDTO.setCollegeCode(record.get("College Code").trim());
                            facultyDataDTO.setSubjectCode(record.get("subject code").trim());
                            facultyDataDTO.setLastDateToSubmit(record.get("last date to submit").trim());
                            facultyDataDTO.setRemarks("Email vs Mobile Mismatch for existing user");
                            facultyDataDTO.setMobileNo(record.get("Contact").trim());
                            facultyDataDTO.setEmail(record.get("Email").trim());
                            failureList.add(facultyDataDTO);
                        }

                    }
                    else{
                        FacultyDataDTO facultyDataDTO = new FacultyDataDTO();
                        facultyDataDTO.setFirstName(record.get("Staff Name").trim());
                        facultyDataDTO.setCollegeCode(record.get("College Code").trim());
                        facultyDataDTO.setSubjectCode(record.get("subject code").trim());
                        facultyDataDTO.setLastDateToSubmit(record.get("last date to submit").trim());
                        facultyDataDTO.setRemarks("Email vs Mobile Mismatch for existing user");
                        facultyDataDTO.setMobileNo(record.get("Contact").trim());
                        facultyDataDTO.setEmail(record.get("Email").trim());
                        failureList.add(facultyDataDTO);
                    }
                    userDataRepository.saveAll(userList);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  // ✅ FIX: Log exception for debugging
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(successList);
        arrayList.add(failureList);
        return arrayList;

    }

    private String generateUserName(int roleId) {
        return (roleId == 2 ? "S" : "M") + RandomUtils.nextLong(10000, 99999);
    }
}