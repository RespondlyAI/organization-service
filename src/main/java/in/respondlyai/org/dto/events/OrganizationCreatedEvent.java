package in.respondlyai.org.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationCreatedEvent {
    private UUID organizationId;
    private UUID ownerUserId;
    private List<InvitedUser> invitedUsers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvitedUser {
        private String email;
        private String role;
    }
}
