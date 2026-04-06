package in.respondlyai.org.repository;

import in.respondlyai.org.entity.OrganizationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrganizationTypeRepository extends JpaRepository<OrganizationType, UUID> {
}