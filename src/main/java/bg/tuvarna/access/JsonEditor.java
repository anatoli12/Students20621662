package bg.tuvarna.access;

import bg.tuvarna.models.Program;
import bg.tuvarna.models.Student;
import bg.tuvarna.repositories.StudentRepository;
import bg.tuvarna.services.StudentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * JsonEditor класът отговаря за работата с файлове, по-конкретно json, включващи информация за
 * студенти. Поддържа операции като отваряне, затваряне, запазване на файлове. Използва CLI за
 * обработката на данните за студентите.
 */
public class JsonEditor {

  /** Текущият път до файл. */
  private Path currentFilePath;
  /** Текущите данни. */
  private ObjectNode currentData;

  private boolean isFileOpen;
  /** Обектът от тип ObjectMapper отговаря за трансформацията между Java обект и JSON. */
  private ObjectMapper objectMapper;
  /** Данните за студентите */
  private StudentRepository studentRepository;

  public JsonEditor() {
    objectMapper = new ObjectMapper();
    isFileOpen = false;
    studentRepository = new StudentRepository();
  }
  /**
   * Отваря файл по зададен път, чете JSON данните и ги запазва в Student хранилището.
   *
   * @param filePathString пътят към файла за отваряне.
   */
  public void openFile(String filePathString) {
    Path filePath = Path.of(filePathString);
    File file = filePath.toFile();
    if (!file.exists()) {
      try {
        if (!file.createNewFile()) {
          System.out.println("Failed to create the file.");
          return;
        }
      } catch (IOException e) {
        System.out.println("An error occurred while creating the file.");
        e.printStackTrace();
        return;
      }
    }

    try {
      JsonNode jsonNode = objectMapper.readTree(file);
      if (jsonNode instanceof ObjectNode) {
        currentData = (ObjectNode) jsonNode;
      } else {
        currentData = objectMapper.createObjectNode(); // Create an empty ObjectNode
      }
      currentFilePath = filePath;
      isFileOpen = true;
      // Read student data from the file and save it in the repository
      JsonNode studentsJson = currentData.get("students");
      if (studentsJson != null && studentsJson.isArray()) {
        ArrayNode studentsArray = (ArrayNode) studentsJson;
        for (JsonNode studentNode : studentsArray) {
          Student student = objectMapper.treeToValue(studentNode, Student.class);
          studentRepository.save(student);
        }
      }

      System.out.println("File opened successfully.");
    } catch (IOException e) {
      System.out.println("An error occurred while reading the file.");
      e.printStackTrace();
    }
  }
  /** Затваря текущо отворения файл, ако има такъв. */
  public void closeFile() {
    if (!isFileOpen) {
      System.out.println("No file is currently open.");
      return;
    }

    currentFilePath = null;
    currentData = null;
    isFileOpen = false;
    objectMapper = new ObjectMapper();
    studentRepository = new StudentRepository();
    System.out.println("File closed successfully");
  }
  /** Запазва текущите данни във файла. Ако няма отворен файл, този метод не прави нищо. */
  public void save() {
    if (!isFileOpen) {
      System.out.println("No file is currently open.");
      return;
    }

    try {
      // Convert student data to JSON
      ObjectNode studentData = objectMapper.createObjectNode();
      studentData.put("students", studentRepository.getStudentsJson());

      // Merge student data with current data
      if (currentData != null) {
        currentData.set("students", studentData.get("students"));
      } else {
        currentData = studentData;
      }

      // Save the merged data to the file
      objectMapper.writeValue(currentFilePath.toFile(), currentData);
      System.out.println("Data saved successfully.");
    } catch (IOException e) {
      System.out.println("An error occurred while saving the file.");
    }
  }

