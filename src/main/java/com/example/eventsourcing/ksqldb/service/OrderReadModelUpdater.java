package com.example.eventsourcing.ksqldb.service;

import com.example.eventsourcing.ksqldb.repository.OrderRepository;
import com.example.eventsourcing.ksqldb.domain.readmodel.Order;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderReadModelUpdater {

  private final OrderRepository repository;

  public void saveOrUpdate(Order order) {
    Objects.requireNonNull(order);
    log.debug("Updating read model for order {}", order);
    repository.save(order);
  }
}
