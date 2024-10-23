package com.quantum.controller;

import com.quantum.model.PurchaseOrder;
import com.quantum.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @Autowired
    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    /**
     * Create a new purchase order for a restaurant.
     *
     * @param restaurantId The UUID of the restaurant.
     * @param order        The purchase order object.
     * @return The created purchase order.
     */
    @PostMapping("/{restaurantId}")
    public ResponseEntity<PurchaseOrder> createPurchaseOrder(
            @PathVariable UUID restaurantId,
            @RequestBody PurchaseOrder order) {
        PurchaseOrder createdOrder = purchaseOrderService.createPurchaseOrder(restaurantId, order);
        return ResponseEntity.ok(createdOrder);
    }

    /**
     * Get all purchase orders for a specific restaurant.
     *
     * @param restaurantId The UUID of the restaurant.
     * @return A list of purchase orders for the restaurant.
     */
    @GetMapping("/{restaurantId}")
    public ResponseEntity<List<PurchaseOrder>> getPurchaseOrdersByRestaurant(
            @PathVariable UUID restaurantId) {
        List<PurchaseOrder> orders = purchaseOrderService.getPurchaseOrdersByRestaurant(restaurantId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Update the status of a purchase order (e.g., mark it as RECEIVED).
     *
     * @param orderId The UUID of the purchase order.
     * @param status  The new status for the purchase order.
     * @return The updated purchase order.
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<PurchaseOrder> updatePurchaseOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam String status) {
        PurchaseOrder updatedOrder = purchaseOrderService.updatePurchaseOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Delete a purchase order.
     *
     * @param orderId The UUID of the purchase order to delete.
     * @return A no-content response indicating successful deletion.
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deletePurchaseOrder(@PathVariable UUID orderId) {
        purchaseOrderService.deletePurchaseOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
