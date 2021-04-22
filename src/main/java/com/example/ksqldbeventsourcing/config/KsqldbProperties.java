package com.example.ksqldbeventsourcing.config;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("ksqldb")
@Data
public class KsqldbProperties {

  private URL serverUrl;
  private boolean init = true;
  private List<String> initSql = new ArrayList<>();
}
