package in.coempt.service;

import in.coempt.entity.FacultyAppointment;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public interface CSVService {
    public ArrayList<Object> saveCSV(MultipartFile file);
    }
