package com.example.ksqldbeventsourcing.service;

import com.example.ksqldbeventsourcing.config.KafkaTopicsConfig;
import com.example.ksqldbeventsourcing.model.command.Command;
import com.example.ksqldbeventsourcing.model.domain.Order;
import com.example.ksqldbeventsourcing.model.event.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCommandAndEventListener {

  private final ObjectMapper objectMapper;
  private final OrderJsonReader jsonReader;
  private final OrderCommandHandler commandHandler;
  private final OrderEventHandler eventHandler;

  @KafkaListener(topics = KafkaTopicsConfig.TOPIC_ORDER_AGGREGATES, concurrency = "10")
  public void listen(
      @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) UUID orderId,
      @Payload(required = false) String json)
      throws JsonProcessingException {
    log.debug("Consumed record for order {}: {}", orderId, json);
    if (json == null) {
      log.warn("Skipping record for order {} with empty payload", orderId);
      return;
    }
    JsonNode jsonNode = objectMapper.readTree(json);
    Order order = jsonReader.readOrder(orderId, jsonNode);
    String type = jsonNode.get("LATEST_TYPE").asText();
    switch (type) {
      case "command":
        Command command = jsonReader.readCommand(jsonNode);
        log.info("Handling command {} for order {}", command.getCommandType(), orderId);
        commandHandler.handle(command, order);
        break;
      case "event":
        Event event = jsonReader.readEvent(jsonNode);
        log.info("Handling event {} for order {}", event.getEventType(), orderId);
        eventHandler.handle(event, order);
        break;
      default:
        log.warn("Unsupported type {} for order {}", type, orderId);
        break;
    }
  }
}
