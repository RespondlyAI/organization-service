package in.respondlyai.org.controller;

import in.respondlyai.org.dto.request.CreateOrganizationRequest;
import in.respondlyai.org.dto.response.OrganizationResponse;
import in.respondlyai.org.entity.Organization;
import in.respondlyai.org.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Slf4j
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<OrganizationResponse> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request
    ) {

        log.info("Received request to create organization: {}", request.name());

        // Temporary hardcoded user ID.
        // TODO: we will extract this dynamically from the JWT Auth token later.
        String ownerUserId = "temp-owner-uuid-123";

        // Call to Service to handle the business logic of creating an organization
        Organization savedOrg = organizationService.createOrganization(request, ownerUserId);

        // Map the Database Entity to our safe Response DTO
        OrganizationResponse response = new OrganizationResponse(
                savedOrg.getId(),
                savedOrg.getName(),
                savedOrg.getIndustry().getName(),
                savedOrg.getOrganizationType().getName(),
                savedOrg.getStatus().name()
        );

        // Return HTTP 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}