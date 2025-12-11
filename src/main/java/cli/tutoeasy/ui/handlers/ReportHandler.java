package cli.tutoeasy.ui.handlers;

import cli.tutoeasy.command.AppFactory;
import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.dto.CreateReportDto;
import cli.tutoeasy.model.dto.ReportDto;
import cli.tutoeasy.service.ReportService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * <p>Handler for report management operations.</p>
 * <p>Allows administrators to view, generate, delete, and export reports.</p>
 */
public class ReportHandler {

    /**
     * <p>Handles the main report management menu.</p>
     * <p>Provides options to view all reports, filter by type, generate new reports, or delete existing ones.</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    public static void handleReports(Scanner scanner, AppFactory factory) {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    REPORT MANAGEMENT                       ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        System.out.println("1. View all reports");
        System.out.println("2. View reports by type");
        System.out.println("3. Generate new report");
        System.out.println("4. Delete report");
        System.out.println("0. Return");
        System.out.print("\nOption: ");

        String input = scanner.nextLine().trim();

        try {
            int option = Integer.parseInt(input);

            switch (option) {
                case 1:
                    handleViewAllReports(scanner, factory);
                    break;
                case 2:
                    handleViewReportsByType(scanner, factory);
                    break;
                case 3:
                    handleGenerateReport(scanner, factory);
                    break;
                case 4:
                    handleDeleteReport(scanner, factory);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("\n Invalid option");
            }

        } catch (NumberFormatException e) {
            System.out.println("\n Invalid option");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * <p>Handles viewing all reports with an optional limit.</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    private static void handleViewAllReports(Scanner scanner, AppFactory factory) {
        System.out.print("\nLimit of reports to show (Enter for 20): ");
        String limitStr = scanner.nextLine().trim();
        Integer limit = limitStr.isEmpty() ? 20 : Integer.parseInt(limitStr);

        ReportService service = factory.getReportService();
        List<ReportDto> reports = service.getAllReports(limit);

        displayReports(reports, "All Reports");
    }

    /**
     * <p>Handles viewing reports filtered by specific types (tutoring sessions, tutors, students).</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    private static void handleViewReportsByType(Scanner scanner, AppFactory factory) {
        System.out.println("\nReport types:");
        System.out.println("1. tutoring_sessions (Tutoring sessions)");
        System.out.println("2. tutors (Tutors)");
        System.out.println("3. students (Students)");
        System.out.print("\nSelect type (1-3): ");

        String typeInput = scanner.nextLine().trim();
        String reportType;

        switch (typeInput) {
            case "1":
                reportType = "tutoring_sessions";
                break;
            case "2":
                reportType = "tutors";
                break;
            case "3":
                reportType = "students";
                break;
            default:
                System.out.println("\n Invalid type");
                return;
        }

        System.out.print("Report limit (Enter for all): ");
        String limitStr = scanner.nextLine().trim();
        Integer limit = limitStr.isEmpty() ? null : Integer.parseInt(limitStr);

        ReportService service = factory.getReportService();
        List<ReportDto> reports = service.getReportsByType(reportType, limit);

        displayReports(reports, "Reports of " + reportType);
    }

    /**
     * <p>Handles the generation of a new report.</p>
     * <p>Prompts for report type and content, creates the report, and optionally offers export.</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    private static void handleGenerateReport(Scanner scanner, AppFactory factory) {
        System.out.println("\n GENERATE NEW REPORT");
        System.out.println("─".repeat(60));

        System.out.println("\nReport type:");
        System.out.println("1. tutoring_sessions");
        System.out.println("2. tutors");
        System.out.println("3. students");
        System.out.print("\nSelect (1-3): ");

        String typeInput = scanner.nextLine().trim();
        String reportType;

        switch (typeInput) {
            case "1":
                reportType = "tutoring_sessions";
                break;
            case "2":
                reportType = "tutors";
                break;
            case "3":
                reportType = "students";
                break;
            default:
                System.out.println("\n Invalid type");
                return;
        }

        System.out.print("\nContent/Description of the report:\n> ");
        String content = scanner.nextLine().trim();

        if (content.isEmpty()) {
            System.out.println("\n Content cannot be empty");
            return;
        }

        CreateReportDto dto = new CreateReportDto(reportType, content);

        ReportService service = factory.getReportService();
        int adminId = AuthSession.getCurrentUser().getId();

        var response = service.createReport(adminId, dto);

        if (response.success()) {
            System.out.println("\n " + response.message());

            System.out.print("\nDo you want to export the report to a file? (y/n): ");
            String exportChoice = scanner.nextLine().trim().toLowerCase();

            if (exportChoice.equals("s") || exportChoice.equals("si") || exportChoice.equals("yes") || exportChoice.equals("y")) {
                handleExportReport(scanner, factory, reportType, content);
            }
        } else {
            System.out.println("\n " + response.message());
        }
    }

    /**
     * <p>Handles exporting a report to a file.</p>
     * <p>Supports TXT, MD, and DOCX formats.</p>
     *
     * @param scanner    Scanner for reading user input.
     * @param factory    AppFactory for accessing services.
     * @param reportType The type of the report.
     * @param content    The content of the report.
     */
    private static void handleExportReport(Scanner scanner, AppFactory factory,
                                           String reportType, String content) {
        System.out.println("\nAvailable formats:");
        System.out.println("1. TXT (Plain text)");
        System.out.println("2. MD (Markdown)");
        System.out.println("3. DOCX (Word)");
        System.out.print("\nSelect format (1-3): ");

        String formatInput = scanner.nextLine().trim();
        String format;

        switch (formatInput) {
            case "1":
                format = "txt";
                break;
            case "2":
                format = "md";
                break;
            case "3":
                format = "docx";
                break;
            default:
                System.out.println("\n Invalid format, using TXT by default");
                format = "txt";
        }

        System.out.print("Open file automatically? (y/n): ");
        String openChoice = scanner.nextLine().trim().toLowerCase();
        boolean openFile = openChoice.equals("s") || openChoice.equals("si") ||
                openChoice.equals("yes") || openChoice.equals("y");

        ReportService service = factory.getReportService();
        int adminId = AuthSession.getCurrentUser().getId();

        var exportResponse = service.exportReportToFile(
                adminId, reportType, content, format, openFile
        );

        if (exportResponse.success()) {
            System.out.println("\n " + exportResponse.message());
        } else {
            System.out.println("\n  " + exportResponse.message());
        }
    }

