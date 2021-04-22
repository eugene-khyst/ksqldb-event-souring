package com.example.ksqldbeventsourcing.config;

import com.example.ksqldbeventsourcing.util.AutoclosingWrapper;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.StreamInfo;
import io.confluent.ksql.api.client.TableInfo;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KsqldbSqlInitializer {

  private static final Pattern CREATE_STREAM_OR_TABLE_PATTERN =
      Pattern.compile("^CREATE\\s+(STREAM|TABLE)\\s+([\\w+]+)", Pattern.CASE_INSENSITIVE);

  private final KsqldbClientFactory ksqldbClientFactory;
  private final KsqldbProperties ksqldbProperties;

  @PostConstruct
  public void init() {
    if (!ksqldbProperties.isInit()) {
      log.info("Skipping ksqlDB schema init: {}", ksqldbProperties);
      return;
    }

    log.info("Creating ksqlDB streams, tables, queries: {}", ksqldbProperties);
    try (AutoclosingWrapper<Client> clientWrapper = ksqldbClientFactory.newClient()) {
      Client client = clientWrapper.getObject();

      log.info("Checking if ksqlDB schema is up to date");

      List<StreamInfo> streams = client.listStreams().get();
      List<TableInfo> tables = client.listTables().get();

      Map<String, Object> properties = Collections.singletonMap("auto.offset.reset", "earliest");

      for (String sql : ksqldbProperties.getInitSql()) {
        Matcher matcher = CREATE_STREAM_OR_TABLE_PATTERN.matcher(sql);
        if (!matcher.find()) {
          log.warn("Can't parse SQL statement: {}", sql);
        }

        String objectType = matcher.group(1);
        String objectName = matcher.group(2);

        if ("STREAM".equalsIgnoreCase(objectType)) {
          if (streams.stream()
              .map(StreamInfo::getName)
              .noneMatch(name -> name.equals(objectName))) {
            log.info("Creating stream {}", objectName);
            client.executeStatement(sql, properties).get();
          } else {
            log.debug("Stream {} already exists", objectName);
          }
        } else if ("TABLE".equalsIgnoreCase(objectType)) {
          if (tables.stream().map(TableInfo::getName).noneMatch(name -> name.equals(objectName))) {
            log.info("Creating table {}", objectName);
            client.executeStatement(sql, properties).get();
          } else {
            log.debug("Table {} already exists", objectName);
          }
        } else {
          log.warn("Expected STREAM or TABLE but was {}: {}", objectType, sql);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Error while initializing ksqlDB schema", e);
    }
  }
}
