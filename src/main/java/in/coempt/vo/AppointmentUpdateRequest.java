package in.coempt.vo;

import lombok.Data;

import java.time.LocalDate;
@Data
public class AppointmentUpdateRequest {
    private Long id;
    private Integer noOfSets;
    private LocalDate lastDateToSubmit;

}
