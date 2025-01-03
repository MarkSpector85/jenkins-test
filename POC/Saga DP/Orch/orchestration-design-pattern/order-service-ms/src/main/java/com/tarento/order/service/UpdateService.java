package com.tarento.order.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tarento.order.dto.OrchestratorResponseDTO;
import com.tarento.order.repo.PurchaseOrderRepository;
import com.tarento.order.entity.PurchaseOrder;

import java.util.Optional;

@Service
@Slf4j
public class UpdateService {

    private final PurchaseOrderRepository repo;

    @Autowired
    public UpdateService(PurchaseOrderRepository repo) {
        this.repo = repo;
    }

    /**
     * Updates the status of a PurchaseOrder in the database.
     * This method processes an OrchestratorResponseDTO to retrieve the order ID and
     * the updated status. If the order is found, its status is updated and saved back
     * to the database.
     *
     * @param responseDTO contains the orderId and the new status to be updated
     */
    public void updateOrder(OrchestratorResponseDTO responseDTO) {
        try {
            log.info("Response {} ", responseDTO.getStatus());
            Optional<PurchaseOrder> purchaseOrderOptional = repo.findById(responseDTO.getOrderId());
            if (purchaseOrderOptional.isPresent()) {
                PurchaseOrder purchaseOrder = purchaseOrderOptional.get();
                purchaseOrder.setStatus(responseDTO.getStatus());
                repo.save(purchaseOrder);
                log.info("Order with ID {} updated successfully to status {}",
                        responseDTO.getOrderId(), responseDTO.getStatus());
            } else {
                log.error("Order with ID {} not found in the database", responseDTO.getOrderId());
            }
        } catch (Exception ex) {
            log.error("An unexpected error occurred while updating order with ID {}: {}",
                    responseDTO.getOrderId(), ex.getMessage(), ex);
        }
    }
}
