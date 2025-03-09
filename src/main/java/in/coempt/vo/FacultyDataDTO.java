package in.coempt.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FacultyDataDTO {

    private String firstName;
    private String lastName;
    private String subjectCode;
    private String mobileNo;
    private String email;
    private String orderDate;
    private String lastDateToSubmit;
    private Integer noOfSets;
    private String collegeCode;
    private String roleId;
}
