package in.respondlyai.org.dto.response;

import java.util.UUID;

public record OrganizationResponse(
        UUID id,
        String name,
        String industry,
        String type,
        String status
) {}