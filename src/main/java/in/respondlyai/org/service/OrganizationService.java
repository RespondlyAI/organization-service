package in.respondlyai.org.service;

import in.respondlyai.org.dto.request.CreateOrganizationRequest;
import in.respondlyai.org.entity.*;
import in.respondlyai.org.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final IndustryRepository industryRepository;
    private final OrganizationTypeRepository organizationTypeRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Transactional
    public Organization createOrganization(CreateOrganizationRequest request, String ownerUserId) {
        log.info("Attempting to create organization: {}", request.name());

        // Fail Fast: Check if the organization name is already taken
        if (organizationRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Organization name is already taken.");
        }

        // Fetch the related Reference Data from the DB using the IDs from the DTO
        // If the frontend sends a fake/bad ID, .orElseThrow() instantly stops the process.
        Industry industry = industryRepository.findById(request.industryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Industry ID"));

        OrganizationType orgType = organizationTypeRepository.findById(request.organizationTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Organization Type ID"));

        SubscriptionPlan defaultPlan = subscriptionPlanRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Critical: No subscription plans found in DB!"));

        // Build the Organization Entity
        Organization newOrganization = Organization.builder()
                .name(request.name())
                .description(request.description())
                .createdByUserId(ownerUserId) // TODO: Eventually, this comes from the JWT Auth token
                .subscriptionStatus(SubscriptionStatus.trialing)
                .status(OrgStatus.active)
                .industry(industry)
                .organizationType(orgType)
                .subscriptionPlan(defaultPlan)
                .build();

        // Save to PostgreSQL
        Organization savedOrg = organizationRepository.save(newOrganization);
        log.info("Successfully saved Organization to DB with ID: {}", savedOrg.getId());

        // TODO: Fire Event to AWS API Gateway for the Invited Users
        if (request.invitedUsers() != null && !request.invitedUsers().isEmpty()) {
            log.info("Queuing invitations for {} users to Auth Service...", request.invitedUsers().size());
            // publishEventToAws(savedOrg.getId(), request.invitedUsers());
        }

        return savedOrg;
    }
}