  /**
   * Запазва текущите данни в нов файл, указан от newFilePathString. Ако няма отворен файл, този
   * метод не прави нищо.
   *
   * @param newFilePathString пътят до файла, където данните трябва да бъдат запазени
   */
  public void saveAs(String newFilePathString) {
    if (!isFileOpen) {
      System.out.println("No file is currently open.");
      return;
    }

    Path newFilePath = Path.of(newFilePathString);
    try {
      objectMapper.writeValue(newFilePath.toFile(), currentData);
    } catch (IOException e) {
      System.out.println("An error occurred while saving the file.");
    }
  }
  /** Отпечатва помощник за менюто, изброяващо всички налични команди и техния начин на употреба. */
  public void help() {
    System.out.println("Supported commands:");
    System.out.println("open <file_path> - Opens the file at <file_path>.");
    System.out.println("close - Closes the currently opened file.");
    System.out.println("save - Saves the changes to the currently opened file.");
    System.out.println(
        "saveas <new_file_path> - Saves the changes to a new file at <new_file_path>.");
    System.out.println("help - Shows this help message.");
    System.out.println("exit - Exits the program.");
    System.out.println("enroll <name> <program> <group> <facultyNumber> - Enroll a student.");
    System.out.println("advance <facultyNumber> - Advance a student to the next year.");
    System.out.println("change <facultyNumber> <option> <value> - Change student details.");
    System.out.println("graduate <facultyNumber> - Mark a student as graduated.");
    System.out.println("interrupt <facultyNumber> - Mark a student as dropped out.");
    System.out.println("resume <facultyNumber> - Reinstate a dropped-out student.");
    System.out.println("print <facultyNumber> - Print student details.");
    System.out.println("printall <program> <year> - Print all students in a program and year.");
    System.out.println(
        "enrollin <facultyNumber> <disciplineName> - Enroll a student in a discipline.");
    System.out.println(
        "addgrade <facultyNumber> <disciplineName> <grade> - Add a grade for a student in a discipline.");
    System.out.println("protocol <disciplineName> - Generate a protocol for a discipline.");
    System.out.println("report <facultyNumber> - Generate a report for a student.");
  }

