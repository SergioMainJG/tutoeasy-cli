package cli.tutoeasy.model.dto;

/**
 * Represents a response to an action.
 * This record is used to return a success status and a message after performing an action.
 *
 * @param success A boolean indicating whether the action was successful.
 * @param message A message providing details about the action's result.
 */
public record ActionResponseDto(
        boolean success,
        String message
) { }
