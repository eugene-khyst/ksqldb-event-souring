package com.example.ksqldbeventsourcing.service;

import com.example.ksqldbeventsourcing.config.KafkaTopicsConfig;
import com.example.ksqldbeventsourcing.model.domain.Order;
import com.example.ksqldbeventsourcing.model.event.Event;
import com.example.ksqldbeventsourcing.service.OrderJsonMapper.OrderAggregate;
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

  private final OrderJsonMapper jsonMapper;
  private final OrderCommandHandler commandHandler;
  private final OrderEventHandler eventHandler;

  @KafkaListener(topics = KafkaTopicsConfig.TOPIC_ORDER_AGGREGATES, concurrency = "10")
  public void listen(
      @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) UUID orderId,
      @Payload(required = false) String json) {
    log.debug("Consumed record for order {}: {}", orderId, json);
    if (json == null) {
      log.warn("Skipping record for order {} with empty payload", orderId);
      return;
    }
    OrderAggregate orderAggregate = jsonMapper.readOrderAggregate(orderId, json);
    Order order = orderAggregate.readOrder();
    if (orderAggregate.isLatestIsCommand()) {
      commandHandler.process(orderAggregate.readLatestCommands(), order);
    } else {
      Event event = orderAggregate.readLatestEvent();
      eventHandler.process(event, order);
    }
  }
}
