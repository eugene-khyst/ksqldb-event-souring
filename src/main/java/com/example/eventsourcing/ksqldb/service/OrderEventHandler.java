package com.example.eventsourcing.ksqldb.service;

import com.example.eventsourcing.ksqldb.domain.writemodel.Order;
import com.example.eventsourcing.ksqldb.eventsourcing.ErrorEvent;
import com.example.eventsourcing.ksqldb.eventsourcing.Event;
import com.example.eventsourcing.ksqldb.mapper.OrderMapper;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventHandler {

  private final OrderMapper mapper;
  private final OrderReadModelUpdater readModelUpdater;
  private final OrderIntegrationEventSender integrationEventSender;

  public void process(UUID orderId, List<Event> events) {
    Objects.requireNonNull(orderId);
    Objects.requireNonNull(events);
    log.debug("Processing events {}", events);
    Order order = new Order(orderId, events);
    Event latestEvent = events.get(events.size() - 1);
    readModelUpdater.saveOrUpdate(mapper.toReadModel(order));
    if (!(latestEvent instanceof ErrorEvent)) {
      integrationEventSender.send(mapper.toIntegrationEvent(latestEvent, order));
    }
  }
}
