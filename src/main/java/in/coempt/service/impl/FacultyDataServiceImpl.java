package in.coempt.service.impl;


import in.coempt.entity.FacultyData;
import in.coempt.repository.FacultyDataRepository;
import in.coempt.service.FacultyDataService;
import in.coempt.util.CSVHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FacultyDataServiceImpl implements FacultyDataService {

    @Autowired
    private FacultyDataRepository facultyDataRepository;

    public List<FacultyData> getAllFaculties() {
        return facultyDataRepository.findAll();
    }

    public Optional<FacultyData> getFacultyById(Integer id) {
        return facultyDataRepository.findById(id);
    }

    public Optional<FacultyData> getFacultyByMobileNumber(String mobileNumber) {
        return facultyDataRepository.findByMobileNumber(mobileNumber);
    }

    public FacultyData saveFaculty(FacultyData facultyData) {
        return facultyDataRepository.save(facultyData);
    }

    public void deleteFaculty(Integer id) {
        facultyDataRepository.deleteById(id);
    }
    public ArrayList<Object> saveCSV(MultipartFile file) {


        List<FacultyData> successList = new ArrayList<>();
        List<FacultyData> failureList = new ArrayList<>();

        try {
            List<FacultyData> facultyList = CSVHelper.csvToFacultyData(file.getInputStream());

            for (FacultyData faculty : facultyList) {
                // Check if a record exists by mobile number
                Optional<FacultyData> existingFacultyOpt = facultyDataRepository.findByMobileNumber(faculty.getMobileNumber());

                if (existingFacultyOpt.isPresent()) {
                    failureList.add(existingFacultyOpt.get());
                } else {
                    successList.add(faculty);
                    facultyDataRepository.save(faculty);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to store CSV data: " + e.getMessage());
        }
        ArrayList<Object> arrayList=new ArrayList<>();
        arrayList.add(successList);
        arrayList.add(failureList);
        return arrayList;
    }
}

