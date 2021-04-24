package com.example.eventsourcing.ksqldb.eventsourcing;

import lombok.Value;

@Value
public class ErrorMessage {

  String commandType;
  int expectedVersion;
  String message;

  public static ErrorMessage from(ErrorEvent event) {
    return new ErrorMessage(
        event.getCommandType(), event.getExpectedVersion(), event.getErrorMessage());
  }
}
