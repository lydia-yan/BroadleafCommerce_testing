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

import org.broadleafcommerce.common.money.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class DiscreteOrderItemFeePriceTest {

    @Test
    public void testSetAndGetId() {
        DiscreteOrderItemFeePriceImpl feePrice = new DiscreteOrderItemFeePriceImpl();
        feePrice.setId(100L);
        assertEquals(100L, feePrice.getId());
    }

    @Test
    public void testSetAndGetName() {
        DiscreteOrderItemFeePriceImpl feePrice = new DiscreteOrderItemFeePriceImpl();
        feePrice.setName("Shipping Fee");
        assertEquals("Shipping Fee", feePrice.getName());
    }

    @Test
    public void testSetAndGetReportingCode() {
        DiscreteOrderItemFeePriceImpl feePrice = new DiscreteOrderItemFeePriceImpl();
        feePrice.setReportingCode("SF-001");
        assertEquals("SF-001", feePrice.getReportingCode());
    }

    @Test
    public void testAmountConversion() {
        DiscreteOrderItemFeePriceImpl feePrice = new DiscreteOrderItemFeePriceImpl();
        Money money = new Money(BigDecimal.valueOf(99.99));
        feePrice.setAmount(money);

        assertNotNull(feePrice.getAmount());
        assertEquals(BigDecimal.valueOf(99.99), feePrice.getAmount().getAmount());
    }

    @Test
    public void testCloneMethod() {
        DiscreteOrderItemFeePriceImpl original = new DiscreteOrderItemFeePriceImpl();
        original.setId(101L);
        original.setName("Handling Fee");
        original.setReportingCode("HF-002");
        original.setAmount(new Money(BigDecimal.valueOf(25.50)));

        DiscreteOrderItemFeePriceImpl cloned = (DiscreteOrderItemFeePriceImpl) original.clone();

        assertNotNull(cloned);
        assertNotSame(original, cloned);
        assertEquals(original.getName(), cloned.getName());
        assertEquals(original.getReportingCode(), cloned.getReportingCode());
        assertEquals(original.getAmount().getAmount(), cloned.getAmount().getAmount());
    }

    @Test
    public void testEqualsAndHashCode() {
        DiscreteOrderItemFeePriceImpl feePrice1 = new DiscreteOrderItemFeePriceImpl();
        feePrice1.setId(102L);
        feePrice1.setName("Gift Wrap Fee");
        feePrice1.setReportingCode("GF-003");
        feePrice1.setAmount(new Money(BigDecimal.valueOf(5.99)));

        DiscreteOrderItemFeePriceImpl feePrice2 = new DiscreteOrderItemFeePriceImpl();
        feePrice2.setId(102L);
        feePrice2.setName("Gift Wrap Fee");
        feePrice2.setReportingCode("GF-003");
        feePrice2.setAmount(new Money(BigDecimal.valueOf(5.99)));

        assertEquals(feePrice1, feePrice2);
        assertEquals(feePrice1.hashCode(), feePrice2.hashCode());
    }
}
