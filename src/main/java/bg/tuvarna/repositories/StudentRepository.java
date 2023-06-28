package bg.tuvarna.repositories;

import bg.tuvarna.models.Student;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
/**
 * Класът StudentRepository създава хранилище за студенти.
 * Предоставя методи за добавяне, търсене и извличане на студенти.
 */
public class StudentRepository {
  private final List<Student> studentList = new ArrayList<>();
  /**
   * Добавя студент към списъка.
   *
   * @param student обектът Student, който да бъде добавен.
   */
  public void save(Student student) {
    studentList.add(student);
  }
  /**
   * Търси студент по факултетен номер.
   *
   * @param fn факултетен номер на студента.
   * @return Връща студента, ако е намерен. В противен случай връща null.
   */
  public Student findByFacultyNumber(String fn) {
    for (Student student : studentList) {
      if (student.getFacultyNumber().equals(fn)) {
        return student;
      }
    }
    return null;
  }
  /**
   * Търси студенти по програма и текуща година.
   *
   * @param programName име на програмата.
   * @param year текущата година на обучение.
   * @return Връща списък със студенти, които отговарят на критериите.
   */
  public List<Student> findByProgramAndCurrentYear(String programName, int year) {
    List<Student> foundStudents = new ArrayList<>();
    for (Student student : studentList) {
      if (student.getProgram().name().equals(programName) && student.getCurrentYear() == year) {
        foundStudents.add(student);
      }
    }
    return foundStudents;
  }
  /**
   * Извлича всички студенти.
   *
   * @return Връща списък със всички студенти.
   */
  public List<Student> findAll() {
    return studentList;
  }

  /**
   * Извлича всички студенти в JSON формат.
   *
   * @return Връща JsonNode със студенти.
   */
  public JsonNode getStudentsJson() {
    ObjectMapper objectMapper = new ObjectMapper();
    ArrayNode studentsJsonArray = JsonNodeFactory.instance.arrayNode();

    for (Student student : studentList) {
      ObjectNode studentJson = objectMapper.valueToTree(student);
      studentsJsonArray.add(studentJson);
    }

    return studentsJsonArray;
  }
}
