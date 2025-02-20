package org.broadleafcommerce.core.order.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for {@link OrderLockImpl}
 */
public class OrderLockImplTest {

    private OrderLockImpl orderLock;

    @Before
    public void setUp() {
        orderLock = new OrderLockImpl();
    }

    @Test
    public void testOrderId() {
        // Arrange
        Long orderId = 100L;

        // Act
        orderLock.setOrderId(orderId);

        // Assert
        assertEquals(orderId, orderLock.getOrderId());
    }

    @Test
    public void testLocked() {
        // Test default value
        assertFalse(orderLock.getLocked());

        // Test setting to true
        orderLock.setLocked(true);
        assertTrue(orderLock.getLocked());

        // Test setting to false
        orderLock.setLocked(false);
        assertFalse(orderLock.getLocked());

        // Test setting to null
        orderLock.setLocked(null);
        assertFalse(orderLock.getLocked());
    }

    @Test
    public void testLastUpdated() {
        // Arrange
        Long lastUpdated = System.currentTimeMillis();

        // Act
        orderLock.setLastUpdated(lastUpdated);

        // Assert
        assertEquals(lastUpdated, orderLock.getLastUpdated());
    }

    @Test
    public void testKey() {
        // Arrange
        String key = "test-node-key";

        // Act
        orderLock.setKey(key);

        // Assert
        assertEquals(key, orderLock.getKey());
    }

    @Test
    public void testOrderLockPkEquals() {
        // Arrange
        OrderLockImpl.OrderLockPk pk1 = new OrderLockImpl.OrderLockPk();
        OrderLockImpl.OrderLockPk pk2 = new OrderLockImpl.OrderLockPk();

        pk1.setOrderId(100L);
        pk1.setKey("key1");

        pk2.setOrderId(100L);
        pk2.setKey("key1");

        // Test same values
        assertTrue(pk1.equals(pk2));
        assertTrue(pk2.equals(pk1));

        // Test same instance
        assertTrue(pk1.equals(pk1));

        // Test null
        assertFalse(pk1.equals(null));

        // Test different type
        assertFalse(pk1.equals(new Object()));

        // Test different values
        pk2.setOrderId(200L);
        assertFalse(pk1.equals(pk2));

        pk2.setOrderId(100L);
        pk2.setKey("key2");
        assertFalse(pk1.equals(pk2));
    }

    @Test
    public void testOrderLockPkHashCode() {
        // Arrange
        OrderLockImpl.OrderLockPk pk1 = new OrderLockImpl.OrderLockPk();
        OrderLockImpl.OrderLockPk pk2 = new OrderLockImpl.OrderLockPk();

        pk1.setOrderId(100L);
        pk1.setKey("key1");

        pk2.setOrderId(100L);
        pk2.setKey("key1");

        // Test same values have same hashcode
        assertEquals(pk1.hashCode(), pk2.hashCode());

        // Test different values have different hashcodes
        pk2.setOrderId(200L);
        assertNotEquals(pk1.hashCode(), pk2.hashCode());
    }

    @Test
    public void testCompleteWorkflow() {
        // Arrange
        Long orderId = 100L;
        String key = "test-key";
        Long lastUpdated = System.currentTimeMillis();

        // Act
        orderLock.setOrderId(orderId);
        orderLock.setKey(key);
        orderLock.setLastUpdated(lastUpdated);
        orderLock.setLocked(true);

        // Assert
        assertEquals(orderId, orderLock.getOrderId());
        assertEquals(key, orderLock.getKey());
        assertEquals(lastUpdated, orderLock.getLastUpdated());
        assertTrue(orderLock.getLocked());

        // Test unlock
        orderLock.setLocked(false);
        assertFalse(orderLock.getLocked());
    }

    @Test
    public void testCharacterLockValues() {
        // Test default value
        assertEquals('N', orderLock.locked.charValue());

        // Test setting to true
        orderLock.setLocked(true);
        assertEquals('Y', orderLock.locked.charValue());

        // Test setting to false
        orderLock.setLocked(false);
        assertEquals('N', orderLock.locked.charValue());

        // Test setting to null
        orderLock.setLocked(null);
        assertEquals('N', orderLock.locked.charValue());
    }
}