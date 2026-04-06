package in.respondlyai.org.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record CreateOrganizationRequest(

        // Org Details
        @NotBlank(message = "Organization name is required")
        String name,

        String description,

        @NotNull(message = "Industry ID is required")
        UUID industryId,

        @NotNull(message = "Organization Type ID is required")
        UUID organizationTypeId,

        // Users to invite
        List<InvitedUserDto> invitedUsers
) {
    // Nested record for the user list
    public record InvitedUserDto(
            @NotBlank(message = "Email is required")
            String email,

            @NotBlank(message = "Role is required")
            String role // E.g., "ADMIN" or "MEMBER"
    ) {}
}