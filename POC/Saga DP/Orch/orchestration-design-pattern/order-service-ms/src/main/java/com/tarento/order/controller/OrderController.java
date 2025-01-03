package com.tarento.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.tarento.order.dto.OrderRequestDTO;
import com.tarento.order.dto.OrderResponseDTO;
import com.tarento.order.entity.PurchaseOrder;
import com.tarento.order.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public PurchaseOrder createOrder(@RequestBody OrderRequestDTO orderRequest) {
        return orderService.createOrder(orderRequest);
    }

    @GetMapping("/all")
    public List<OrderResponseDTO> getOrders() {
        return orderService.getAllOrders();
    }
}
