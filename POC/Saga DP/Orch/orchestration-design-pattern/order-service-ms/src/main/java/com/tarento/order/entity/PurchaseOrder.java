package com.tarento.order.entity;

import jakarta.persistence.*;

import java.util.UUID;

import com.tarento.order.dto.OrderStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "purchase_order")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID id;

    private Integer userId;

    private Integer productId;

    private Double price;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
