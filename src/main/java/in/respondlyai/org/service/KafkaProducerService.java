package in.respondlyai.org.service;

import in.respondlyai.org.dto..events.OrganizationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "org-events";

    public void sendOrganizationCreatedEvent(OrganizationCreatedEvent event) {
        log.info("Publishing OrganizationCreatedEvent to Kafka for Org ID: {}", event.getOrganizationId());
        kafkaTemplate.send(TOPIC, event.getOrganizationId().toString(), event);
    }
}
