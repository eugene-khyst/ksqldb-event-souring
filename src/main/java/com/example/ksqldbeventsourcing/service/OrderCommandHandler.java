package com.example.ksqldbeventsourcing.service;

import com.example.ksqldbeventsourcing.model.command.Command;
import com.example.ksqldbeventsourcing.model.domain.Order;
import com.example.ksqldbeventsourcing.model.event.Event;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCommandHandler {

  private final OrderEventPublisher eventPublisher;

  public void handle(Command command, Order order) {
    if (checkVersionMatches(order.getAggregateId(), command, order)) {
      log.debug("Processing {} command for order {}", command, order);
      order.process(command);
    }
    for (Event event : order.getChanges()) {
      log.debug("Publishing event {} for order {}", event, order);
      eventPublisher.publish(event);
    }
  }

  private boolean checkVersionMatches(UUID orderId, Command command, Order order) {
    if (order.getBaseVersion() != command.getOriginalVersion()) {
      order.error(
          String.format(
              "Order %s base version %s does not match command %s expected version %s",
              orderId,
              order.getBaseVersion(),
              command.getClass().getSimpleName(),
              command.getOriginalVersion()));
      return false;
    }
    return true;
  }
}
