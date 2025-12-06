package cli.tutoeasy.util.files;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * Utility class for exporting reports to various file formats.
 * This class provides static methods to generate report files in different formats
 * including plain text, Markdown, and Microsoft Word documents.
 * </p>
 *
 * <p>
 * The utility supports three main export formats:
 * </p>
 * <ul>
 *     <li><strong>TXT</strong> - Plain text with simple formatting using ASCII characters</li>
 *     <li><strong>MD</strong> - Markdown format with proper headers and formatting syntax</li>
 *     <li><strong>DOCX</strong> - Microsoft Word document with professional formatting</li>
 * </ul>
 *
 * <h3>File Generation:</h3>
 * <p>
 * All files are created in the current working directory with standardized naming:
 * {@code [filename].[extension]}. The files include:
 * </p>
 * <ul>
 *     <li>Report header with title and metadata</li>
 *     <li>Timestamp of generation</li>
 *     <li>Administrator information</li>
 *     <li>Report content</li>
 *     <li>Footer with system information</li>
 * </ul>
 *
 * <h3>Usage Example:</h3>
 * <pre>
 * String filePath = FileExportUtil.exportReport(
 *     "monthly_report_202401",
 *     "tutoring_sessions",
 *     "This month we had 150 sessions with 95% completion rate",
 *     "admin_user",
 *     "docx"
 * );
 * System.out.println("Report exported to: " + filePath);
 * </pre>
 *
 * <h3>Dependencies:</h3>
 * <p>
 * This class requires Apache POI library for Word document generation.
 * Ensure the following dependency is in your pom.xml:
 * </p>
 * <pre>
 * &lt;dependency&gt;
 *     &lt;groupId&gt;org.apache.poi&lt;/groupId&gt;
 *     &lt;artifactId&gt;poi-ooxml&lt;/artifactId&gt;
 *     &lt;version&gt;5.5.1&lt;/version&gt;
 * &lt;/dependency&gt;
 * </pre>
 *
 * @version 1.0
 * @since 1.0
 */
public class FileExportUtil {

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private FileExportUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Exports a report to a file in the specified format.
     *
     * <p>
     * This is the main entry point for report export functionality. It delegates
     * to format-specific methods based on the requested format.
     * </p>
     *
     * <p>
     * The method performs the following steps:
     * </p>
     * <ol>
     *     <li>Validates the format parameter</li>
     *     <li>Delegates to the appropriate format-specific method</li>
     *     <li>Optionally opens the file with the system's default application</li>
     *     <li>Returns the absolute path of the created file</li>
     * </ol>
     *
     * <p>
     * If an unsupported format is specified, defaults to plain text (.txt).
     * </p>
     *
     * @param filename The base filename without extension (e.g., "report_2024_01")
     * @param reportType The type of report (e.g., "tutoring_sessions", "tutors", "students")
     * @param content The main content of the report
     * @param adminUsername The username of the administrator generating the report
     * @param format The desired output format: "txt", "md", or "docx"
     * @param openFile Whether to automatically open the file after creation
     * @return The absolute path to the created file as a String
     * @throws IOException if an error occurs during file creation or writing
     */
    public static String exportReport(String filename, String reportType,
                                      String content, String adminUsername,
                                      String format, boolean openFile) throws IOException {
        String filePath = switch (format.toLowerCase()) {
            case "txt", "text" -> exportToTxt(filename, reportType, content, adminUsername);
            case "md", "markdown" -> exportToMarkdown(filename, reportType, content, adminUsername);
            case "docx", "word" -> exportToDocx(filename, reportType, content, adminUsername);
            default -> exportToTxt(filename, reportType, content, adminUsername);
        };

        if (openFile) {
            openFileWithDefaultApp(filePath);
        }

        return filePath;
    }

    /**
     * Exports a report to a plain text file (.txt).
     *
     * <p>
     * Creates a simple, readable text file with ASCII art borders and clear sections.
     * The format is designed to be human-readable in any text editor or terminal.
     * </p>
     *
     * <h3>File Structure:</h3>
     * <pre>
     * ================================================================================
     * TUTOEASY ADMINISTRATIVE REPORT
     * ================================================================================
     * Report Type: [type]
     * Generated: [timestamp]
     * Generated by: [admin]
     * --------------------------------------------------------------------------------
     * [content]
     * ================================================================================
     * Generated by TutoEasy Report System
     * ================================================================================
     * </pre>
     *
     * <p>
     * The file is encoded in UTF-8 to support international characters.
     * </p>
     *
     * @param filename The base filename without extension
     * @param reportType The type of report
     * @param content The report content
     * @param adminUsername The administrator's username
     * @return The absolute path to the created .txt file
     * @throws IOException if the file cannot be created or written
     */
    private static String exportToTxt(String filename, String reportType,
                                      String content, String adminUsername) throws IOException {
        Path filePath = Paths.get(filename + ".txt");

        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(80)).append("\n");
        sb.append("TUTOEASY ADMINISTRATIVE REPORT\n");
        sb.append("=".repeat(80)).append("\n\n");
        sb.append("Report Type: ").append(reportType.toUpperCase()).append("\n");
        sb.append("Generated: ").append(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("Generated by: ").append(adminUsername).append("\n\n");
        sb.append("-".repeat(80)).append("\n\n");
        sb.append(content).append("\n\n");
        sb.append("=".repeat(80)).append("\n");
        sb.append("Generated by TutoEasy Report System\n");
        sb.append("=".repeat(80)).append("\n");

        Files.writeString(filePath, sb.toString(), StandardCharsets.UTF_8);

        return filePath.toAbsolutePath().toString();
    }

