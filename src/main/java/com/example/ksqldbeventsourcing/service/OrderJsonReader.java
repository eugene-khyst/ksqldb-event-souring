package com.example.ksqldbeventsourcing.service;

import static java.util.stream.Collectors.toList;

import com.example.ksqldbeventsourcing.model.command.AcceptOrderCommand;
import com.example.ksqldbeventsourcing.model.command.CancelOrderCommand;
import com.example.ksqldbeventsourcing.model.command.Command;
import com.example.ksqldbeventsourcing.model.command.CompleteOrderCommand;
import com.example.ksqldbeventsourcing.model.command.PlaceOrderCommand;
import com.example.ksqldbeventsourcing.model.domain.Order;
import com.example.ksqldbeventsourcing.model.event.ErrorEvent;
import com.example.ksqldbeventsourcing.model.event.Event;
import com.example.ksqldbeventsourcing.model.event.OrderAcceptedEvent;
import com.example.ksqldbeventsourcing.model.event.OrderCancelledEvent;
import com.example.ksqldbeventsourcing.model.event.OrderCompletedEvent;
import com.example.ksqldbeventsourcing.model.event.OrderPlacedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.ksql.api.client.Row;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderJsonReader {

  private static final Map<String, Class<? extends Command>> COMMAND_TYPES =
      commandTypes(
          PlaceOrderCommand.class,
          AcceptOrderCommand.class,
          CompleteOrderCommand.class,
          CancelOrderCommand.class);

  private static final Map<String, Class<? extends Event>> EVENT_TYPES =
      eventTypes(
          OrderPlacedEvent.class,
          OrderAcceptedEvent.class,
          OrderCompletedEvent.class,
          OrderCancelledEvent.class,
          ErrorEvent.class);

  private final ObjectMapper objectMapper;

  @SafeVarargs
  private static Map<String, Class<? extends Command>> commandTypes(
      Class<? extends Command>... types) {
    return Arrays.stream(types)
        .collect(Collectors.toMap(Class::getSimpleName, Function.identity()));
  }

  @SafeVarargs
  private static Map<String, Class<? extends Event>> eventTypes(Class<? extends Event>... types) {
    return Arrays.stream(types)
        .collect(Collectors.toMap(Class::getSimpleName, Function.identity()));
  }

  public Command readCommand(JsonNode jsonNode) throws JsonProcessingException {
    return readCommand(
        jsonNode.get("LATEST_DETAILS").asText(), jsonNode.get("LATEST_SUB_TYPE").asText());
  }

  public Event readEvent(JsonNode jsonNode) throws JsonProcessingException {
    return readEvent(jsonNode.get("LATEST_DETAILS").asText(), jsonNode.get("LATEST_SUB_TYPE").asText());
  }

  public Order readOrder(UUID orderId, JsonNode jsonNode) throws JsonProcessingException {
    List<String> typeList =
        StreamSupport.stream(jsonNode.get("TYPE_LIST").spliterator(), false)
            .map(JsonNode::asText)
            .collect(toList());

    List<String> subTypeList =
        StreamSupport.stream(jsonNode.get("SUB_TYPE_LIST").spliterator(), false)
            .map(JsonNode::asText)
            .collect(toList());

    List<String> detailsList =
        StreamSupport.stream(jsonNode.get("DETAILS_LIST").spliterator(), false)
            .map(JsonNode::asText)
            .collect(toList());

    return loadFromHistory(orderId, typeList, subTypeList, detailsList);
  }

  public Order readOrder(Row row) throws JsonProcessingException {
    UUID orderId = UUID.fromString(row.getString("ORDER_ID"));

    List<String> typeList =
        row.getKsqlArray("TYPE_LIST").stream().map(Object::toString).collect(toList());

    List<String> subTypeList =
        row.getKsqlArray("SUB_TYPE_LIST").stream().map(Object::toString).collect(toList());

    List<String> detailsList =
        row.getKsqlArray("DETAILS_LIST").stream().map(Object::toString).collect(toList());

    return loadFromHistory(orderId, typeList, subTypeList, detailsList);
  }

  private Order loadFromHistory(
      UUID orderId, List<String> typeList, List<String> subTypeList, List<String> detailsList)
      throws JsonProcessingException {
    List<Event> events = new ArrayList<>();
    for (int i = 0; i < detailsList.size(); i++) {
      if (!"event".equals(typeList.get(i))) {
        continue;
      }
      events.add(readEvent(detailsList.get(i), subTypeList.get(i)));
    }
    return new Order(orderId, events);
  }

  private Command readCommand(String json, String commandType) throws JsonProcessingException {
    Class<? extends Command> clazz = COMMAND_TYPES.get(commandType);
    if (clazz == null) {
      throw new UnsupportedOperationException("Unsupported command " + commandType);
    }
    return objectMapper.readValue(json, clazz);
  }

  private Event readEvent(String json, String eventType) throws JsonProcessingException {
    Class<? extends Event> clazz = EVENT_TYPES.get(eventType);
    if (clazz == null) {
      throw new UnsupportedOperationException("Unsupported event  " + eventType);
    }
    return objectMapper.readValue(json, clazz);
  }
}
