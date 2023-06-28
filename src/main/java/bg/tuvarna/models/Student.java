package bg.tuvarna.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * Класът Student представя модела на студент в университет. Съдържа информация за името на
 * студента, факултетния номер, текущата година на обучение, специалността, в която се обучава
 * студентът, групата, в която се намира, статуса на студента, средния успех (който не се
 * сериализира в JSON), и списък от курсове, които студентът изучава.
 */
@Data
public class Student {
  private String name;
  private String facultyNumber;
  private Integer currentYear;
  private Program program;
  private Integer group;
  private Status status;
  @JsonIgnore private BigDecimal gpa;
  private List<Course> courseList;
}
