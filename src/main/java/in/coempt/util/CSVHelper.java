package in.coempt.util;

import in.coempt.entity.FacultyData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CSVHelper {

    public static String TYPE = "text/csv";

    public static boolean hasCSVFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static List<FacultyData> csvToFacultyData(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim())) {

            List<FacultyData> facultyList = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                FacultyData faculty = new FacultyData();

                faculty.setFirstName(csvRecord.get("firstname"));
                faculty.setLastName(csvRecord.get("lastname"));
                faculty.setMobileNumber(csvRecord.get("mobile no"));
                faculty.setEmail(csvRecord.get("email"));
                faculty.setCollegeCode(csvRecord.get("college code"));
                faculty.setTeachingExp(csvRecord.get("teaching experience"));
                faculty.setIndustryExp(csvRecord.get("industry experience"));
                faculty.setDesignation(csvRecord.get("designation"));

                facultyList.add(faculty);
            }

            return facultyList;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }
    }
}

