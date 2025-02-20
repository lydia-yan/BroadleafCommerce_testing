package org.broadleafcommerce.core.order.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for {@link OrderAttributeImpl}
 */
public class OrderAttributeImplTest {

    private OrderAttributeImpl orderAttribute;
    private Order order;

    @Before
    public void setUp() {
        orderAttribute = new OrderAttributeImpl();
        order = new OrderImpl();
    }

    @Test
    public void testBasicProperties() {
        // Arrange
        Long id = 1L;
        String name = "testAttribute";
        String value = "testValue";

        // Act
        orderAttribute.setId(id);
        orderAttribute.setName(name);
        orderAttribute.setValue(value);
        orderAttribute.setOrder(order);

        // Assert
        assertEquals(id, orderAttribute.getId());
        assertEquals(name, orderAttribute.getName());
        assertEquals(value, orderAttribute.getValue());
        assertEquals(order, orderAttribute.getOrder());
    }

    @Test
    public void testToString() {
        // Arrange
        String value = "testValue";
        orderAttribute.setValue(value);

        // Act & Assert
        assertEquals(value, orderAttribute.toString());
    }

    @Test
    public void testHashCode() {
        // Arrange
        String value = "testValue";
        orderAttribute.setValue(value);

        // Act & Assert
        assertEquals(value.hashCode(), orderAttribute.hashCode());
    }

    @Test
    public void testEqualsWithSameValue() {
        OrderAttributeImpl attribute1 = new OrderAttributeImpl();
        OrderAttributeImpl attribute2 = new OrderAttributeImpl();

        attribute1.setValue("sameValue");
        attribute2.setValue("sameValue");

        assertTrue(attribute1.equals(attribute2));
    }

    @Test
    public void testEqualsWithDifferentValue() {
        OrderAttributeImpl attribute1 = new OrderAttributeImpl();
        OrderAttributeImpl attribute2 = new OrderAttributeImpl();

        attribute1.setValue("value1");
        attribute2.setValue("value2");

        assertFalse(attribute1.equals(attribute2));
    }

    @Test
    public void testEqualsWithNull() {
        OrderAttributeImpl attribute1 = new OrderAttributeImpl();
        OrderAttributeImpl attribute2 = new OrderAttributeImpl();

        attribute1.setValue("value");
        attribute2.setValue(null);

        assertFalse(attribute1.equals(attribute2));
    }

    @Test
    public void testEqualsWithSameInstance() {
        OrderAttributeImpl attribute = new OrderAttributeImpl();
        attribute.setValue("value");

        assertTrue(attribute.equals(attribute));
    }

    @Test
    public void testEqualsWithDifferentClass() {
        OrderAttributeImpl attribute = new OrderAttributeImpl();
        attribute.setValue("value");

        assertFalse(attribute.equals(new Object()));
    }

    @Test
    public void testCompleteWorkflow() {
        // Create and set all properties
        orderAttribute.setId(1L);
        orderAttribute.setName("attributeName");
        orderAttribute.setValue("attributeValue");
        orderAttribute.setOrder(order);

        // Verify all properties
        assertEquals(Long.valueOf(1L), orderAttribute.getId());
        assertEquals("attributeName", orderAttribute.getName());
        assertEquals("attributeValue", orderAttribute.getValue());
        assertEquals(order, orderAttribute.getOrder());
        assertEquals("attributeValue", orderAttribute.toString());
        assertEquals("attributeValue".hashCode(), orderAttribute.hashCode());

        // Test clone functionality
        OrderAttributeImpl newAttribute = new OrderAttributeImpl();
        newAttribute.setId(2L);
        newAttribute.setName("attributeName");
        newAttribute.setValue("attributeValue");
        newAttribute.setOrder(order);

        // Test equals with a different instance but same value
        assertTrue(orderAttribute.getValue().equals(newAttribute.getValue()));

        // Test equals with the same instance
        assertTrue(orderAttribute.equals(orderAttribute));

        // Test hashCode consistency
        assertEquals(orderAttribute.getValue().hashCode(), newAttribute.getValue().hashCode());
    }
}