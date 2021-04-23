package com.example.ksqldbeventsourcing.service;

import static java.util.stream.Collectors.toList;

import com.example.ksqldbeventsourcing.model.command.Command;
import com.example.ksqldbeventsourcing.model.domain.Order;
import com.example.ksqldbeventsourcing.model.event.Event;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderJsonMapper {

  public static final class OrderAggregate {

    private final ObjectMapper objectMapper;
    private final UUID orderId;
    private final List<Boolean> isCommandList;
    private final List<String> typeList;
    private final List<String> detailsList;

    @SneakyThrows
    public OrderAggregate(ObjectMapper objectMapper, UUID orderId, String json) {
      this.objectMapper = objectMapper;
      this.orderId = orderId;
      JsonNode jsonNode = objectMapper.readTree(json);
      this.isCommandList = readIsCommandList(jsonNode);
      this.typeList = readTypeList(jsonNode);
      this.detailsList = readDetailsList(jsonNode);
    }

    private List<Boolean> readIsCommandList(JsonNode jsonNode) {
      return StreamSupport.stream(jsonNode.get("IS_COMMAND_LIST").spliterator(), false)
          .map(JsonNode::asBoolean)
          .collect(toList());
    }

    private List<String> readDetailsList(JsonNode jsonNode) {
      return StreamSupport.stream(jsonNode.get("DETAILS_LIST").spliterator(), false)
          .map(JsonNode::asText)
          .collect(toList());
    }

    private List<String> readTypeList(JsonNode jsonNode) {
      return StreamSupport.stream(jsonNode.get("TYPE_LIST").spliterator(), false)
          .map(JsonNode::asText)
          .collect(toList());
    }

    public boolean isLatestIsCommand() {
      return isCommandList.get(isCommandList.size() - 1);
    }

    @SneakyThrows
    private Command readCommand(String type, String details) {
      Class<?> clazz = Class.forName("com.example.ksqldbeventsourcing.model.command." + type);
      return (Command) objectMapper.readValue(details, clazz);
    }

    @SneakyThrows
    private Event readEvent(String type, String details) {
      Class<?> clazz = Class.forName("com.example.ksqldbeventsourcing.model.event." + type);
      return (Event) objectMapper.readValue(details, clazz);
    }

    public List<Command> readLatestCommands() {
      List<Command> commands = new ArrayList<>();
      for (int i = detailsList.size() - 1; i >= 0; i--) {
        if (isCommandList.get(i)) {
          commands.add(readCommand(typeList.get(i), detailsList.get(i)));
        } else {
          break;
        }
      }
      return commands;
    }

    public Event readLatestEvent() {
      if (isLatestIsCommand()) {
        return null;
      }
      int lastIndex = detailsList.size() - 1;
      return readEvent(typeList.get(lastIndex), detailsList.get(lastIndex));
    }

    public Order readOrder() {
      List<Event> events = new ArrayList<>();
      for (int i = 0; i < detailsList.size(); i++) {
        if (!isCommandList.get(i)) {
          events.add(readEvent(typeList.get(i), detailsList.get(i)));
        }
      }
      return new Order(orderId, events);
    }
  }

  private final ObjectMapper objectMapper;

  public OrderAggregate readOrderAggregate(UUID orderId, String json) {
    return new OrderAggregate(objectMapper, orderId, json);
  }

  @SneakyThrows
  public String write(Command command) {
    Objects.requireNonNull(command);
    ObjectNode on = objectMapper.createObjectNode();
    on.put("order_id", command.getAggregateId().toString());
    on.put("is_command", true);
    on.put("type", command.getClass().getSimpleName());
    on.put("details", objectMapper.writeValueAsString(command));
    return on.toString();
  }

  @SneakyThrows
  public String write(Event event) {
    Objects.requireNonNull(event);
    ObjectNode on = objectMapper.createObjectNode();
    on.put("order_id", event.getAggregateId().toString());
    on.put("is_command", false);
    on.put("type", event.getClass().getSimpleName());
    on.put("details", objectMapper.writeValueAsString(event));
    return on.toString();
  }
}
