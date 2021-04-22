package com.example.ksqldbeventsourcing.repository;

import com.example.ksqldbeventsourcing.config.KsqldbClientFactory;
import com.example.ksqldbeventsourcing.model.domain.Aggregate;
import com.example.ksqldbeventsourcing.util.AutoclosingWrapper;
import io.confluent.ksql.api.client.BatchedQueryResult;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.Row;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KsqlRepository {

  @FunctionalInterface
  public interface RowMapper<T> {

    T map(Row row) throws Exception;
  }

  private final KsqldbClientFactory ksqldbClientFactory;

  public <T extends Aggregate> Optional<T> findAggregate(String pullQuery, RowMapper<T> rowMapper) {
    try (AutoclosingWrapper<Client> clientWrapper = ksqldbClientFactory.newClient()) {
      Client client = clientWrapper.getObject();
      BatchedQueryResult batchedQueryResult = client.executeQuery(pullQuery);
      List<Row> resultRows = batchedQueryResult.get();
      for (Row resultRow : resultRows) {
        T map = rowMapper.map(resultRow);
        return Optional.of(map);
      }
      return Optional.empty();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
