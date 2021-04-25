package com.example.eventsourcing.ksqldb.service;

import static java.util.stream.Collectors.toList;

import com.example.eventsourcing.ksqldb.domain.writemodel.command.PlaceOrderCommand;
import com.example.eventsourcing.ksqldb.domain.writemodel.event.OrderPlacedEvent;
import com.example.eventsourcing.ksqldb.eventsourcing.Command;
import com.example.eventsourcing.ksqldb.eventsourcing.ErrorEvent;
import com.example.eventsourcing.ksqldb.eventsourcing.Event;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventJsonSerde {

  public final class EventData {

    private final List<Boolean> isCommandList;
    private final List<String> typeList;
    private final List<String> jsonDataList;

    @SneakyThrows
    public EventData(String json) {
      JsonNode jsonNode = objectMapper.readTree(json);
      this.isCommandList = readIsCommandList(jsonNode);
      this.typeList = readTypeList(jsonNode);
      this.jsonDataList = readJsonDataList(jsonNode);
    }

    private List<Boolean> readIsCommandList(JsonNode jsonNode) {
      return StreamSupport.stream(jsonNode.get("IS_COMMAND_LIST").spliterator(), false)
          .map(JsonNode::asBoolean)
          .collect(toList());
    }

    private List<String> readJsonDataList(JsonNode jsonNode) {
      return StreamSupport.stream(jsonNode.get("JSON_DATA_LIST").spliterator(), false)
          .map(JsonNode::asText)
          .collect(toList());
    }

    private List<String> readTypeList(JsonNode jsonNode) {
      return StreamSupport.stream(jsonNode.get("TYPE_LIST").spliterator(), false)
          .map(JsonNode::asText)
          .collect(toList());
    }

    @SneakyThrows
    private Command readCommand(String type, String jsonData) {
      Class<?> clazz = Class.forName(PlaceOrderCommand.class.getPackageName() + "." + type);
      return (Command) objectMapper.readValue(jsonData, clazz);
    }

    @SneakyThrows
    private Event readEvent(String type, String jsonData) {
      Class<?> clazz;
      if (ErrorEvent.class.getSimpleName().equals(type)) {
        clazz = ErrorEvent.class;
      } else {
        clazz = Class.forName(OrderPlacedEvent.class.getPackageName() + "." + type);
      }
      return (Event) objectMapper.readValue(jsonData, clazz);
    }

    public boolean isLatestIsCommand() {
      return isCommandList.get(isCommandList.size() - 1);
    }

    public List<Command> readLatestCommands() {
      List<Command> commands = new ArrayList<>();
      for (int i = jsonDataList.size() - 1; i >= 0; i--) {
        if (isCommandList.get(i)) {
          commands.add(readCommand(typeList.get(i), jsonDataList.get(i)));
        } else {
          break;
        }
      }
      return commands;
    }

    public List<Event> readEvents() {
      List<Event> events = new ArrayList<>();
      for (int i = 0; i < jsonDataList.size(); i++) {
        if (!isCommandList.get(i)) {
          events.add(readEvent(typeList.get(i), jsonDataList.get(i)));
        }
      }
      return events;
    }
  }

  private final ObjectMapper objectMapper;

  public EventData deserialize(String json) {
    return new EventData(json);
  }

  @SneakyThrows
  public String serialize(Command command) {
    Objects.requireNonNull(command);
    ObjectNode on = objectMapper.createObjectNode();
    on.put("aggregate_id", command.getAggregateId().toString());
    on.put("is_command", true);
    on.put("type", command.getClass().getSimpleName());
    on.put("json_data", objectMapper.writeValueAsString(command));
    return on.toString();
  }

  @SneakyThrows
  public String serialize(Event event) {
    Objects.requireNonNull(event);
    ObjectNode on = objectMapper.createObjectNode();
    on.put("aggregate_id", event.getAggregateId().toString());
    on.put("is_command", false);
    on.put("type", event.getClass().getSimpleName());
    on.put("json_data", objectMapper.writeValueAsString(event));
    return on.toString();
  }
}
