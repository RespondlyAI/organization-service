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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import in.respondlyai.org.dto.response.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Slf4j
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponse> getOrganizationById(@PathVariable UUID id) {

        log.info("Received request to fetch organization by ID: {}", id);

        // Fetch the Entity
        Organization org = organizationService.getOrganizationById(id);

        // Map it to the Response DTO (Never return the Entity directly!)
        OrganizationResponse response = new OrganizationResponse(
                org.getId(),
                org.getName(),
                org.getIndustry().getName(),
                org.getOrganizationType().getName(),
                org.getStatus().name()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<OrganizationResponse> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request
    ) {


        log.info("Received request to create organization: {}", request.name());

        String ownerUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

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

    @GetMapping
    public ResponseEntity<PageResponse<OrganizationResponse>> getOrganizations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Received request to fetch organizations");

        // Get the Page of Entities from the Service
        Page<Organization> orgPage = organizationService.getAllOrganizations(page, size);

        // Map the Entities to our safe Response DTOs
        List<OrganizationResponse> dtoList = orgPage.getContent().stream()
                .map(org -> new OrganizationResponse(
                        org.getId(),
                        org.getName(),
                        org.getIndustry().getName(),
                        org.getOrganizationType().getName(),
                        org.getStatus().name()
                ))
                .toList();

        // Wrap it in our custom PageResponse
        PageResponse<OrganizationResponse> response = new PageResponse<>(
                dtoList,
                orgPage.getNumber(),
                orgPage.getSize(),
                orgPage.getTotalElements(),
                orgPage.getTotalPages(),
                orgPage.isLast()
        );

        return ResponseEntity.ok(response);
    }
}