package in.coempt.vo;

import lombok.Data;

import javax.persistence.*;

@Data
public class IndividualAppointmentVo {

    private int role_id;

    private String mobile_number;

    private String email;
    private String collegeCode,fname,lname,course_id;

    private int subject_id;
    private String order_date;
    private String submission_date;

    private String no_of_sets;


}

