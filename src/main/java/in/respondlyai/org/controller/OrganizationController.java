package in.respondlyai.org.controller;

import in.respondlyai.org.dto.request.CreateOrganizationRequest;
import in.respondlyai.org.dto.response.OrganizationResponse;
import in.respondlyai.org.dto.response.PageResponse;
import in.respondlyai.org.entity.Organization;
import in.respondlyai.org.exception.ApiErrorResponse;
import in.respondlyai.org.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/org")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Organization Management", description = "Endpoints for creating and retrieving organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping("/create")
    @Operation(summary = "Create a new organization", description = "Creates a new organization and queues invitations for users. Requires a valid JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Organization created successfully", content = @Content(schema = @Schema(implementation = OrganizationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error or malformed JSON", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User lacks permissions", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflict - Organization name already exists", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<OrganizationResponse> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request) {

        log.info("Received request to create organization: {}", request.name());

        // Extract the userId that our JwtAuthenticationFilter securely verified
        String ownerUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Organization savedOrg = organizationService.createOrganization(request, ownerUserId);

        OrganizationResponse response = new OrganizationResponse(
                savedOrg.getId(),
                savedOrg.getName(),
                savedOrg.getIndustry().getName(),
                savedOrg.getOrganizationType().getName(),
                savedOrg.getStatus().name()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all organizations", description = "Returns a paginated list of all organizations. Requires a valid JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<OrganizationResponse>> getOrganizations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Received request to fetch organizations");

        Page<Organization> orgPage = organizationService.getAllOrganizations(page, size);

        List<OrganizationResponse> dtoList = orgPage.getContent().stream()
                .map(org -> new OrganizationResponse(
                        org.getId(),
                        org.getName(),
                        org.getIndustry().getName(),
                        org.getOrganizationType().getName(),
                        org.getStatus().name()
                ))
                .toList();

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

    @GetMapping("/{id}")
    @Operation(summary = "Get organization by ID", description = "Returns a single organization by its UUID. Requires a valid JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved organization", content = @Content(schema = @Schema(implementation = OrganizationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Organization ID does not exist", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<OrganizationResponse> getOrganizationById(@PathVariable UUID id) {

        log.info("Received request to fetch organization by ID: {}", id);

        Organization org = organizationService.getOrganizationById(id);

        OrganizationResponse response = new OrganizationResponse(
                org.getId(),
                org.getName(),
                org.getIndustry().getName(),
                org.getOrganizationType().getName(),
                org.getStatus().name()
        );

        return ResponseEntity.ok(response);
    }
}
