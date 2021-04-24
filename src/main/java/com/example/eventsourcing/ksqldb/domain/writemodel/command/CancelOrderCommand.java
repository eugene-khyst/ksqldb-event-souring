package com.example.eventsourcing.ksqldb.domain.writemodel.command;

import com.example.eventsourcing.ksqldb.eventsourcing.Command;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CancelOrderCommand extends Command {

  @JsonCreator
  public CancelOrderCommand(UUID aggregateId, int originalVersion) {
    super(aggregateId, originalVersion);
  }
}
