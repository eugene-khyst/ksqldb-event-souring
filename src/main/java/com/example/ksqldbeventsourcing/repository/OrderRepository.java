package com.example.ksqldbeventsourcing.repository;

import com.example.ksqldbeventsourcing.domain.readmodel.Order;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {}
