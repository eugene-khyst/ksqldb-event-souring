package com.example.eventsourcing.ksqldb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KsqldbEventSourcingApplication {

  public static void main(String[] args) {
    SpringApplication.run(KsqldbEventSourcingApplication.class, args);
  }
}
