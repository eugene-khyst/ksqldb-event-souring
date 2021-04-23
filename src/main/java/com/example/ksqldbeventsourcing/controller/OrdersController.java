package com.example.ksqldbeventsourcing.controller;

import com.example.ksqldbeventsourcing.model.command.AcceptOrderCommand;
import com.example.ksqldbeventsourcing.model.command.CancelOrderCommand;
import com.example.ksqldbeventsourcing.model.command.CompleteOrderCommand;
import com.example.ksqldbeventsourcing.model.command.PlaceOrderCommand;
import com.example.ksqldbeventsourcing.model.domain.OrderStatus;
import com.example.ksqldbeventsourcing.repository.OrderRepository;
import com.example.ksqldbeventsourcing.service.OrderCommandSender;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

  private final ObjectMapper objectMapper;
  private final OrderCommandSender commandSender;
  private final OrderRepository orderRepository;

  @PostMapping
  public ResponseEntity<JsonNode> placeOrder(@RequestBody JsonNode request) throws IOException {
    UUID orderId = UUID.randomUUID();
    commandSender.send(
        PlaceOrderCommand.builder()
            .aggregateId(orderId)
            .originalVersion(0)
            .riderId(UUID.fromString(request.get("riderId").asText()))
            .price(new BigDecimal(request.get("price").asText()))
            .route(
                objectMapper.readValue(
                    objectMapper.treeAsTokens(request.get("route")), new TypeReference<>() {}))
            .build());
    return ResponseEntity.accepted()
        .body(objectMapper.createObjectNode().put("orderId", orderId.toString()));
  }

  @PatchMapping("/{orderId}")
  public ResponseEntity<?> modifyOrder(@PathVariable UUID orderId, @RequestBody JsonNode request) {
    OrderStatus newStatus = OrderStatus.valueOf(request.get("status").asText());
    int version = request.get("version").asInt();
    switch (newStatus) {
      case ACCEPTED:
        commandSender.send(
            AcceptOrderCommand.builder()
                .aggregateId(orderId)
                .originalVersion(version)
                .driverId(UUID.fromString(request.get("driverId").asText()))
                .build());
        return ResponseEntity.accepted().build();
      case COMPLETED:
        commandSender.send(new CompleteOrderCommand(orderId, version));
        return ResponseEntity.accepted().build();
      case CANCELLED:
        commandSender.send(new CancelOrderCommand(orderId, version));
        return ResponseEntity.accepted().build();
      default:
        return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/")
  public ResponseEntity<List<com.example.ksqldbeventsourcing.model.read.Order>> getOrders() {
    return ResponseEntity.ok(orderRepository.findAll());
  }

  @GetMapping("/{orderId}")
  public ResponseEntity<com.example.ksqldbeventsourcing.model.read.Order> getOrder(
      @PathVariable UUID orderId) {
    return orderRepository
        .findById(orderId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
