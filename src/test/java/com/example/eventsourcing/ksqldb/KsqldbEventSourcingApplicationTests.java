package com.example.eventsourcing.ksqldb;

import com.example.eventsourcing.ksqldb.config.KafkaTopicsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(
    bootstrapServersProperty = "spring.kafka.bootstrap-servers",
    topics = {
      KafkaTopicsConfig.TOPIC_ORDER_COMMANDS_AND_EVENTS,
      KafkaTopicsConfig.TOPIC_ORDER_AGGREGATES,
      KafkaTopicsConfig.TOPIC_ORDER_INTEGRATION_EVENTS
    })
class KsqldbEventSourcingApplicationTests {

  @Test
  void contextLoads() {}
}
