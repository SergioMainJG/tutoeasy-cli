package cli.tutoeasy.service;

import cli.tutoeasy.model.dto.ContactInfoDto;
import cli.tutoeasy.model.entities.Tutoring;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.repository.ContactRepository;
import cli.tutoeasy.repository.TutoringRepository;

/**
 * Service for handling contact-related operations.
 * Provides methods to retrieve user information for messaging purposes.
 */
public class ContactService {

  private final ContactRepository contactRepository;
  private final TutoringRepository tutoringRepository;

  public ContactService(ContactRepository contactRepository, TutoringRepository tutoringRepository) {
    this.contactRepository = contactRepository;
    this.tutoringRepository = tutoringRepository;
  }

  /**
   * Gets contact information for a user by their username
   * 
   * @param username The username to search for
   * @return ContactInfoDto with user information, or null if not found
   */
  public ContactInfoDto getContactInfoByUsername(String username) {
    User user = contactRepository.findByUsername(username);

    if (user == null) {
      return null;
    }

    return mapUserToContactInfo(user);
  }

  /**
   * Gets contact information from a tutoring session.
   * Returns tutor info if requestor is student, or student info if requestor is
   * tutor.
   * 
   * @param tutoringId  The ID of the tutoring session
   * @param requestorId The ID of the user requesting the information
   * @return ContactInfoDto with the appropriate user information
   * @throws IllegalArgumentException if tutoring not found or requestor not part
   *                                  of the tutoring
   */
  public ContactInfoDto getContactInfoFromTutoring(int tutoringId, int requestorId) {
    Tutoring tutoring = tutoringRepository.findById(tutoringId);

    if (tutoring == null) {
      throw new IllegalArgumentException("Tutoring session not found with ID: " + tutoringId);
    }

    User contactUser;

    if (tutoring.getStudent().getId() == requestorId) {
      contactUser = contactRepository.findUserWithCareer(tutoring.getTutor().getId());
    } else if (tutoring.getTutor().getId() == requestorId) {
      contactUser = contactRepository.findUserWithCareer(tutoring.getStudent().getId());
    } else {
      throw new IllegalArgumentException(
          "You are not part of this tutoring session (ID: " + tutoringId + ")");
    }

    if (contactUser == null) {
      throw new IllegalArgumentException("Contact user not found in tutoring session");
    }

    return mapUserToContactInfo(contactUser);
  }

  /**
   * Maps a User entity to ContactInfoDto
   */
  private ContactInfoDto mapUserToContactInfo(User user) {
    String careerName = null;
    if (user.getCareer() != null) {
      careerName = user.getCareer().getName();
    }

    return new ContactInfoDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        careerName,
        user.getRol().name());
  }

  /**
   * Validates if a user exists by username
   * 
   * @param username The username to validate
   * @return true if user exists, false otherwise
   */
  public boolean userExists(String username) {
    return contactRepository.findByUsername(username) != null;
  }
}