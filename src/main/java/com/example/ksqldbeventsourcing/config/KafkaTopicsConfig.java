package com.example.ksqldbeventsourcing.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

  public static final String TOPIC_ORDER_COMMANDS_AND_EVENTS = "order-commands-and-events";
  public static final String TOPIC_ORDER_AGGREGATES = "order-aggregates";
  public static final String TOPIC_ORDER_INTEGRATION_EVENTS = "order-integration-events";

  @Bean
  public NewTopic orderCommandsAndEventsTopic() {
    return TopicBuilder.name(TOPIC_ORDER_COMMANDS_AND_EVENTS).partitions(10).replicas(1).build();
  }

  @Bean
  public NewTopic orderAggregates() {
    return TopicBuilder.name(TOPIC_ORDER_AGGREGATES).partitions(10).replicas(1).build();
  }

  @Bean
  public NewTopic orderIntegrationEvents() {
    return TopicBuilder.name(TOPIC_ORDER_INTEGRATION_EVENTS).partitions(10).replicas(1).build();
  }
}
