package com.example.ksqldbeventsourcing.service;

import com.example.ksqldbeventsourcing.config.KafkaTopicsConfig;
import com.example.ksqldbeventsourcing.model.event.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher implements EventPublisher {

  private final ObjectMapper objectMapper;
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Override
  public void publish(Event event) {
    kafkaTemplate.send(
        KafkaTopicsConfig.TOPIC_ORDER_COMMANDS_AND_EVENTS,
        event.getAggregateId().toString(),
        toJson(event));
  }

  private String toJson(Event event) {
    try {
      ObjectNode on = objectMapper.createObjectNode();
      on.put("order_id", event.getAggregateId().toString());
      on.put("type", "event");
      on.put("sub_type", event.getClass().getSimpleName());
      on.put("details", objectMapper.writeValueAsString(event));
      return on.toString();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
