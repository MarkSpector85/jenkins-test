package com.tarento.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tarento.inventory.dto.InventoryRequestDTO;
import com.tarento.inventory.dto.InventoryResponseDTO;
import com.tarento.inventory.service.InventoryService;

@RestController
@RequestMapping("inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Deducts inventory based on the provided request data.
     *
     * @param requestDTO the request data containing inventory deduction details
     * @return a response containing the status and details of the deduction operation
     */
    @PostMapping("/deduct")
    public InventoryResponseDTO deduct(@RequestBody InventoryRequestDTO requestDTO) {
        return inventoryService.deduct(requestDTO);

    }

    /**
     * Adds inventory based on the provided request data.
     *
     * @param requestDTO the request data containing inventory addition details
     */
    @PostMapping("/add")
    public void add(@RequestBody InventoryRequestDTO requestDTO) {
        inventoryService.add(requestDTO);
    }
}
