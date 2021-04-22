package com.example.ksqldbeventsourcing.service;

import com.example.ksqldbeventsourcing.config.KafkaTopicsConfig;
import com.example.ksqldbeventsourcing.model.command.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCommandSender implements CommandSender {

  private final ObjectMapper objectMapper;
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Override
  public void send(Command command) {
    kafkaTemplate.send(
        KafkaTopicsConfig.TOPIC_ORDER_COMMANDS_AND_EVENTS,
        command.getAggregateId().toString(),
        toJson(command));
  }

  private String toJson(Command command) {
    try {
      ObjectNode on = objectMapper.createObjectNode();
      on.put("order_id", command.getAggregateId().toString());
      on.put("type", "command");
      on.put("sub_type", command.getClass().getSimpleName());
      on.put("details", objectMapper.writeValueAsString(command));
      return on.toString();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
