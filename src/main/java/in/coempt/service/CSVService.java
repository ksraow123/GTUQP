package in.coempt.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

public interface CSVService {
    public ArrayList<Object> saveCSV(MultipartFile file);

}
