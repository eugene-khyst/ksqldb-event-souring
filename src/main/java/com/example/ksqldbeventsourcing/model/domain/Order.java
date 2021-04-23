package com.example.ksqldbeventsourcing.model.domain;

import com.example.ksqldbeventsourcing.model.command.AcceptOrderCommand;
import com.example.ksqldbeventsourcing.model.command.CancelOrderCommand;
import com.example.ksqldbeventsourcing.model.command.CompleteOrderCommand;
import com.example.ksqldbeventsourcing.model.command.PlaceOrderCommand;
import com.example.ksqldbeventsourcing.model.event.Event;
import com.example.ksqldbeventsourcing.model.event.OrderAcceptedEvent;
import com.example.ksqldbeventsourcing.model.event.OrderCancelledEvent;
import com.example.ksqldbeventsourcing.model.event.OrderCompletedEvent;
import com.example.ksqldbeventsourcing.model.event.OrderPlacedEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Order extends Aggregate {

  private OrderStatus status;
  private UUID riderId;
  private BigDecimal price;
  private List<Waypoint> route;
  private UUID driverId;
  private Instant placedDate;
  private Instant acceptedDate;
  private Instant completedDate;
  private Instant cancelledDate;

  public Order(UUID orderId, List<Event> events) {
    super(orderId, events);
  }

  public void process(PlaceOrderCommand command) {
    if (status != null) {
      error(command, String.format("Order in status %s can't be placed", status));
      return;
    }
    applyChange(
        new OrderPlacedEvent(
            aggregateId,
            getNextVersion(),
            command.getRiderId(),
            command.getPrice(),
            command.getRoute()));
  }

  public void process(AcceptOrderCommand command) {
    if (status == OrderStatus.CANCELLED) {
      error(command, String.format("Order in status %s can't be accepted", status));
      return;
    }
    applyChange(new OrderAcceptedEvent(aggregateId, getNextVersion(), command.getDriverId()));
  }

  public void process(CompleteOrderCommand command) {
    if (status != OrderStatus.ACCEPTED) {
      error(command, String.format("Order in status %s can't be completed", status));
      return;
    }
    applyChange(new OrderCompletedEvent(aggregateId, getNextVersion()));
  }

  public void process(CancelOrderCommand command) {
    if (!EnumSet.of(OrderStatus.PLACED, OrderStatus.ACCEPTED).contains(status)) {
      error(command, String.format("Order in status %s can't be cancelled", status));
      return;
    }
    applyChange(new OrderCancelledEvent(aggregateId, getNextVersion()));
  }

  public void apply(OrderPlacedEvent event) {
    this.status = OrderStatus.PLACED;
    this.riderId = event.getRiderId();
    this.price = event.getPrice();
    this.route = event.getRoute();
    this.placedDate = event.getCreatedDate();
  }

  public void apply(OrderAcceptedEvent event) {
    this.status = OrderStatus.ACCEPTED;
    this.driverId = event.getDriverId();
    this.acceptedDate = event.getCreatedDate();
  }

  public void apply(OrderCompletedEvent event) {
    this.status = OrderStatus.COMPLETED;
    this.completedDate = event.getCreatedDate();
  }

  public void apply(OrderCancelledEvent event) {
    this.status = OrderStatus.CANCELLED;
    this.cancelledDate = event.getCreatedDate();
  }
}
