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

public class JsonEditor {

  private Path currentFilePath;
  private ObjectNode currentData;
  private boolean isFileOpen;

  private final ObjectMapper objectMapper;
  private final StudentRepository studentRepository;

  public JsonEditor() {
    objectMapper = new ObjectMapper();
    isFileOpen = false;
    studentRepository = new StudentRepository();
  }

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

  public void closeFile() {
    if (!isFileOpen) {
      System.out.println("No file is currently open.");
      return;
    }

    currentFilePath = null;
    currentData = null;
    isFileOpen = false;
  }

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

  public void run() {
    Scanner scanner = new Scanner(System.in);
    StudentService studentService = new StudentService(studentRepository);

    while (true) {
      System.out.print("> ");
      String command = scanner.nextLine().trim();
      String[] parts = command.split(" ", 2);
      if (parts.length > 0 && !parts[0].isEmpty()) {
        String cmd = parts[0];
        String args = parts.length > 1 ? parts[1] : "";

        switch (cmd) {
          case "open":
            if (args.isEmpty()) {
              System.out.println("File path is required.");
            } else {
              openFile(args);
            }
            break;
          case "close":
            closeFile();
            break;
          case "save":
            save();
            break;
          case "saveas":
            if (args.isEmpty()) {
              System.out.println("New file path is required.");
            } else {
              saveAs(args);
            }
            break;
          case "help":
            help();
            break;
          case "exit":
            exit();
            break;
          case "enroll":
            if (isFileOpen) {
              String[] enrollArgs = args.split("\\s+");
              if (enrollArgs.length < 4) {
                System.out.println(
                    "Insufficient parameters. Usage: enroll <name> <program> <group> <facultyNumber>");
              } else {
                String name = enrollArgs[0];
                Program program = Program.valueOf(enrollArgs[1].toUpperCase());
                int group = Integer.parseInt(enrollArgs[2]);
                String facultyNumber = enrollArgs[3];
                studentService.enrollStudent(name, program, group, facultyNumber);
                System.out.println("Student enrolled successfully.");
              }
            } else {
              System.out.println("No file is currently open.");
            }
            break;
          case "advance":
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
            break;
          case "change":
            if (isFileOpen) {
              if (args.isEmpty()) {
                System.out.println(
                        "Insufficient parameters. Usage: change <facultyNumber> <option> <value>");
              } else {
                String[] commandArgs = args.split(" ", 3);
                if (commandArgs.length < 3) {
                  System.out.println(
                          "Insufficient parameters. Usage: change <facultyNumber> <option> <value>");
                } else {
                  String facultyNumber = commandArgs[0];
                  String option = commandArgs[1];
                  String value = commandArgs[2];
                  studentService.change(facultyNumber, option, value);
                }
              }
            } else {
              System.out.println("No file is currently open.");
            }
            break;
          case "graduate":
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
            break;
          case "interrupt":
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
            break;
          case "resume":
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
            break;
          case "print":
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
            break;
          case "printall":
            if (isFileOpen) {
              if (parts.length < 3) {
                System.out.println("Insufficient parameters. Usage: printall <program> <year>");
              } else {
                String programName = parts[1];
                int year = Integer.parseInt(parts[2]);
                studentService.printAll(programName, year);
              }
            } else {
              System.out.println("No file is currently open.");
            }
            break;
          case "enrollin":
            if (isFileOpen) {
              if (parts.length < 3) {
                System.out.println(
                    "Insufficient parameters. Usage: enrollin <facultyNumber> <disciplineName>");
              } else {
                String facultyNumber = parts[1];
                String disciplineName = parts[2];
                studentService.enrollIn(facultyNumber, disciplineName);
              }
            } else {
              System.out.println("No file is currently open.");
            }
            break;
          case "addgrade":
            if (isFileOpen) {
              if (parts.length < 4) {
                System.out.println(
                    "Insufficient parameters. Usage: addgrade <facultyNumber> <disciplineName> <grade>");
              } else {
                String facultyNumber = parts[1];
                String disciplineName = parts[2];
                double grade = Double.parseDouble(parts[3]);
                studentService.addGrade(facultyNumber, disciplineName, grade);
              }
            } else {
              System.out.println("No file is currently open.");
            }
            break;
          case "protocol":
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
            break;
          case "report":
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
            break;
          default:
            System.out.println("Unknown command.");
            break;
        }
      }
    }
  }
}
