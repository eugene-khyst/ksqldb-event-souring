package com.example.ksqldbeventsourcing.integration;

import com.example.ksqldbeventsourcing.model.domain.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderIntegrationEvent {

  @JsonProperty("ORDER_ID")
  UUID orderId;

  @JsonProperty("EVENT_TYPE")
  String eventType;

  @JsonProperty("EVENT_TIMESTAMP")
  long eventTimestamp;

  @JsonProperty("VERSION")
  int version;

  @JsonProperty("STATUS")
  OrderStatus status;

  @JsonProperty("RIDER_ID")
  UUID riderId;

  @JsonProperty("PRICE")
  BigDecimal price;

  @JsonProperty("ROUTE")
  List<Waypoint> route;

  @JsonProperty("DRIVER_ID")
  UUID driverId;
}
