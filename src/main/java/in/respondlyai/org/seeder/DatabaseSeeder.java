package in.respondlyai.org.seeder;

import in.respondlyai.org.entity.Industry;
import in.respondlyai.org.entity.OrganizationType;
import in.respondlyai.org.entity.SubscriptionPlan;
import in.respondlyai.org.repository.IndustryRepository;
import in.respondlyai.org.repository.OrganizationTypeRepository;
import in.respondlyai.org.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final IndustryRepository industryRepository;
    private final OrganizationTypeRepository orgTypeRepository;
    private final SubscriptionPlanRepository planRepository;

    @Override
    public void run(String... args) {
        log.info("Checking database for seed data...");
        seedIndustries();
        seedOrganizationTypes();
        seedSubscriptionPlans();
        log.info("Database seeding check complete.");
    }

    private void seedIndustries() {
        if (industryRepository.count() == 0) {
            log.info("Seeding Industries...");
            industryRepository.saveAll(List.of(
                    Industry.builder().name("Software").build(),
                    Industry.builder().name("Healthcare").build(),
                    Industry.builder().name("Finance").build(),
                    Industry.builder().name("Marketing").build()
            ));
        }
    }
    private void seedOrganizationTypes() {
        if (orgTypeRepository.count() == 0) {
            log.info("Seeding Organization Types...");
            orgTypeRepository.saveAll(List.of(
                    OrganizationType.builder().name("Startup").build(),
                    OrganizationType.builder().name("Medium Scale").build(),
                    OrganizationType.builder().name("Enterprise").build()
            ));
        }
    }

    private void seedSubscriptionPlans() {
        if (planRepository.count() == 0) {
            log.info("Seeding Subscription Plans...");
            planRepository.saveAll(List.of(
                    SubscriptionPlan.builder().name("Free Tier").maxAgents(1).maxAiCalls(100).build(),
                    SubscriptionPlan.builder().name("Pro").maxAgents(5).maxAiCalls(5000).build(),
                    SubscriptionPlan.builder().name("Enterprise").maxAgents(999).maxAiCalls(999999).build()
            ));
        }
    }
}