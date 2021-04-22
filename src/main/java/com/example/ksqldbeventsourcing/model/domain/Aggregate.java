package com.example.ksqldbeventsourcing.model.domain;

import com.example.ksqldbeventsourcing.model.command.Command;
import com.example.ksqldbeventsourcing.model.event.ErrorEvent;
import com.example.ksqldbeventsourcing.model.event.Event;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public abstract class Aggregate {

  protected final UUID aggregateId;

  @JsonProperty("version")
  protected int baseVersion = 0;

  @JsonIgnore protected final List<Event> events;
  @JsonIgnore protected final List<Event> changes = new ArrayList<>();

  protected final List<String> errors = new ArrayList<>();

  public Aggregate(UUID aggregateId, List<Event> events) {
    Objects.requireNonNull(aggregateId);
    Objects.requireNonNull(events);
    this.aggregateId = aggregateId;
    this.events = List.copyOf(events);
    this.events.forEach(
        event -> {
          apply(event);
          baseVersion = event.getVersion();
        });
  }

  public Aggregate(UUID aggregateId) {
    this(aggregateId, Collections.emptyList());
  }

  protected void applyChange(Event event) {
    if (event.getVersion() != getNextVersion()) {
      throw new IllegalStateException(
          String.format(
              "Event version %s doesn't match expected version %s",
              event.getVersion(), getNextVersion()));
    }
    apply(event);
    changes.add(event);
  }

  private void apply(Event event) {
    invoke(event, "apply");
  }

  public void process(Command command) {
    invoke(command, "process");
  }

  private void invoke(Object o, String methodName) {
    try {
      Method method = this.getClass().getMethod(methodName, o.getClass());
      method.invoke(this, o);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new UnsupportedOperationException(
          String.format(
              "Aggregate '%s' doesn't support %s(%s)",
              this.getClass().getSimpleName(), methodName, o.getClass().getSimpleName()),
          e);
    }
  }

  public void error(String errorMessage) {
    applyChange(new ErrorEvent(aggregateId, getNextVersion(), errorMessage));
  }

  public void apply(ErrorEvent event) {
    log.info("Error '{}' in aggregate {}", event.getErrorMessage(), this);
    errors.add(event.getErrorMessage());
  }

  protected int getNextVersion() {
    return baseVersion + changes.size() + 1;
  }
}