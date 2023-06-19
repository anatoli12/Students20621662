package bg.tuvarna.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Student {
    private String name;
    private String facultyNumber;
    private Integer currentYear;
    private Program program;
    private Integer group;
    private Status status;
    @JsonIgnore
    private BigDecimal gpa;
    private List<Course> courseList;
}