package com.example.eventsourcing.ksqldb.eventsourcing;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErrorEvent extends Event {

  private String commandType;
  private int expectedVersion;
  private String errorMessage;

  @Builder
  public ErrorEvent(
      UUID aggregateId, String commandType, int expectedVersion, String errorMessage) {
    super(aggregateId, 0);
    this.commandType = commandType;
    this.expectedVersion = expectedVersion;
    this.errorMessage = errorMessage;
  }
}
