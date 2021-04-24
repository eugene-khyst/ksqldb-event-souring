package com.example.ksqldbeventsourcing.service;

import com.example.ksqldbeventsourcing.domain.readmodel.Order;
import com.example.ksqldbeventsourcing.repository.OrderRepository;
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