    /**
     * <p>Handles the deletion of a report by its ID.</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    private static void handleDeleteReport(Scanner scanner, AppFactory factory) {
        System.out.print("\nID of the report to delete: ");
        String idStr = scanner.nextLine().trim();

        try {
            int reportId = Integer.parseInt(idStr);
            int adminId = AuthSession.getCurrentUser().getId();

            ReportService service = factory.getReportService();
            var response = service.deleteReport(reportId, adminId);

            if (response.success()) {
                System.out.println("\n " + response.message());
            } else {
                System.out.println("\n " + response.message());
            }

        } catch (NumberFormatException e) {
            System.out.println("\n Invalid ID");
        }
    }

    /**
     * <p>Displays a list of reports with details.</p>
     *
     * @param reports List of reports to display.
     * @param title   Title for the list.
     */
    private static void displayReports(List<ReportDto> reports, String title) {
        System.out.println("\n" + title);
        System.out.println("═".repeat(60));

        if (reports.isEmpty()) {
            System.out.println("\n No reports to show");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (int i = 0; i < reports.size(); i++) {
            ReportDto report = reports.get(i);
            String typeSymbol = getTypeSymbol(report.reportType());

            System.out.println("\n" + (i + 1) + ". " + typeSymbol + " " +
                    report.reportType().toUpperCase());
            System.out.println("   ID: " + report.id());
            System.out.println("   Created by: " + report.adminUsername());
            System.out.println("   Date: " + report.createdAt().format(formatter));
            System.out.println("   Content:");

            String contentPreview = report.content().length() > 100
                    ? report.content().substring(0, 97) + "..."
                    : report.content();
            System.out.println("      " + contentPreview);
        }

        System.out.println("\n═".repeat(60));
        System.out.println("Total: " + reports.size() + " report(s)");
    }

    /**
     * <p>Gets a symbol representing the report type.</p>
     *
     * @param type The report type.
     * @return A string symbol.
     */
    private static String getTypeSymbol(String type) {
        return switch (type.toLowerCase()) {
            case "tutoring_sessions" -> "[SESSIONS]";
            case "tutors" -> "[TUTORS]";
            case "students" -> "[STUDENTS]";
            default -> "[REPORT]";
        };
    }

    /**
     * <p>Clears the console screen.</p>
     * <p>Works on Windows (cmd/cls) and Unix-like systems (ANSI escape codes).</p>
     */
    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) System.out.println();
        }
    }
}