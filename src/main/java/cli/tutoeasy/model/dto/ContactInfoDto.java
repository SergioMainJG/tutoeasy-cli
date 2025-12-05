package cli.tutoeasy.model.dto;

/**
 * Data transfer object for contact information.
 * Contains basic user information for messaging purposes.
 * 
 * @param userId     The ID of the user
 * @param username   The username of the user
 * @param email      The email address of the user
 * @param careerName The name of the user's career (can be null)
 * @param role       The role of the user (admin, tutor, student)
 */
public record ContactInfoDto(
    int userId,
    String username,
    String email,
    String careerName,
    String role) {
  /**
   * Formats the contact information for display
   */
  public String toFormattedString() {
    StringBuilder sb = new StringBuilder();
    sb.append("User Information:\n");
    sb.append("  ID: ").append(userId).append("\n");
    sb.append("  Name: ").append(username).append("\n");
    sb.append("  Email: ").append(email).append("\n");
    sb.append("  Role: ").append(role).append("\n");
    sb.append("  Career: ").append(careerName != null ? careerName : "Not specified");
    return sb.toString();
  }
}