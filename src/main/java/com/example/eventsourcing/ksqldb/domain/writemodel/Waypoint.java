package com.example.eventsourcing.ksqldb.domain.writemodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class Waypoint {

  String address;

  @JsonProperty("lat")
  double latitude;

  @JsonProperty("lon")
  double longitude;
}
