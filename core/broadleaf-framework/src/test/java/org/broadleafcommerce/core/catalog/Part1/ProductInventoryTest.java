package org.broadleafcommerce.core.catalog.Part1;

import org.broadleafcommerce.core.catalog.domain.*;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for Product Inventory
 * Using partition testing approach with following partitions:
 * 1. Stock Quantity Partitions
 * 2. Inventory Type Partitions
 * 3. Stock Operations Partitions
 */
public class ProductInventoryTest {
    private Product product;
    private Sku sku;
    @Before
    public void setUp() {
        product = new ProductImpl();
        sku = new SkuImpl();
        product.setDefaultSku(sku);
    }

    // ===== Stock Quantity Tests =====

    @Test
    public void testInStockQuantity() {
        sku.setInventoryType(InventoryType.CHECK_QUANTITY);
        sku.setQuantityAvailable(100);
        assertEquals(Integer.valueOf(100), sku.getQuantityAvailable());
    }

    @Test
    public void testOutOfStockQuantity() {
        sku.setInventoryType(InventoryType.CHECK_QUANTITY);
        sku.setQuantityAvailable(0);
        assertEquals(Integer.valueOf(0), sku.getQuantityAvailable());
    }

    @Test
    public void testLowStockQuantity() {
        sku.setInventoryType(InventoryType.CHECK_QUANTITY);
        int threshold = 10;
        sku.setQuantityAvailable(5);
        assertTrue(sku.getQuantityAvailable() < threshold);
    }

    // ===== Inventory Type Tests =====

    @Test
    public void testCheckQuantityType() {
        sku.setInventoryType(InventoryType.CHECK_QUANTITY);
        sku.setQuantityAvailable(0);
        assertEquals(InventoryType.CHECK_QUANTITY, sku.getInventoryType());
    }

    @Test
    public void testAlwaysAvailableType() {
        sku.setInventoryType(InventoryType.ALWAYS_AVAILABLE);
        sku.setQuantityAvailable(0);
        assertEquals(InventoryType.ALWAYS_AVAILABLE, sku.getInventoryType());
    }

    @Test
    public void testUnavailableType() {
        sku.setInventoryType(InventoryType.UNAVAILABLE);
        sku.setQuantityAvailable(100);
        assertEquals(InventoryType.UNAVAILABLE, sku.getInventoryType());
    }

    // ===== Stock Operations Tests =====

    @Test
    public void testAddStock() {
        sku.setInventoryType(InventoryType.CHECK_QUANTITY);
        sku.setQuantityAvailable(100);
        int addQuantity = 50;
        sku.setQuantityAvailable(sku.getQuantityAvailable() + addQuantity);
        assertEquals(Integer.valueOf(150), sku.getQuantityAvailable());
    }

    @Test
    public void testReduceStock() {
        sku.setInventoryType(InventoryType.CHECK_QUANTITY);
        sku.setQuantityAvailable(100);
        int reduceQuantity = 30;
        sku.setQuantityAvailable(sku.getQuantityAvailable() - reduceQuantity);
        assertEquals(Integer.valueOf(70), sku.getQuantityAvailable());
    }

    @Test
    public void testMultipleStockReductions() {
        sku.setInventoryType(InventoryType.CHECK_QUANTITY);
        sku.setQuantityAvailable(100);
        // Test multiple reductions
        sku.setQuantityAvailable(sku.getQuantityAvailable() - 20);
        assertEquals(Integer.valueOf(80), sku.getQuantityAvailable());
        sku.setQuantityAvailable(sku.getQuantityAvailable() - 30);
        assertEquals(Integer.valueOf(50), sku.getQuantityAvailable());
    }

    @Test
    public void testBulkStockUpdate() {
        sku.setInventoryType(InventoryType.CHECK_QUANTITY);
        sku.setQuantityAvailable(1000);
        assertEquals(Integer.valueOf(1000), sku.getQuantityAvailable());
        sku.setQuantityAvailable(0);
        assertEquals(Integer.valueOf(0), sku.getQuantityAvailable());
    }

    /**
     * Test inventory type change
     * Testing transition between different inventory types
     */
    @Test
    public void testInventoryTypeChange() {
        sku.setInventoryType(InventoryType.CHECK_QUANTITY);
        sku.setQuantityAvailable(100);
        assertEquals(InventoryType.CHECK_QUANTITY, sku.getInventoryType());
        assertEquals(Integer.valueOf(100), sku.getQuantityAvailable());

        sku.setInventoryType(InventoryType.ALWAYS_AVAILABLE);
        assertEquals(InventoryType.ALWAYS_AVAILABLE, sku.getInventoryType());
        assertEquals(Integer.valueOf(100), sku.getQuantityAvailable());

        sku.setInventoryType(InventoryType.UNAVAILABLE);
        assertEquals(InventoryType.UNAVAILABLE, sku.getInventoryType());
        assertEquals(Integer.valueOf(100), sku.getQuantityAvailable());

        sku.setInventoryType(InventoryType.CHECK_QUANTITY);
        assertEquals(InventoryType.CHECK_QUANTITY, sku.getInventoryType());
        assertEquals(Integer.valueOf(100), sku.getQuantityAvailable());
    }
}