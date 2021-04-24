package com.example.ksqldbeventsourcing.domain.writemodel.command;

import com.example.ksqldbeventsourcing.eventsourcing.Command;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CompleteOrderCommand extends Command {

  @JsonCreator
  public CompleteOrderCommand(UUID aggregateId, int originalVersion) {
    super(aggregateId, originalVersion);
  }
}
