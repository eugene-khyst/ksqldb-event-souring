package com.example.ksqldbeventsourcing.service;

import com.example.ksqldbeventsourcing.eventsourcing.Command;
import com.example.ksqldbeventsourcing.domain.writemodel.Order;
import com.example.ksqldbeventsourcing.eventsourcing.Event;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCommandHandler {

  private final OrderEventPublisher eventPublisher;

  public void process(List<Command> commands, Order order) {
    Objects.requireNonNull(commands);
    Objects.requireNonNull(order);
    if (commands.isEmpty()) {
      throw new IllegalArgumentException(
          String.format("Latest commands are empty for order %s", order.getAggregateId()));
    }
    Command command = commands.get(0);
    log.debug("Processing {} command for order {}", command, order);
    if (checkVersionMatches(command, commands, order)) {
      order.process(command);
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
