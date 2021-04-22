package com.example.ksqldbeventsourcing.model.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OrderAcceptedEvent extends Event {

  UUID driverId;

  @JsonCreator
  public OrderAcceptedEvent(UUID aggregateId, int version, UUID driverId) {
    super(aggregateId, version);
    this.driverId = driverId;
  }
}