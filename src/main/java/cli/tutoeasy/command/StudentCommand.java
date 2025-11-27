package cli.tutoeasy.command;

import cli.tutoeasy.model.dto.CreateStudentDto;
import cli.tutoeasy.service.StudentService;
import picocli.CommandLine.*;

@Command(
        name = "create-student",
        description = "Register a new student",
        mixinStandardHelpOptions = true
)
public class StudentCommand implements Runnable {

    @Option(names = "--name", required = true)
    private String name;

    @Option(names = "--email", required = true)
    private String email;

    @Option(names = "--password", required = true, interactive = true)
    private String password;

    private final StudentService studentService;

    public StudentCommand(StudentService studentService) {
        this.studentService = studentService;
    }

    @Override
    public void run() {
        try {
            CreateStudentDto dto = new CreateStudentDto(name, email, password);
            studentService.createStudent(dto);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}