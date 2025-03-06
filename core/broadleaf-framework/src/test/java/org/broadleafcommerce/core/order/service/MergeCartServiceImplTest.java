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
package org.broadleafcommerce.core.order.service;
import static org.junit.jupiter.api.Assertions.*;
import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.order.domain.*;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

class MergeCartServiceImplTest {

    private MergeCartServiceImpl mergeCartService;
    private OrderService orderService;
    private MergeCartServiceExtensionManager extensionManager;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl();
        extensionManager = new MergeCartServiceExtensionManager();

        mergeCartService = new MergeCartServiceImpl("yyyy-MM-dd");

        try {
            Field extensionManagerField = MergeCartServiceImpl.class.getDeclaredField("extensionManager");
            extensionManagerField.setAccessible(true);
            extensionManagerField.set(mergeCartService, extensionManager);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSetSavedCartAttributes() {
        Order cart = new OrderImpl();
        Auditable auditable = new Auditable();
        auditable.setDateUpdated(Date.from(LocalDateTime.of(2025, 2, 24, 10, 30)
                .atZone(ZoneId.systemDefault()).toInstant()));
        cart.setAuditable(auditable);

        //Default format
        mergeCartService = new MergeCartServiceImpl("yyyy-MM-dd");
        mergeCartService.setSavedCartAttributes(cart);
        assertEquals("Previously saved cart - 2025-02-24", cart.getName());

        //Different date format
        mergeCartService = new MergeCartServiceImpl("dd-MM-yyyy");
        mergeCartService.setSavedCartAttributes(cart);
        assertEquals("Previously saved cart - 24-02-2025", cart.getName());

        assertEquals(OrderStatus.NAMED, cart.getStatus());
    }

    @Test
    void testUpdateCartOwnership() {
        Order cart = new OrderImpl();
        Customer customer = new CustomerImpl();
        customer.setEmailAddress("test@example.com");

        mergeCartService.updateCartOwnership(cart, customer);

        assertEquals(customer, cart.getCustomer());
        assertEquals("test@example.com", cart.getEmailAddress());
    }

    @Test
    void testGetItemsToRemove() {
        Order mockOrder = new OrderImpl();
        DiscreteOrderItem validItem = new DiscreteOrderItemImpl();
        DiscreteOrderItem invalidItem = new DiscreteOrderItemImpl();

        Sku validSku = Mockito.mock(Sku.class);
        Sku invalidSku = Mockito.mock(Sku.class);

        Mockito.when(validSku.isActive(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(invalidSku.isActive(Mockito.any(), Mockito.any())).thenReturn(false);

        validItem.setSku(validSku);
        invalidItem.setSku(invalidSku);

        mockOrder.addOrderItem(validItem);
        mockOrder.addOrderItem(invalidItem);

        List<OrderItem> itemsToRemove = mergeCartService.getItemsToRemove(mockOrder);

        assertEquals(1, itemsToRemove.size());
        assertTrue(itemsToRemove.contains(invalidItem));
        assertFalse(itemsToRemove.contains(validItem));
    }

}
