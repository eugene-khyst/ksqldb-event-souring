package com.example.ksqldbeventsourcing.service;

import com.example.ksqldbeventsourcing.config.KafkaTopicsConfig;
import com.example.ksqldbeventsourcing.eventsourcing.Command;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCommandSender {

  private final EventJsonSerde jsonSerde;
  private final KafkaTemplate<String, String> kafkaTemplate;

  public void send(Command command) {
    Objects.requireNonNull(command);
    log.debug("Sending command {}", command);
    kafkaTemplate.send(
        KafkaTopicsConfig.TOPIC_ORDER_COMMANDS_AND_EVENTS,
        command.getAggregateId().toString(),
        jsonSerde.serialize(command));
  }
}
