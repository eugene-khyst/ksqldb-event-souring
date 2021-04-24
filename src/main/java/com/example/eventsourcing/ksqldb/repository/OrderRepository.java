package com.example.eventsourcing.ksqldb.repository;

import com.example.eventsourcing.ksqldb.domain.readmodel.Order;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {}
