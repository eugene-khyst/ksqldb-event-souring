package com.example.ksqldbeventsourcing.service;

import com.example.ksqldbeventsourcing.model.domain.Order;
import com.example.ksqldbeventsourcing.repository.KsqlRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventStore {

  private final KsqlRepository ksqlRepository;
  private final OrderJsonReader jsonReader;

  public Optional<Order> readOrder(UUID orderId) {
    return ksqlRepository.findAggregate(
        "SELECT * FROM ORDER_AGGREGATES WHERE ORDER_ID='" + orderId + "';", jsonReader::readOrder);
  }
}