  public void exit() {
    System.exit(0);
  }
  /** Основният цикъл на програмата, чакащ за вход от потребителя и изпълняващ команди. */
  public void run() {
    Scanner scanner = new Scanner(System.in);
    StudentService studentService = new StudentService(studentRepository);

    while (true) {
      System.out.print("> ");
      String command = scanner.nextLine().trim();
      String[] parts = command.split(" ", 2);
      if (parts.length > 0 && !parts[0].isEmpty()) {
        String cmd = parts[0];
        String[] cmdArgs = parts.length > 1 ? parts[1].split(" ") : new String[0];

        switch (cmd) {
          case "open" -> {
            if (cmdArgs.length < 1) {
              System.out.println("File path is required.");
            } else {
              openFile(cmdArgs[0]);
            }
          }
          case "close" -> closeFile();
          case "save" -> save();
          case "saveas" -> {
            if (cmdArgs.length < 1) {
              System.out.println("New file path is required.");
            } else {
              saveAs(cmdArgs[0]);
            }
          }
          case "help" -> help();
          case "exit" -> exit();
          case "enroll" -> {
            if (isFileOpen) {
              if (cmdArgs.length < 4) {
                System.out.println(
                    "Insufficient parameters. Usage: enroll <name> <program> <group> <facultyNumber>");
              } else {
                String name = cmdArgs[0];
                Program program = Program.valueOf(cmdArgs[1].toUpperCase());
                int group = Integer.parseInt(cmdArgs[2]);
                String facultyNumber = cmdArgs[3];
                studentService.enrollStudent(name, program, group, facultyNumber);
                System.out.println("Student enrolled successfully.");
              }
            } else {
              System.out.println("No file is currently open.");
            }
          }
          case "advance" -> {
            if (isFileOpen) {
              if (parts.length < 2) {
                System.out.println("Faculty number is required.");
              } else {
                String facultyNumber = parts[1];
                studentService.advance(facultyNumber);
              }
            } else {
              System.out.println("No file is currently open.");
            }
          }
          case "change" -> {
            if (isFileOpen) {
              if (cmdArgs.length < 1) {
                System.out.println(
                    "Insufficient parameters. Usage: change <facultyNumber> <option> <value>");
              } else {
                if (cmdArgs.length < 3) {
                  System.out.println(
                      "Insufficient parameters. Usage: change <facultyNumber> <option> <value>");
                } else {
                  String facultyNumber = cmdArgs[0];
                  String option = cmdArgs[1];
                  String value = cmdArgs[2];
                  studentService.change(facultyNumber, option, value);
                }
              }
            } else {
              System.out.println("No file is currently open.");
            }
          }
          case "graduate" -> {
            if (isFileOpen) {
              if (parts.length < 2) {
                System.out.println("Faculty number is required.");
              } else {
                String facultyNumber = parts[1];
                studentService.graduate(facultyNumber);
              }
            } else {
              System.out.println("No file is currently open.");
            }
          }
          case "interrupt" -> {
            if (isFileOpen) {
              if (parts.length < 2) {
                System.out.println("Faculty number is required.");
              } else {
                String facultyNumber = parts[1];
                studentService.interrupt(facultyNumber);
              }
            } else {
              System.out.println("No file is currently open.");
            }
          }
          case "resume" -> {
            if (isFileOpen) {
              if (parts.length < 2) {
                System.out.println("Faculty number is required.");
              } else {
                String facultyNumber = parts[1];
                studentService.resume(facultyNumber);
              }
            } else {
              System.out.println("No file is currently open.");
            }
          }
          case "print" -> {
            if (isFileOpen) {
              if (parts.length < 2) {
                System.out.println("Faculty number is required.");
              } else {
                String facultyNumber = parts[1];
                studentService.print(facultyNumber);
              }
            } else {
              System.out.println("No file is currently open.");
            }
          }
          case "printall" -> {
            if (isFileOpen) {
              if (parts.length < 2) {
                System.out.println("Insufficient parameters. Usage: printall <program> <year>");
              } else {
                String programName = cmdArgs[0];
                int year = Integer.parseInt(cmdArgs[1]);
                studentService.printAll(programName, year);
              }
            } else {
              System.out.println("No file is currently open.");
            }
          }
          case "enrollin" -> {
            if (isFileOpen) {
              if (parts.length < 2) {
                System.out.println(
                    "Insufficient parameters. Usage: enrollin <facultyNumber> <disciplineName>");
              } else {
                String facultyNumber = cmdArgs[0];
                String disciplineName = cmdArgs[1];
                studentService.enrollIn(facultyNumber, disciplineName);
              }
            } else {
              System.out.println("No file is currently open.");
            }
          }
          case "addgrade" -> {
            if (isFileOpen) {
              if (cmdArgs.length < 3) {
                System.out.println(
                    "Insufficient parameters. Usage: addgrade <facultyNumber> <disciplineName> <grade>");
              } else {
                String facultyNumber = cmdArgs[0];
                String disciplineName = cmdArgs[1];
                double grade = Double.parseDouble(cmdArgs[2]);
                studentService.addGrade(facultyNumber, disciplineName, grade);
              }
            } else {
              System.out.println("No file is currently open.");
            }
          }
          case "protocol" -> {
            if (isFileOpen) {
              if (parts.length < 2) {
                System.out.println("Discipline name is required.");
              } else {
                String disciplineName = parts[1];
                studentService.protocol(disciplineName);
              }
            } else {
              System.out.println("No file is currently open.");
            }
          }
          case "report" -> {
            if (isFileOpen) {
              if (parts.length < 2) {
                System.out.println("Faculty number is required.");
              } else {
                String facultyNumber = parts[1];
                studentService.report(facultyNumber);
              }
            } else {
              System.out.println("No file is currently open.");
            }
          }
          default -> System.out.println("Unknown command.");
        }
      }
    }
  }
}
