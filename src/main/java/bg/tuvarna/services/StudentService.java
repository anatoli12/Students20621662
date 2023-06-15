package bg.tuvarna.services;

import bg.tuvarna.models.*;
import bg.tuvarna.repositories.StudentRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
public class StudentService {
  private final StudentRepository studentRepository;

  public StudentService(StudentRepository studentRepository) {
    this.studentRepository = studentRepository;
  }

  // Записване на студент в първи курс
  public void enrollStudent(String name, Program program, int group, String fn) {
    Student student = new Student();
    student.setName(name);
    student.setProgram(program);
    student.setGroup(group);
    student.setFacultyNumber(fn);
    student.setStatus(Status.ACTIVE);
    student.setCourseList(new ArrayList<>());
    student.setCurrentYear(1);

    studentRepository.save(student);
  }

  private boolean isCourseSuccessfullyCompleted(Course course) {
    return course.isEnrolled() && course.isGraded() && course.getGrade() >= 3.0;
  }

  // Записване на студент в следващ курс
  public void advance(String fn) {
    Student student = studentRepository.findByFacultyNumber(fn);
    if (student == null) {
      System.out.println("No student found with this faculty number.");
      return;
    }
    if (student.getStatus() == Status.DROPOUT) {
      System.out.println("The student doesn't have rights.");
      return;
    }

    // Проверка дали студентът е минал задължителните дисциплини от текущия курс
    for (Course course : student.getCourseList()) {
      if (course.getDiscipline().getYear().equals(student.getCurrentYear())
          && course.getDiscipline().getIsMandatory()
          && !isCourseSuccessfullyCompleted(course)) {
        System.out.println(
            "The student has not successfully completed all mandatory disciplines of the current year.");
        return;
      }
    }

    // Преминаване в следващ курс
    student.setCurrentYear(student.getCurrentYear() + 1);
    System.out.println("The student has been advanced to the next year.");
  }

  public void change(String fn, String option, String value) {
    Student student = studentRepository.findByFacultyNumber(fn);
    if (student == null) {
      System.out.println("No student found with this faculty number.");
      return;
    }
    if (student.getStatus() == Status.DROPOUT) {
      System.out.println("The student doesn't have rights.");
      return;
    }

    switch (option.toLowerCase()) {
      case "group":
        student.setGroup(Integer.parseInt(value));
        System.out.println("Student's group has been changed.");
        break;

      case "year":
        if (Integer.parseInt(value) != student.getCurrentYear() + 1) {
          System.out.println("Can only advance to the next year.");
          return;
        }
        for (Course course : student.getCourseList()) {
          if (course.getDiscipline().getYear().equals(student.getCurrentYear())
              && course.getDiscipline().getIsMandatory()
              && !isCourseSuccessfullyCompleted(course)) {
            System.out.println(
                "The student has not successfully completed all mandatory disciplines of the current year.");
            return;
          }
        }
        student.setCurrentYear(student.getCurrentYear() + 1);
        System.out.println("The student has been advanced to the next year.");
        break;

      case "program":
        Program newProgram = Program.valueOf(value.toUpperCase());
        for (Discipline discipline : newProgram.getDisciplines()) {
          if (discipline.getYear() <= student.getCurrentYear() && discipline.getIsMandatory()) {
            boolean hasCourse = false;
            for (Course course : student.getCourseList()) {
              if (course.getDiscipline() == discipline && isCourseSuccessfullyCompleted(course)) {
                hasCourse = true;
                break;
              }
            }
            if (!hasCourse) {
              System.out.println(
                  "The student has not successfully completed all mandatory disciplines for the new program.");
              return;
            }
          }
        }
        student.setProgram(newProgram);
        System.out.println("The student's program has been changed.");
        break;

      default:
        System.out.println("Invalid option.");
        break;
    }
  }

  public void graduate(String fn) {
    Student student = studentRepository.findByFacultyNumber(fn);
    if (student == null) {
      System.out.println("No student found with this faculty number.");
      return;
    }
    if (student.getStatus() == Status.DROPOUT) {
      System.out.println("The student doesn't have rights.");
      return;
    }
    for (Course course : student.getCourseList()) {
      if (!isCourseSuccessfullyCompleted(course)) {
        System.out.println("The student has not successfully completed all disciplines.");
        return;
      }
    }

    student.setStatus(Status.GRADUATED);
    System.out.println("The student has graduated.");
  }

  public void interrupt(String fn) {
    Student student = studentRepository.findByFacultyNumber(fn);
    if (student == null) {
      System.out.println("No student found with this faculty number.");
      return;
    }

    student.setStatus(Status.DROPOUT);
    System.out.println("The student has dropped out.");
  }
  public void resume(String fn){
    Student student = studentRepository.findByFacultyNumber(fn);
    if (student == null) {
      System.out.println("No student found with this faculty number.");
      return;
    }
    if(student.getStatus() == Status.DROPOUT){
      student.setStatus(Status.ACTIVE);
      System.out.println("Student rights successfully regained.");
    }
    else {
      System.out.println("Student has not dropped out.");
    }
  }

