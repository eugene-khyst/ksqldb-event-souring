package com.example.eventsourcing.ksqldb.domain.writemodel.event;

import com.example.eventsourcing.ksqldb.eventsourcing.Event;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OrderCompletedEvent extends Event {

  @JsonCreator
  public OrderCompletedEvent(UUID aggregateId, int version) {
    super(aggregateId, version);
  }
}
