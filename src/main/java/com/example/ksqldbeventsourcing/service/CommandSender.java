package com.example.ksqldbeventsourcing.service;

import com.example.ksqldbeventsourcing.model.command.Command;

public interface CommandSender {

  void send(Command command);
}
