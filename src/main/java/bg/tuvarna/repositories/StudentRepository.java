package bg.tuvarna.repositories;

import bg.tuvarna.models.Student;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class StudentRepository {
  private final List<Student> studentList = new ArrayList<>();

  public void save(Student student) {
    studentList.add(student);
  }

  public Student findByFacultyNumber(String fn) {
    for (Student student : studentList) {
      if (student.getFacultyNumber().equals(fn)) {
        return student;
      }
    }
    return null;
  }

  public List<Student> findByProgramAndCurrentYear(String programName, int year) {
    List<Student> foundStudents = new ArrayList<>();
    for (Student student : studentList) {
      if (student.getProgram().name().equals(programName) && student.getCurrentYear() == year) {
        foundStudents.add(student);
      }
    }
    return foundStudents;
  }

  public List<Student> findAll() {
    return studentList;
  }

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
