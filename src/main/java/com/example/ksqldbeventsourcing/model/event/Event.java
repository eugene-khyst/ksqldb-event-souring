package com.example.ksqldbeventsourcing.model.event;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public abstract class Event {

  protected final UUID aggregateId;
  protected final int version;
  protected final Instant createdDate = Instant.now();

  public String getEventType() {
    return this.getClass().getSimpleName();
  }
}
