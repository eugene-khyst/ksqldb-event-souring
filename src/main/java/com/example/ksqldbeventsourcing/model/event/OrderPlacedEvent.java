package com.example.ksqldbeventsourcing.model.event;

import com.example.ksqldbeventsourcing.model.domain.Waypoint;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OrderPlacedEvent extends Event {

  UUID riderId;
  BigDecimal price;
  List<Waypoint> route;

  @Builder
  @JsonCreator
  public OrderPlacedEvent(
      UUID aggregateId, int version, UUID riderId, BigDecimal price, List<Waypoint> route) {
    super(aggregateId, version);
    this.riderId = riderId;
    this.price = price;
    this.route = List.copyOf(route);
  }
}