  public void print(String fn) {
    Student student = studentRepository.findByFacultyNumber(fn);
    if (student == null) {
      System.out.println("No student found with this faculty number.");
      return;
    }

    System.out.println("Student name: " + student.getName());
    System.out.println("Program: " + student.getProgram().getName());
    System.out.println("Group: " + student.getGroup());
    System.out.println("Faculty Number: " + student.getFacultyNumber());
    System.out.println("Status: " + student.getStatus());
    System.out.println("Current Year: " + student.getCurrentYear());
    System.out.println("Courses:");
    for (Course course : student.getCourseList()) {
      System.out.println("  Course: " + course.getDiscipline().getName());
      System.out.println("  Grade: " + (course.isGraded() ? course.getGrade() : "Not graded yet"));
    }
  }
// Insufficent parameters
  public void printAll(String programName, int year) {
    List<Student> students = studentRepository.findByProgramAndCurrentYear(programName, year);
    if (students.isEmpty()) {
      System.out.println("No students found in this program and year.");
      return;
    }

    for (Student student : students) {
      System.out.println("-----");
      System.out.println("Student name: " + student.getName());
      System.out.println("Group: " + student.getGroup());
      System.out.println("Faculty Number: " + student.getFacultyNumber());
      System.out.println("Status: " + student.getStatus());
      System.out.println("Courses:");
      for (Course course : student.getCourseList()) {
        System.out.println("  Course: " + course.getDiscipline().getName());
        System.out.println("  Grade: " + (course.isGraded() ? course.getGrade() : "Not graded yet"));
      }
      System.out.println("-----");
    }
  }
// Insufficent parameters
  public void enrollIn(String fn, String disciplineName) {
    Student student = studentRepository.findByFacultyNumber(fn);
    if (student == null) {
      System.out.println("No student found with this faculty number.");
      return;
    }

    Discipline discipline;
    try {
      discipline = Discipline.valueOf(disciplineName);
    } catch (IllegalArgumentException e) {
      System.out.println("No discipline found with this name.");
      return;
    }

    // Проверка дали дисциплината е от съответната специалност и курс
    if (!student.getProgram().getDisciplines().contains(discipline)
            || !discipline.getYear().equals(student.getCurrentYear())) {
      System.out.println("The student cannot enroll in this discipline.");
      return;
    }

    // Създаване на нова дисциплина за студента и добавяне към списъка му с дисциплини
    Course course = new Course();
    course.setDiscipline(discipline);
    course.setEnrolled(true);
    course.setGraded(false);

    student.getCourseList().add(course);

    // Запазване на промените в repository
    studentRepository.save(student);

    System.out.println("The student has been enrolled in the course.");
  }

  public void addGrade(String fn, String disciplineName, double grade) {
    // Намиране на студента по факултетен номер
    Student student = studentRepository.findByFacultyNumber(fn);
    if (student == null) {
      System.out.println("No student found with this faculty number.");
      return;
    }

    // Опит за намиране на дисциплината по име
    Discipline discipline;
    try {
      discipline = Discipline.valueOf(disciplineName);
    } catch (IllegalArgumentException e) {
      System.out.println("No discipline found with this name.");
      return;
    }

    // Намиране на дисциплината в списъка с дисциплини на студента
    Course course = student.getCourseList().stream()
            .filter(c -> c.getDiscipline() == discipline)
            .findFirst()
            .orElse(null);

    // Проверка дали студентът е записан за тази дисциплина
    if (course == null || !course.isEnrolled()) {
      System.out.println("The student is not enrolled in this discipline.");
      return;
    }

    // Добавяне на оценката и маркиране на дисциплината като оценена
    course.setGrade(grade);
    course.setGraded(true);

    // Запазване на промените в базата данни
    studentRepository.save(student);

    System.out.println("The grade has been added.");
  }

  public void protocol(String disciplineName) {
    // Опит за намиране на дисциплината по име
    Discipline discipline;
    try {
      discipline = Discipline.valueOf(disciplineName);
    } catch (IllegalArgumentException e) {
      System.out.println("No discipline found with this name.");
      return;
    }

    // Извличане на всички студенти от базата данни
    List<Student> students = studentRepository.findAll();

    // Филтриране на студентите, които са записани за дадената дисциплина
    List<Student> enrolledStudents = students.stream()
            .filter(student -> student.getCourseList().stream()
                    .anyMatch(course -> course.getDiscipline() == discipline && course.isEnrolled()))
            .collect(Collectors.toList());


    // Групиране на студентите по специалност и курс
    Map<Program, Map<Integer, List<Student>>> groupedStudents = enrolledStudents.stream()
            .collect(Collectors.groupingBy(
                    Student::getProgram,
                    TreeMap::new,
                    Collectors.groupingBy(
                            Student::getCurrentYear,
                            TreeMap::new,
                            Collectors.toList())));

    // Извеждане на протоколите
    groupedStudents.forEach((program, byYear) -> {
      System.out.println("Program: " + program);
      byYear.forEach((year, studentList) -> {
        System.out.println("Year: " + year);
        studentList.forEach(student -> System.out.println("Student: " + student.getName()));
      });
    });
  }

  public void report(String fn) {
    Student student = studentRepository.findByFacultyNumber(fn);
    if (student == null) {
      System.out.println("No student found with this faculty number.");
      return;
    }

    List<Course> enrolledCourses = student.getCourseList();
    double totalGrade = 0.0;
    int gradedCoursesCount = 0;
    List<Discipline> ungradedCourses = new ArrayList<>();

    System.out.println("Student report for " + student.getName() + "(" + student.getFacultyNumber() + ")");
    System.out.println("Completed courses:");

    for (Course course : enrolledCourses) {
      if (course.isSuccessfullyCompleted()) {
        totalGrade += course.getGrade();
        gradedCoursesCount++;
        System.out.println(course.getDiscipline().getName() + " - " + course.getGrade());
      } else if (course.isEnrolled()) {
        ungradedCourses.add(course.getDiscipline());
      }
    }

    System.out.println("Ungraded courses:");
    for (Discipline discipline : ungradedCourses) {
      System.out.println(discipline.getName());
    }

    double averageGrade = (gradedCoursesCount > 0) ? (totalGrade / gradedCoursesCount) : 0;
    System.out.println("Average grade: " + averageGrade);
  }
}
