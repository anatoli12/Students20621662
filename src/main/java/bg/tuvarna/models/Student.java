package bg.tuvarna.models;

import lombok.Data;

import java.util.List;

/**
 * Класът Student представя модела на студент в университет. Съдържа информация за името на
 * студента, факултетния номер, текущата година на обучение, специалността, в която се обучава
 * студентът, групата, в която се намира, статуса на студента, и списък от курсове, които студентът изучава.
 */
@Data
public class Student {
  private String name;
  private String facultyNumber;
  private Integer currentYear;
  private Program program;
  private Integer group;
  private Status status;
  private List<Course> courseList;
}
