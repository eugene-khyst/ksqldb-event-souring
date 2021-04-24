package com.example.ksqldbeventsourcing.domain.writemodel.event;

import com.example.ksqldbeventsourcing.eventsourcing.Event;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OrderCancelledEvent extends Event {

  @JsonCreator
  public OrderCancelledEvent(UUID aggregateId, int version) {
    super(aggregateId, version);
  }
}
