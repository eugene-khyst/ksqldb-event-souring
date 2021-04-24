package com.example.ksqldbeventsourcing.eventsourcing;

import java.util.UUID;
import lombok.Data;

@Data
public abstract class Command {

  protected final UUID aggregateId;
  protected final int expectedVersion;

  public String getCommandType() {
    return this.getClass().getSimpleName();
  }
}
