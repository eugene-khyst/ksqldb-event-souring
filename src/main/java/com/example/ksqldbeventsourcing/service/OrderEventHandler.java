package com.example.ksqldbeventsourcing.service;

import com.example.ksqldbeventsourcing.mapper.OrderMapper;
import com.example.ksqldbeventsourcing.model.domain.Order;
import com.example.ksqldbeventsourcing.model.event.Event;
import com.example.ksqldbeventsourcing.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventHandler {

  private final OrderMapper mapper;
  private final OrderRepository orderRepository;
  private final OrderIntegrationEventSender integrationEventSender;

  public void handle(Event event, Order order) {
    orderRepository.save(mapper.toReadModel(order));
    integrationEventSender.send(mapper.toIntegrationEvent(event, order));
  }
}
