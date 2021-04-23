package com.example.ksqldbeventsourcing.model.domain;

import com.example.ksqldbeventsourcing.model.event.ErrorEvent;
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
