package com.example.eventsourcing.ksqldb.domain.readmodel;

import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class ErrorMessage implements Serializable {

  private String command;
  private int expectedVersion;
  private String message;
}
