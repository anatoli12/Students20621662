package bg.tuvarna;

import bg.tuvarna.models.Program;
import bg.tuvarna.models.Status;
import bg.tuvarna.models.Student;
import bg.tuvarna.repositories.StudentRepository;
import bg.tuvarna.services.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StudentServiceTest {

  private StudentRepository repository;
  private StudentService service;
  private String name;
  private Program program;
  private int group;
  private String fn;

  @BeforeEach
  public void setUp(){
    repository = new StudentRepository();
    service = new StudentService(repository);
    name = "Test Student";
    program = Program.CS;  // Hypothetical program
    group = 1;
    fn = "123456";
  }
  @Test
  public void testEnrollStudent() {

    service.enrollStudent(name, program, group, fn);

    Student student = repository.findByFacultyNumber(fn);
    assertNotNull(student);
    assertEquals(name, student.getName());
    assertEquals(program, student.getProgram());
    assertEquals(group, student.getGroup());
    assertEquals(fn, student.getFacultyNumber());
  }
  @Test
  public void testAdvance() {
    // Assuming that the student has successfully completed all mandatory courses
    service.enrollStudent(name, program, group, fn);
    service.advance(fn);

    Student student = repository.findByFacultyNumber(fn);
    assertNotNull(student);
    assertEquals(2, student.getCurrentYear());
  }
  @Test
  public void testChangeGroup() {
    service.enrollStudent(name, program, group, fn);
    service.change(fn, "group", "2");

    Student student = repository.findByFacultyNumber(fn);
    assertNotNull(student);
    assertEquals(2, student.getGroup());
  }

  @Test
  public void testGraduation(){
    service.enrollStudent(name, program, group, fn);
    service.graduate(fn);

    Student student = repository.findByFacultyNumber(fn);
    assertEquals(Status.GRADUATED, student.getStatus());
  }

}