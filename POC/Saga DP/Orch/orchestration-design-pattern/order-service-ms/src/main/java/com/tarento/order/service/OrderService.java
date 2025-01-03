package com.tarento.order.service;

import java.util.Map;

import com.tarento.order.kafka.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tarento.order.dto.OrchestratorRequestDTO;
import com.tarento.order.dto.OrderRequestDTO;
import com.tarento.order.dto.OrderResponseDTO;
import com.tarento.order.dto.OrderStatus;
import com.tarento.order.entity.PurchaseOrder;
import com.tarento.order.repo.PurchaseOrderRepository;

import java.util.List;

@Service
@Slf4j
public class OrderService {

    private static final Map<Integer, Double> ORDER_PRICE = Map.of(
            1, 100d,
            2, 200d,
            3, 300d
    );

    private final PurchaseOrderRepository repository;

    private final KafkaProducerService kafkaProducer;

    @Autowired
    public OrderService(PurchaseOrderRepository repository, KafkaProducerService kafkaProducer) {
        this.repository = repository;
        this.kafkaProducer = kafkaProducer;
    }

    public PurchaseOrder createOrder(OrderRequestDTO orderRequestDTO) {
        PurchaseOrder purchaseOrder = dtoToEntity(orderRequestDTO);
        PurchaseOrder savedOrder = repository.save(purchaseOrder);
        orderRequestDTO.setOrderId(savedOrder.getId());
        emitEvent(orderRequestDTO);
        return savedOrder;
    }

    public List<OrderResponseDTO> getAllOrders() {
        List<PurchaseOrder> orders = repository.findAll();
        return orders.stream()
                .map(this::entityToDto)
                .toList();
    }

    public void emitEvent(OrderRequestDTO orderRequestDTO) {
        OrchestratorRequestDTO requestDTO = getOrchestratorRequestDTO(orderRequestDTO);
        String topic = "order-created"; // Kafka topic name
        kafkaProducer.sendMessage(topic, requestDTO.getOrderId().toString(), requestDTO);
        log.info("Event emitted to Kafka: {} ", requestDTO);
    }

    private PurchaseOrder dtoToEntity(final OrderRequestDTO dto) {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(dto.getOrderId());
        purchaseOrder.setProductId(dto.getProductId());
        purchaseOrder.setUserId(dto.getUserId());
        purchaseOrder.setStatus(OrderStatus.ORDER_CREATED);
        purchaseOrder.setPrice(ORDER_PRICE.get(purchaseOrder.getProductId()));
        return purchaseOrder;
    }

    public OrchestratorRequestDTO getOrchestratorRequestDTO(OrderRequestDTO orderRequestDTO) {
        OrchestratorRequestDTO requestDTO = new OrchestratorRequestDTO();
        requestDTO.setUserId(orderRequestDTO.getUserId());
        requestDTO.setAmount(ORDER_PRICE.get(orderRequestDTO.getProductId()));
        requestDTO.setOrderId(orderRequestDTO.getOrderId());
        requestDTO.setProductId(orderRequestDTO.getProductId());
        return requestDTO;
    }

    private OrderResponseDTO entityToDto(PurchaseOrder purchaseOrder) {
        log.info("Purchase Order Status {} ", purchaseOrder.getStatus());
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(purchaseOrder.getId());
        dto.setProductId(purchaseOrder.getProductId());
        dto.setUserId(purchaseOrder.getUserId());
        dto.setStatus(purchaseOrder.getStatus());
        dto.setAmount(purchaseOrder.getPrice());
        return dto;
    }
}
