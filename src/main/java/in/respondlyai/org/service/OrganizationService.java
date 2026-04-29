package in.respondlyai.org.service;

import in.respondlyai.org.dto.events.OrganizationCreatedEvent;
import in.respondlyai.org.dto.request.CreateOrganizationRequest;
import in.respondlyai.org.entity.*;
import in.respondlyai.org.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final IndustryRepository industryRepository;
    private final OrganizationTypeRepository organizationTypeRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    // Injecting the Kafka Producer
    private final KafkaProducerService kafkaProducerService;

    @Transactional(readOnly = true)
    public Organization getOrganizationById(UUID id) {
        log.info("Fetching organization with ID: {}", id);

        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Organization not found with ID: {}", id);
                    return new EntityNotFoundException("Organization not found with ID: " + id);
                });

        log.debug("Successfully found organization: {}", organization.getName());
        return organization;
    }

    @Transactional
    public Organization createOrganization(CreateOrganizationRequest request, String ownerUserId) {
        log.info("Attempting to create organization: {} for user: {}", request.name(), ownerUserId);

        // Fail Fast: Check if the organization name is already taken
        if (organizationRepository.existsByName(request.name())) {
            log.warn("Failed to create organization: Name '{}' is already taken", request.name());
            throw new IllegalArgumentException("Organization name is already taken.");
        }

        // Fetch the related Reference Data from the DB using the IDs from the DTO
        Industry industry = industryRepository.findById(request.industryId())
                .orElseThrow(() -> {
                    log.warn("Invalid Industry ID: {}", request.industryId());
                    return new IllegalArgumentException("Invalid Industry ID");
                });
        log.debug("Industry found: {}", industry.getName());

        OrganizationType orgType = organizationTypeRepository.findById(request.organizationTypeId())
                .orElseThrow(() -> {
                    log.warn("Invalid Organization Type ID: {}", request.organizationTypeId());
                    return new IllegalArgumentException("Invalid Organization Type ID");
                });
        log.debug("Organization type found: {}", orgType.getName());

        SubscriptionPlan defaultPlan = subscriptionPlanRepository.findAll().stream().findFirst()
                .orElseThrow(() -> {
                    log.error("CRITICAL: No subscription plans found in DB!");
                    return new IllegalStateException("Critical: No subscription plans found in DB!");
                });
        log.debug("Using default subscription plan: {}", defaultPlan.getName());

        // Build the Organization Entity
        Organization newOrganization = Organization.builder()
                .name(request.name())
                .description(request.description())
                .createdByUserId(ownerUserId)
                .subscriptionStatus(SubscriptionStatus.trialing)
                .status(OrgStatus.active)
                .industry(industry)
                .organizationType(orgType)
                .subscriptionPlan(defaultPlan)
                .build();

        // Save to PostgreSQL
        Organization savedOrg = organizationRepository.save(newOrganization);
        log.info("Organization created successfully: ID={}, Name={}", savedOrg.getId(), savedOrg.getName());

        // Map invited users (if any) to the Event DTO payload
        List<OrganizationCreatedEvent.InvitedUser> invitedUsers = Collections.emptyList();
        if (request.invitedUsers() != null && !request.invitedUsers().isEmpty()) {
            invitedUsers = request.invitedUsers().stream()
                    .map(user -> new OrganizationCreatedEvent.InvitedUser(user.email(), user.role()))
                    .toList();
            log.info("Including {} invited users in the Kafka event for Org: {}", invitedUsers.size(), savedOrg.getName());
        }

        // Build the Kafka Event
        // Note: parsing ownerUserId to UUID assuming your DTO expects a UUID type for ownerUserId
        OrganizationCreatedEvent event = OrganizationCreatedEvent.builder()
                .organizationId(savedOrg.getId())
                .ownerUserId(UUID.fromString(ownerUserId))
                .invitedUsers(invitedUsers)
                .build();

        // Fire the event to Kafka
        kafkaProducerService.sendOrganizationCreatedEvent(event);

        return savedOrg;
    }

    @Transactional(readOnly = true)
    public Page<Organization> getAllOrganizations(int page, int size) {
        log.info("Fetching organizations page {} with size {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Organization> orgPage = organizationRepository.findAll(pageable);
        log.info("Retrieved {} organizations for page {}", orgPage.getNumberOfElements(), page);

        return orgPage;
    }
}
