package com.example.ksqldbeventsourcing.model.read;

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