    /**
     * Opens a file with the system's default application.
     *
     * <p>
     * This method attempts to open the specified file using the operating system's
     * default application associated with the file type. It uses Java's {@link Desktop}
     * API to interact with the native desktop environment.
     * </p>
     *
     * <h3>Requirements:</h3>
     * <ul>
     *     <li>The system must support desktop operations (not headless)</li>
     *     <li>The file must exist at the specified path</li>
     *     <li>A default application must be registered for the file type</li>
     *     <li>The application must have permission to launch external programs</li>
     * </ul>
     *
     * <h3>Behavior:</h3>
     * <p>
     * If desktop support is available and the file exists, the system will:
     * </p>
     * <ol>
     *     <li>Locate the default application for the file type</li>
     *     <li>Launch the application with the file as parameter</li>
     *     <li>Display a success message in the console</li>
     * </ol>
     *
     * <p>
     * If the operation fails for any reason (no desktop support, file not found,
     * no default application, I/O error), an informative error message is displayed
     * but the method does not throw an exception. This ensures that file generation
     * is not considered failed just because the file couldn't be opened.
     * </p>
     *
     * <h3>Platform Notes:</h3>
     * <ul>
     *     <li><strong>Windows:</strong> Uses file associations from the registry</li>
     *     <li><strong>macOS:</strong> Uses the 'open' command and Launch Services</li>
     *     <li><strong>Linux:</strong> Uses xdg-open or the desktop environment's handler</li>
     * </ul>
     *
     * <h3>Error Handling:</h3>
     * <p>
     * Possible error scenarios and their messages:
     * </p>
     * <ul>
     *     <li><strong>Headless environment:</strong> "Desktop functionality not supported (running on server without GUI)"</li>
     *     <li><strong>File not found:</strong> "The file does not exist at path: [path]"</li>
     *     <li><strong>I/O error:</strong> "I/O error when trying to open file: [error message]"</li>
     *     <li><strong>Security error:</strong> "Security restriction prevented opening file: [error message]"</li>
     * </ul>
     *
     * <h3>Usage Example:</h3>
     * <pre>
     * String reportPath = "/path/to/report.docx";
     * openFileWithDefaultApp(reportPath);
     * // File opens in Microsoft Word (or default .docx handler)
     * </pre>
     *
     * @param filePath The absolute or relative path to the file to open.
     *                 Must not be null.
     *
     * @see Desktop
     * @see Desktop#open(File)
     * @see Desktop#isDesktopSupported()
     */
    private static void openFileWithDefaultApp(String filePath) {
        try {
            File file = new File(filePath);

            if (!Desktop.isDesktopSupported()) {
                System.err.println("Desktop functionality not supported (running on server without GUI).");
                System.out.println("File saved successfully but cannot be opened automatically.");
                return;
            }

            Desktop desktop = Desktop.getDesktop();

            if (!file.exists()) {
                System.err.println("The file does not exist at path: " + filePath);
                return;
            }

            if (!desktop.isSupported(Desktop.Action.OPEN)) {
                System.err.println("Opening files is not supported on this system.");
                System.out.println("File saved successfully but cannot be opened automatically.");
                return;
            }

            desktop.open(file);
            System.out.println("File opened successfully with default application.");

        } catch (IOException e) {
            System.err.println("I/O error when trying to open file: " + e.getMessage());
            System.out.println("File was saved successfully but could not be opened automatically.");
        } catch (SecurityException e) {
            System.err.println("Security restriction prevented opening file: " + e.getMessage());
            System.out.println("File was saved successfully but could not be opened automatically.");
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid file path: " + e.getMessage());
        } catch (UnsupportedOperationException e) {
            System.err.println("Opening files is not supported on this platform: " + e.getMessage());
            System.out.println("File saved successfully but cannot be opened automatically.");
        }
    }

