package com.example.ksqldbeventsourcing.config;

import com.example.ksqldbeventsourcing.util.AutoclosingWrapper;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KsqldbClientFactory {

  private final KsqldbProperties ksqldbProperties;

  public AutoclosingWrapper<Client> newClient() {
    ClientOptions options =
        ClientOptions.create()
            .setHost(ksqldbProperties.getServerUrl().getHost())
            .setPort(ksqldbProperties.getServerUrl().getPort());
    Client client = Client.create(options);
    log.debug("Creating new ksqlDB client: {}", ksqldbProperties);
    return new AutoclosingWrapper<>(client, Client::close);
  }
}
