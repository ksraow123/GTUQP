package in.coempt.service;

import in.coempt.entity.FacultyData;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface FacultyDataService {


    public List<FacultyData> getAllFaculties() ;

    public Optional<FacultyData> getFacultyById(Integer id) ;

    public Optional<FacultyData> getFacultyByMobileNumber(String mobileNumber) ;

    public FacultyData saveFaculty(FacultyData facultyData) ;

    public void deleteFaculty(Integer id);
    public ArrayList<Object> saveCSV(MultipartFile file);


}