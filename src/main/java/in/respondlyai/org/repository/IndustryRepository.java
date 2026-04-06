package in.respondlyai.org.repository;

import in.respondlyai.org.entity.Industry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IndustryRepository extends JpaRepository<Industry, UUID> {
}