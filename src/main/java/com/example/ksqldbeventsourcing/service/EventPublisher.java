package com.example.ksqldbeventsourcing.service;

import com.example.ksqldbeventsourcing.model.event.Event;

public interface EventPublisher {

  void publish(Event event);
}
