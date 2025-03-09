package in.coempt.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tbl_exam_series")
@Data
public class ExamSeries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "series_name", unique = true)
    private String seriesName;

    @Column(name = "exam_time")
    private String examTime;

    @Column(name = "total_marks")
    private String totalMarks;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "exam_start_date")
    private String examStartDate;

    @Column(name = "exam_end_date")
    private String examEndDate;

    // Getters and Setters
}

