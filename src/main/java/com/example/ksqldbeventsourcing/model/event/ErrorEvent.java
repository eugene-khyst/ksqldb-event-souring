package com.example.ksqldbeventsourcing.model.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErrorEvent extends Event {

  String errorMessage;

  @JsonCreator
  public ErrorEvent(UUID aggregateId, int version, String errorMessage) {
    super(aggregateId, version);
    this.errorMessage = errorMessage;
  }
}
