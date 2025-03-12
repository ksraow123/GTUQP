package in.coempt.service.impl;

import in.coempt.entity.*;
import in.coempt.repository.SetterModeratorRepository;
import in.coempt.repository.UserDataRepository;
import in.coempt.service.*;
import in.coempt.util.SecurityUtil;
import in.coempt.vo.FacultyDataDTO;
import in.coempt.vo.SetterModeratorMappingDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired

    private  FacultyDataService facultyDataService;
    @Transactional
    public ArrayList<Object> saveCSV(MultipartFile file) {
        List<FacultyDataDTO> successList = new ArrayList<>();
        List<FacultyDataDTO> failureList = new ArrayList<>();
        List<UserData> userList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {
                FacultyData facultyData =  facultyDataService.getFacultyByMobileNumber(record.get("mobile no")).get();
                if(facultyData==null){
                    facultyData.setEmail(record.get("email"));
                    facultyData.setMobileNumber(record.get("mobile no"));
                    facultyData.setFirstName(record.get("firstname"));
                    facultyData.setLastName(record.get("lastname"));
                    facultyData.setCollegeCode(record.get("college code"));
                    facultyDataService.saveFaculty(facultyData);
                }
                FacultyDataDTO faculty = new FacultyDataDTO();
                faculty.setFirstName(record.get("firstname"));
                faculty.setLastName(record.get("lastname"));
                faculty.setSubjectCode(record.get("subject code"));
                faculty.setMobileNo(record.get("mobile no"));
                faculty.setEmail(record.get("email"));
                faculty.setOrderDate(LocalDateTime.now()+"");
                faculty.setLastDateToSubmit(record.get("last date to submit"));
                faculty.setNoOfSets(Integer.parseInt(record.get("no of sets")));
                faculty.setCollegeCode(record.get("college code"));
                faculty.setRoleId(record.get("role id"));
                int roleId = faculty.getRoleId().equalsIgnoreCase("S") ? 2 : 3;
                User userExists = userService.getUserByMobileNoAndEmailAndRoleId(faculty.getMobileNo(), faculty.getEmail(), roleId);

                if (userExists != null) {
                    failureList.add(faculty);
                } else {
                    UserData user = getUserData(faculty);
                    userList.add(user);
                    successList.add(faculty);
                }
            }
            userDataRepository.saveAll(userList);
        } catch (IOException e) {
        }
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(successList);
        arrayList.add(failureList);

        return arrayList;
    }

    @Override
    public ArrayList<Object> saveMappingCSV(MultipartFile file) {
        List<SetterModeratorMappingDTO> successList = new ArrayList<>();
        List<SetterModeratorMappingDTO> failureList = new ArrayList<>();
        List<SetterModeratorMapping> userList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord record : csvParser) {
                SetterModeratorMappingDTO mappingDTO = new SetterModeratorMappingDTO();

                try {
                    mappingDTO.setSetterId(record.get("Setter Id"));
                    mappingDTO.setModeratorId(record.get("Moderator Id"));
                    mappingDTO.setSubjectCode(record.get("Subject Code"));

                    User user = userService.getUserByUserName(mappingDTO.getSetterId());
                    User user1 = userService.getUserByUserName(mappingDTO.getModeratorId());
                    Subjects subjects = subjectsService.getSubject_code(mappingDTO.getSubjectCode());
                    if(user!=null&&user1!=null&&subjects!=null) {
                        successList.add(mappingDTO);
                        SetterModeratorMapping mappingData = getSetterModeratorMapping(mappingDTO);
                        userList.add(mappingData);
                    }
                    else{
                        failureList.add(mappingDTO);
                    }
                } catch (Exception e) {
                    failureList.add(mappingDTO);
                }
            }

            moderatorRepository.saveAll(userList);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(successList);
        arrayList.add(failureList);
        return arrayList;
    }
    private SetterModeratorMapping getSetterModeratorMapping (SetterModeratorMappingDTO data){
        UserDetails userData = (UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User user = userService.getUserByUserName(data.getSetterId());
        User user1 = userService.getUserByUserName(data.getModeratorId());
        Subjects subjects = subjectsService.getSubject_code(data.getSubjectCode());
        SetterModeratorMapping setterModeratorMapping = new SetterModeratorMapping();

        setterModeratorMapping.setSetterId(user.getId());
        setterModeratorMapping.setModeratorId(user1.getId());
        setterModeratorMapping.setAssigned_date(LocalDate.now().toString());
        setterModeratorMapping.setAssigned_by(userData.getUsername());
        setterModeratorMapping.setSubjectId(subjects.getId());
        return setterModeratorMapping;

    }

    private UserData getUserData (FacultyDataDTO data){
        String customPassword = RandomUtils.nextLong(10000, 99999) + "";
        Subjects subjects = subjectsService.getSubject_code(data.getSubjectCode());
        int roleId = data.getRoleId().equalsIgnoreCase("S") ? 2 : 3;
        UserData userData = new UserData();
        User user = new User();
        user.setFirstName(data.getFirstName());
        user.setLastName(data.getLastName());
        user.setIsActive(0);
        user.setMobileNo(data.getMobileNo());
        user.setEmail(data.getEmail());

        user.setUserName(generateUserName(roleId));
        user.setRoleId(roleId);
        user.setPassword(passwordEncoder.encode(customPassword));
        userService.saveUser(user);

        userData.setNo_of_sets(data.getNoOfSets());
        userData.setUserId(Math.toIntExact(user.getId()));

        CollegeEntity collegeEntity = collegeService.getCollegeByCode(data.getCollegeCode());
        userData.setCollege_id(String.valueOf(collegeEntity.getId()));
        userData.setOffice_order_date(data.getOrderDate());
        userData.setLast_date_to_submit(data.getLastDateToSubmit());
        userData.setNo_of_sets(data.getNoOfSets());
        userData.setSubjectId(Math.toIntExact(subjects.getId()));

        userData.setRole_id(roleId);
        return userData;

    }

    private String generateUserName ( int roleId){
        return (roleId == 2 ? "S" : "M") + RandomUtils.nextLong(10000, 99999);
    }
}