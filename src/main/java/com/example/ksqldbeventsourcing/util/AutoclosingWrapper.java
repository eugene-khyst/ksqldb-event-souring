package com.example.ksqldbeventsourcing.util;

import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AutoclosingWrapper<T> implements AutoCloseable {

  @Getter
  private final T object;

  private final Consumer<T> onClose;

  @Override
  public void close() {
    onClose.accept(object);
  }
}
