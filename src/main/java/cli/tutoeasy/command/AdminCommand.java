package cli.tutoeasy.command;

import cli.tutoeasy.model.dto.CreateAdministradorDto;
import cli.tutoeasy.service.AdministradorService;
import picocli.CommandLine.*;

@Command(
        name = "create-admin",
        description = "Registra un nuevo administrador",
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

    private final AdministradorService adminService;

    public AdminCommand(AdministradorService adminService) {
        this.adminService = adminService;
    }

    @Override
    public void run() {
        try {
            CreateAdministradorDto dto =
                    new CreateAdministradorDto(name, lastName, email, password);

            adminService.createAdministrador(dto);

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}