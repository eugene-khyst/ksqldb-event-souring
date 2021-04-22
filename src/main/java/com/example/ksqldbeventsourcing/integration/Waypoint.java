package com.example.ksqldbeventsourcing.integration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class Waypoint {

  @JsonProperty("ADDRESS")
  String address;

  @JsonProperty("LAT")
  double latitude;

  @JsonProperty("LON")
  double longitude;
}
