package com.example.ksqldbeventsourcing.model.command;

import java.util.UUID;
import lombok.Data;

@Data
public abstract class Command {

  protected final UUID aggregateId;
  protected final int originalVersion;

  public String getCommandType() {
    return this.getClass().getSimpleName();
  }
}
