package com.example.eventsourcing.ksqldb.service;

import com.example.eventsourcing.ksqldb.eventsourcing.ErrorEvent;
import com.example.eventsourcing.ksqldb.eventsourcing.Event;
import com.example.eventsourcing.ksqldb.mapper.OrderMapper;
import com.example.eventsourcing.ksqldb.domain.writemodel.Order;
import java.util.Objects;
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

  public void process(Event event, Order order) {
    Objects.requireNonNull(event);
    Objects.requireNonNull(order);
    log.debug("Processing event {} for order {}", event, order);
    readModelUpdater.saveOrUpdate(mapper.toReadModel(order));
    if (!(event instanceof ErrorEvent)) {
      integrationEventSender.send(mapper.toIntegrationEvent(event, order));
    }
  }
}
