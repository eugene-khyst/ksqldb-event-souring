package com.example.ksqldbeventsourcing.service;

import static java.util.stream.Collectors.toList;

import com.example.ksqldbeventsourcing.eventsourcing.Command;
import com.example.ksqldbeventsourcing.eventsourcing.ErrorEvent;
import com.example.ksqldbeventsourcing.eventsourcing.Event;
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
    private final List<String> detailsList;

    @SneakyThrows
    public EventData(String json) {
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

    @SneakyThrows
    private Command readCommand(String type, String details) {
      Class<?> clazz =
          Class.forName("com.example.ksqldbeventsourcing.domain.writemodel.command." + type);
      return (Command) objectMapper.readValue(details, clazz);
    }

    @SneakyThrows
    private Event readEvent(String type, String details) {
      Class<?> clazz;
      if (ErrorEvent.class.getSimpleName().equals(type)) {
        clazz = ErrorEvent.class;
      } else {
        clazz = Class.forName("com.example.ksqldbeventsourcing.domain.writemodel.event." + type);
      }
      return (Event) objectMapper.readValue(details, clazz);
    }

    public boolean isLatestIsCommand() {
      return isCommandList.get(isCommandList.size() - 1);
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

    public List<Event> readEvents() {
      List<Event> events = new ArrayList<>();
      for (int i = 0; i < detailsList.size(); i++) {
        if (!isCommandList.get(i)) {
          events.add(readEvent(typeList.get(i), detailsList.get(i)));
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
    on.put("details", objectMapper.writeValueAsString(command));
    return on.toString();
  }

  @SneakyThrows
  public String serialize(Event event) {
    Objects.requireNonNull(event);
    ObjectNode on = objectMapper.createObjectNode();
    on.put("aggregate_id", event.getAggregateId().toString());
    on.put("is_command", false);
    on.put("type", event.getClass().getSimpleName());
    on.put("details", objectMapper.writeValueAsString(event));
    return on.toString();
  }
}