    /**
     * Exports a report to a Markdown file (.md).
     *
     * <p>
     * Creates a Markdown-formatted file suitable for documentation systems,
     * GitHub, or conversion to other formats. Uses standard Markdown syntax
     * for headers, emphasis, and code blocks.
     * </p>
     *
     * <h3>File Structure:</h3>
     * <pre>
     * # TutoEasy Administrative Report
     *
     * ## Report Information
     *
     * - **Type:** [type]
     * - **Generated:** [timestamp]
     * - **Generated by:** [admin]
     *
     * ---
     *
     * ## Report Content
     *
     * [content]
     *
     * ---
     *
     * *Generated by TutoEasy Report System*
     * </pre>
     *
     * <p>
     * The Markdown format allows for easy rendering in documentation tools,
     * version control systems, and static site generators.
     * </p>
     *
     * @param filename The base filename without extension
     * @param reportType The type of report
     * @param content The report content
     * @param adminUsername The administrator's username
     * @return The absolute path to the created .md file
     * @throws IOException if the file cannot be created or written
     */
    private static String exportToMarkdown(String filename, String reportType,
                                           String content, String adminUsername) throws IOException {
        Path filePath = Paths.get(filename + ".md");

        StringBuilder sb = new StringBuilder();
        sb.append("# TutoEasy Administrative Report\n\n");
        sb.append("## Report Information\n\n");
        sb.append("- **Type:** ").append(reportType.toUpperCase()).append("\n");
        sb.append("- **Generated:** ").append(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("- **Generated by:** ").append(adminUsername).append("\n\n");
        sb.append("---\n\n");
        sb.append("## Report Content\n\n");
        sb.append(content).append("\n\n");
        sb.append("---\n\n");
        sb.append("*Generated by TutoEasy Report System*\n");

        Files.writeString(filePath, sb.toString(), StandardCharsets.UTF_8);

        return filePath.toAbsolutePath().toString();
    }

    /**
     * Exports a report to a Microsoft Word document (.docx).
     *
     * <p>
     * Creates a professionally formatted Word document using Apache POI.
     * The document includes styled headers, formatted metadata, and proper
     * paragraph spacing.
     * </p>
     *
     * <h3>Document Structure:</h3>
     * <ul>
     *     <li><strong>Title</strong> - Bold, 16pt header</li>
     *     <li><strong>Metadata</strong> - Bold labels with regular text values</li>
     *     <li><strong>Content</strong> - Normal paragraph formatting</li>
     *     <li><strong>Footer</strong> - Italicized system attribution</li>
     * </ul>
     *
     * <h3>Styling:</h3>
     * <ul>
     *     <li>Title: Calibri 16pt Bold</li>
     *     <li>Headers: Calibri 12pt Bold</li>
     *     <li>Content: Calibri 11pt Regular</li>
     *     <li>Footer: Calibri 10pt Italic</li>
     * </ul>
     *
     * <p>
     * The generated document is compatible with Microsoft Word 2007 and later,
     * as well as other word processors that support the OOXML format.
     * </p>
     *
     * <h3>Technical Notes:</h3>
     * <p>
     * This method uses Apache POI's XWPF (XML Word Processing Format) API
     * to create the document. The document is written directly to disk
     * without intermediate buffering.
     * </p>
     *
     * @param filename The base filename without extension
     * @param reportType The type of report
     * @param content The report content
     * @param adminUsername The administrator's username
     * @return The absolute path to the created .docx file
     * @throws IOException if the file cannot be created or written,
     *         or if there's an error with the POI library
     */
    private static String exportToDocx(String filename, String reportType,
                                       String content, String adminUsername) throws IOException {
        Path filePath = Paths.get(filename + ".docx");

        try (XWPFDocument document = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(filePath.toFile())) {

            XWPFParagraph title = document.createParagraph();
            XWPFRun titleRun = title.createRun();
            titleRun.setText("TutoEasy Administrative Report");
            titleRun.setBold(true);
            titleRun.setFontSize(16);
            titleRun.addBreak();

            XWPFParagraph typePara = document.createParagraph();
            XWPFRun typeLabel = typePara.createRun();
            typeLabel.setText("Report Type: ");
            typeLabel.setBold(true);
            XWPFRun typeValue = typePara.createRun();
            typeValue.setText(reportType.toUpperCase());

            XWPFParagraph timePara = document.createParagraph();
            XWPFRun timeLabel = timePara.createRun();
            timeLabel.setText("Generated: ");
            timeLabel.setBold(true);
            XWPFRun timeValue = timePara.createRun();
            timeValue.setText(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))); 

            XWPFParagraph adminPara = document.createParagraph();
            XWPFRun adminLabel = adminPara.createRun();
            adminLabel.setText("Generated by: ");
            adminLabel.setBold(true);
            XWPFRun adminValue = adminPara.createRun();
            adminValue.setText(adminUsername);
            adminValue.addBreak();
            adminValue.addBreak();

            XWPFParagraph separator = document.createParagraph();
            XWPFRun separatorRun = separator.createRun();
            separatorRun.setText("─".repeat(80));
            separatorRun.addBreak();

            XWPFParagraph contentPara = document.createParagraph();
            XWPFRun contentRun = contentPara.createRun();
            contentRun.setText(content);
            contentRun.addBreak();
            contentRun.addBreak();

            XWPFParagraph footerSep = document.createParagraph();
            XWPFRun footerSepRun = footerSep.createRun();
            footerSepRun.setText("─".repeat(80));

            XWPFParagraph footer = document.createParagraph();
            XWPFRun footerRun = footer.createRun();
            footerRun.setText("Generated by TutoEasy Report System");
            footerRun.setItalic(true);
            footerRun.setFontSize(10);

            document.write(out);
        }

        return filePath.toAbsolutePath().toString();
    }
}
