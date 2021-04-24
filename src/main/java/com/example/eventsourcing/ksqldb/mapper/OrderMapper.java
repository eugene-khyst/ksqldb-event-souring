package com.example.eventsourcing.ksqldb.mapper;

import com.example.eventsourcing.ksqldb.domain.writemodel.Waypoint;
import com.example.eventsourcing.ksqldb.eventsourcing.ErrorMessage;
import com.example.eventsourcing.ksqldb.eventsourcing.Event;
import com.example.eventsourcing.ksqldb.domain.integration.OrderIntegrationEvent;
import com.example.eventsourcing.ksqldb.domain.writemodel.Order;
import java.time.Instant;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

  @Mapping(source = "aggregateId", target = "id")
  @Mapping(source = "baseVersion", target = "version")
  com.example.eventsourcing.ksqldb.domain.readmodel.Order toReadModel(Order order);

  @Mapping(source = "order.aggregateId", target = "orderId")
  @Mapping(source = "event.eventType", target = "eventType")
  @Mapping(source = "event.createdDate", target = "eventTimestamp")
  @Mapping(source = "order.baseVersion", target = "version")
  @Mapping(source = "order.riderId", target = "riderId")
  @Mapping(source = "order.price", target = "price")
  @Mapping(source = "order.route", target = "route")
  @Mapping(source = "order.driverId", target = "driverId")
  OrderIntegrationEvent toIntegrationEvent(Event event, Order order);

  com.example.eventsourcing.ksqldb.domain.readmodel.Waypoint toReadModel(Waypoint value);

  @Mapping(source = "commandType", target = "command")
  com.example.eventsourcing.ksqldb.domain.readmodel.ErrorMessage toReadModel(ErrorMessage value);

  com.example.eventsourcing.ksqldb.domain.integration.Waypoint toIntegrationEvent(Waypoint value);

  default long toEpochMilli(Instant instant) {
    return Optional.ofNullable(instant).map(Instant::toEpochMilli).orElse(0L);
  }
}
