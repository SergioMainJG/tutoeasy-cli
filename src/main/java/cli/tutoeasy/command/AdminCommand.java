package cli.tutoeasy.command;

import cli.tutoeasy.model.dto.CreateAdministratorDto;
import cli.tutoeasy.service.AdministratorService;
import picocli.CommandLine.*;

@Command(
        name = "create-admin",
        description = "Register a new administrator",
        mixinStandardHelpOptions = true
)
public class AdminCommand implements Runnable {

    @Option(names = "--name", required = true)
    private String name;

    @Option(names = "--lastname", required = true)
    private String lastName;

    @Option(names = "--email", required = true)
    private String email;

    @Option(names = "--password", required = true, interactive = true)
    private String password;

    private final AdministratorService adminService;

    public AdminCommand(AdministratorService adminService) {
        this.adminService = adminService;
    }

    @Override
    public void run() {
        try {
            CreateAdministratorDto dto =
                    new CreateAdministratorDto(name, lastName, email, password);

            adminService.createAdministrator(dto);

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}