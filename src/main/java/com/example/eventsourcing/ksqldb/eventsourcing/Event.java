package com.example.eventsourcing.ksqldb.eventsourcing;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class Event {

  protected UUID aggregateId;
  protected int version;
  protected Instant createdDate = Instant.now();

  public Event(UUID aggregateId, int version) {
    this.aggregateId = aggregateId;
    this.version = version;
  }

  public String getEventType() {
    return this.getClass().getSimpleName();
  }
}
