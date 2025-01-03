package com.tarento.inventory.service;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.tarento.inventory.dto.InventoryRequestDTO;
import com.tarento.inventory.dto.InventoryResponseDTO;
import com.tarento.inventory.dto.InventoryStatus;

import jakarta.annotation.PostConstruct;

@Service
@Slf4j
public class InventoryService {

    private Map<Integer, Integer> inventoryMap;

    @PostConstruct
    private void init() {
        inventoryMap = new HashMap<>();
        inventoryMap.put(1, 2);
        inventoryMap.put(2, 3);
        inventoryMap.put(3, 4);
    }

    /**
     * Deducts inventory for the specified product if available.
     *
     * @param requestDTO the request data containing product and user details
     * @return a response containing the status and details of the deduction operation
     */
    public InventoryResponseDTO deduct(InventoryRequestDTO requestDTO) {
        try {
            validateInventoryRequest(requestDTO);
            int qty = inventoryMap.getOrDefault(requestDTO.getProductId(), 0);
            InventoryResponseDTO responseDTO = new InventoryResponseDTO();
            responseDTO.setOrderId(requestDTO.getOrderId());
            responseDTO.setProductId(requestDTO.getProductId());
            responseDTO.setUserId(requestDTO.getUserId());
            responseDTO.setStatus(InventoryStatus.UNAVAILABLE);
            log.info("Checking inventory for Product ID: {}, Current Quantity: {}", requestDTO.getProductId(), qty);
            if (qty > 0) {
                responseDTO.setStatus(InventoryStatus.AVAILABLE);
                inventoryMap.put(requestDTO.getProductId(), qty - 1);
                log.info("Inventory updated for Product ID: {}, Remaining Quantity: {}", requestDTO.getProductId(), inventoryMap.get(requestDTO.getProductId()));
            } else {
                log.warn("Inventory unavailable for Product ID: {}", requestDTO.getProductId());
                responseDTO.setStatus(InventoryStatus.UNAVAILABLE);
            }
            return responseDTO;
        } catch (IllegalArgumentException ex) {
            log.error("Invalid inventory request: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during inventory deduction: {}", ex.getMessage(), ex);
            throw new RuntimeException("An error occurred while processing inventory deduction. Please try again later.");
        }
    }

    /**
     * Adds inventory for the specified product.
     *
     * @param requestDTO the request data containing product details
     */
    public void add(InventoryRequestDTO requestDTO) {
        try {
            validateInventoryRequest(requestDTO);
            log.info("Adding inventory for Product ID: {}, Current Quantity: {}", requestDTO.getProductId(), inventoryMap.getOrDefault(requestDTO.getProductId(), 0));
            if (inventoryMap.containsKey(requestDTO.getProductId())) {
                inventoryMap.computeIfPresent(requestDTO.getProductId(), (k, v) -> v + 1);
                log.info("Inventory added successfully for Product ID: {}, New Quantity: {}", requestDTO.getProductId(), inventoryMap.get(requestDTO.getProductId()));
            } else {
                log.warn("Invalid Product ID: {}. Cannot add inventory.", requestDTO.getProductId());
                throw new IllegalArgumentException("The specified product ID does not exist.");
            }
        } catch (IllegalArgumentException ex) {
            log.error("Invalid inventory request {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during inventory addition: {}", ex.getMessage(), ex);
            throw new RuntimeException("An error occurred while adding inventory. Please try again later.");
        }
    }

    /**
     * Validates an {@link InventoryRequestDTO}.
     *
     * @param requestDTO the inventory request to validate
     * @throws IllegalArgumentException if the request is null or missing required fields
     */
    private void validateInventoryRequest(InventoryRequestDTO requestDTO) {
        if (requestDTO == null) {
            log.error("Inventory request is null.");
            throw new IllegalArgumentException("Inventory request cannot be null.");
        }
        if (requestDTO.getProductId() == null || requestDTO.getUserId() == null) {
            log.error("Invalid inventory request: Missing required fields. Product ID and User ID are mandatory.");
            throw new IllegalArgumentException("Product ID and User ID are required.");
        }
        log.info("Inventory request validated successfully for Product ID: {}", requestDTO.getProductId());
    }
}
