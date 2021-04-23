package com.example.ksqldbeventsourcing.mapper;

import com.example.ksqldbeventsourcing.integration.OrderIntegrationEvent;
import com.example.ksqldbeventsourcing.model.domain.ErrorMessage;
import com.example.ksqldbeventsourcing.model.domain.Order;
import com.example.ksqldbeventsourcing.model.domain.Waypoint;
import com.example.ksqldbeventsourcing.model.event.Event;
import java.time.Instant;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

  @Mapping(source = "aggregateId", target = "id")
  @Mapping(source = "baseVersion", target = "version")
  com.example.ksqldbeventsourcing.model.read.Order toReadModel(Order order);

  @Mapping(source = "order.aggregateId", target = "orderId")
  @Mapping(source = "event.eventType", target = "eventType")
  @Mapping(source = "event.createdDate", target = "eventTimestamp")
  @Mapping(source = "order.baseVersion", target = "version")
  @Mapping(source = "order.riderId", target = "riderId")
  @Mapping(source = "order.price", target = "price")
  @Mapping(source = "order.route", target = "route")
  @Mapping(source = "order.driverId", target = "driverId")
  OrderIntegrationEvent toIntegrationEvent(Event event, Order order);

  com.example.ksqldbeventsourcing.model.read.Waypoint toReadModel(Waypoint value);

  @Mapping(source = "commandType", target = "command")
  com.example.ksqldbeventsourcing.model.read.ErrorMessage toReadModel(ErrorMessage value);

  com.example.ksqldbeventsourcing.integration.Waypoint toIntegrationEvent(Waypoint value);

  default long toEpochMilli(Instant instant) {
    return Optional.ofNullable(instant).map(Instant::toEpochMilli).orElse(0L);
  }
}
