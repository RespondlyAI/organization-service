package in.respondlyai.org.repository;

import in.respondlyai.org.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    Optional<Organization> findByName(String name);

    // To check if the name is taken before creating a new org.
    boolean existsByName(String name);

    // Useful for a dashboard showing the owner all their created orgs.
    List<Organization> findByCreatedByUserId(String userId);
}