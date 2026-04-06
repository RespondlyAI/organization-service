package in.respondlyai.org.repository;

import in.respondlyai.org.entity.OrganizationWebsite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizationWebsiteRepository extends JpaRepository<OrganizationWebsite, UUID> {

    List<OrganizationWebsite> findByOrganizationId(UUID organizationId);
}