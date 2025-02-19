/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.order.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import org.broadleafcommerce.core.order.service.type.OrderItemType;


import static org.junit.Assert.*;

public class GiftWrapOrderItemImplTest {

    private GiftWrapOrderItemImpl giftWrapOrderItem;

    @Before
    public void setUp() {
        giftWrapOrderItem = new GiftWrapOrderItemImpl();
    }

    @Test
    public void testWrappedItems() {
        // Create real wrapped items
        OrderItemImpl item1 = new OrderItemImpl();
        OrderItemImpl item2 = new OrderItemImpl();

        List<OrderItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        // Set wrapped items
        giftWrapOrderItem.setWrappedItems(items);

        // Verify wrapped items
        assertEquals(2, giftWrapOrderItem.getWrappedItems().size());
        assertTrue(giftWrapOrderItem.getWrappedItems().contains(item1));
        assertTrue(giftWrapOrderItem.getWrappedItems().contains(item2));
    }

    @Test
    public void testEquals() {
        GiftWrapOrderItemImpl item1 = new GiftWrapOrderItemImpl();
        GiftWrapOrderItemImpl item2 = new GiftWrapOrderItemImpl();

        // Create identical wrapped items lists
        List<OrderItem> items1 = new ArrayList<>();
        List<OrderItem> items2 = new ArrayList<>();

        OrderItemImpl wrappedItem = new OrderItemImpl();
        wrappedItem.setId(1L);
        items1.add(wrappedItem);
        items2.add(wrappedItem);

        item1.setWrappedItems(items1);
        item2.setWrappedItems(items2);

        // Test equality with identical properties
        item1.setId(1L);
        item2.setId(1L);

        assertTrue(item1.equals(item2));

        // Test inequality with different properties
        item2.setId(2L);
        assertFalse(item1.equals(item2));
    }

    @Test
    public void testHashCode() {
        GiftWrapOrderItemImpl item1 = new GiftWrapOrderItemImpl();
        GiftWrapOrderItemImpl item2 = new GiftWrapOrderItemImpl();

        // Set identical properties
        item1.setId(1L);
        item2.setId(1L);

        List<OrderItem> items = new ArrayList<>();
        OrderItemImpl wrappedItem = new OrderItemImpl();
        wrappedItem.setId(1L);
        items.add(wrappedItem);

        item1.setWrappedItems(items);
        item2.setWrappedItems(items);

        // HashCodes should be equal for identical objects
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    public void testNullWrappedItems() {
        // Test with null wrapped items
        giftWrapOrderItem.setWrappedItems(null);
        assertNull(giftWrapOrderItem.getWrappedItems());

        // Test equals and hashCode with null wrapped items
        GiftWrapOrderItemImpl other = new GiftWrapOrderItemImpl();
        other.setWrappedItems(null);

        assertTrue(giftWrapOrderItem.equals(other));
        assertEquals(giftWrapOrderItem.hashCode(), other.hashCode());
    }
}
