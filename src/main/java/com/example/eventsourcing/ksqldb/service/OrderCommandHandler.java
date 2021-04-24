package com.example.eventsourcing.ksqldb.service;

import com.example.eventsourcing.ksqldb.domain.writemodel.Order;
import com.example.eventsourcing.ksqldb.eventsourcing.Command;
import com.example.eventsourcing.ksqldb.eventsourcing.Event;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCommandHandler {

  private final OrderEventPublisher eventPublisher;

  public void process(UUID orderId, List<Command> commands, List<Event> events) {
    Objects.requireNonNull(orderId);
    Objects.requireNonNull(commands);
    Objects.requireNonNull(events);
    if (commands.isEmpty()) {
      throw new IllegalArgumentException(
          String.format("Latest commands are empty for order %s", orderId));
    }
    Order order = new Order(orderId, events);
    Command latestCommand = commands.get(0);
    log.debug("Processing {} command for order {}", latestCommand, events);
    if (checkVersionMatches(latestCommand, commands, order)) {
      order.process(latestCommand);
    }
    for (Event event : order.getChanges()) {
      eventPublisher.publish(event);
    }
  }

  private boolean checkVersionMatches(
      Command latestCommand, List<Command> unprocessedCommands, Order order) {
    if (order.getBaseVersion() != latestCommand.getExpectedVersion()) {
      order.error(
          latestCommand,
          String.format(
              "Actual version %s doesn't match expected version %s",
              order.getBaseVersion(), latestCommand.getExpectedVersion()));
      return false;
    }
    if (unprocessedCommands.size() > 1) {
      log.debug("Concurrent modification: {}", unprocessedCommands);
      order.error(latestCommand, "Concurrent modification");
      return false;
    }
    return true;
  }
}
