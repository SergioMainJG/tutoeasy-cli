package cli.tutoeasy.command;

import cli.tutoeasy.model.dto.CreateTutorDto;
import cli.tutoeasy.service.TutorService;
import picocli.CommandLine.*;

@Command(
        name = "create-tutor",
        description = "Register a new tutor",
        mixinStandardHelpOptions = true
)
public class TutorCommand implements Runnable {

    @Option(names = "--name", required = true)
    private String name;

    @Option(names = "--email", required = true)
    private String email;

    @Option(names = "--password", required = true, interactive = true)
    private String password;

    private final TutorService tutorService;

    public TutorCommand(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @Override
    public void run() {
        try {
            CreateTutorDto dto = new CreateTutorDto(name, email, password);
            tutorService.createTutor(dto);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}