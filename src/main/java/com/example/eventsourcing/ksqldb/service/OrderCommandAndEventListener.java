package com.example.eventsourcing.ksqldb.service;

import com.example.eventsourcing.ksqldb.config.KafkaTopicsConfig;
import com.example.eventsourcing.ksqldb.eventsourcing.Event;
import com.example.eventsourcing.ksqldb.domain.writemodel.Order;
import com.example.eventsourcing.ksqldb.service.EventJsonSerde.EventData;
import java.util.List;
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

  private final EventJsonSerde jsonSerde;
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
    EventData eventData = jsonSerde.deserialize(json);
    List<Event> events = eventData.readEvents();
    Order order = new Order(orderId, events);
    if (eventData.isLatestIsCommand()) {
      commandHandler.process(eventData.readLatestCommands(), order);
    } else {
      Event latestEvent = events.get(events.size() - 1);
      eventHandler.process(latestEvent, order);
    }
  }
}
