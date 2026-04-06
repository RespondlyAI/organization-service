package in.respondlyai.org.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "organization_types")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrganizationType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;
}
