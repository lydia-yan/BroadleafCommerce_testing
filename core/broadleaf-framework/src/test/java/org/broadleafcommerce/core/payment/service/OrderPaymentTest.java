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
package org.broadleafcommerce.core.payment.service;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrencyImpl;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.OrderPaymentImpl;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.payment.domain.PaymentTransactionImpl;
import org.broadleafcommerce.core.payment.service.type.OrderPaymentStatus;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class OrderPaymentTest {

    private OrderPayment orderPayment;
    private OrderPaymentStatusService statusService;

    @Before
    public void setUp() {
        // Initialize currency
        BroadleafCurrency currency = new BroadleafCurrencyImpl();
        currency.setCurrencyCode("USD");

        // Initialize order
        Order order = new OrderImpl();
        order.setCurrency(currency);

        // Initialize payment
        orderPayment = new OrderPaymentImpl();
        orderPayment.setOrder(order);
        orderPayment.setAmount(new Money(BigDecimal.TEN));

        // Initialize service
        statusService = new OrderPaymentStatusServiceImpl();
    }

    // UNDETERMINED State Tests
    @Test
    public void testUndeterminedWithEmptyTransactions() {
        assertEquals(OrderPaymentStatus.UNDETERMINED, getCurrentStatus());
    }

    @Test
    public void testUndeterminedWithNullTransaction() {
        try {
            orderPayment.addTransaction(null);
            OrderPaymentStatus status = statusService.determineOrderPaymentStatus(orderPayment);
            assertEquals(OrderPaymentStatus.UNDETERMINED, status);
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testUndeterminedWithFailedTransaction() {
        addTransaction(PaymentTransactionType.AUTHORIZE, false);
        assertEquals(OrderPaymentStatus.UNDETERMINED, getCurrentStatus());
    }

    // Undetermined to Fully_Capture in one step
    @Test
    public void testAuthorizeAndCaptureInOneStep() {
        addTransaction(PaymentTransactionType.AUTHORIZE_AND_CAPTURE, true);
        assertEquals(OrderPaymentStatus.FULLY_CAPTURED, getCurrentStatus());
    }

    //UNDETERMINED to COMPLETE by DETACHED_CREDIT(refund of non-payment transaction)
    @Test
    public void testDetachedCredit() {
        addTransactionWithAmount(PaymentTransactionType.DETACHED_CREDIT, new BigDecimal("10.00"));
        assertEquals(OrderPaymentStatus.COMPLETE, getCurrentStatus());
    }

    // UNCONFIRMED State Tests
    @Test
    public void testSingleSuccessfulUnconfirmedTransaction() {
        addTransaction(PaymentTransactionType.UNCONFIRMED, true);
        assertEquals(OrderPaymentStatus.UNCONFIRMED, getCurrentStatus());
    }

    @Test
    public void testUnconfirmedStateRequiresExactlyOneTransaction() {
        addTransaction(PaymentTransactionType.UNCONFIRMED, true);
        addTransaction(PaymentTransactionType.UNCONFIRMED, true);
        assertEquals(OrderPaymentStatus.UNDETERMINED, getCurrentStatus());
    }

    @Test
    public void testUnconfirmedWithMixedTransactions() {
        addTransaction(PaymentTransactionType.UNCONFIRMED, true);
        addTransaction(PaymentTransactionType.PENDING, false);
        assertEquals(OrderPaymentStatus.UNCONFIRMED, getCurrentStatus());
    }

    @Test
    public void testUnconfirmedFailedThenSuccess() {
        addTransaction(PaymentTransactionType.UNCONFIRMED, false);
        addTransaction(PaymentTransactionType.UNCONFIRMED, true);
        assertEquals(OrderPaymentStatus.PENDING, getCurrentStatus());
    }

    // PENDING State Tests
    @Test
    public void testPendingState() {
        addTransaction(PaymentTransactionType.PENDING, true);
        assertEquals(OrderPaymentStatus.PENDING, getCurrentStatus());
    }

    @Test
    public void testPendingNotAchievedWithAuthorize() {
        addTransaction(PaymentTransactionType.PENDING, true);
        addTransaction(PaymentTransactionType.AUTHORIZE, true);
        assertEquals(OrderPaymentStatus.AUTHORIZED, getCurrentStatus());
    }

    @Test
    public void testPendingWithMultipleSuccessfulPending() {
        addTransaction(PaymentTransactionType.PENDING, true);
        addTransaction(PaymentTransactionType.PENDING, true);
        assertEquals(OrderPaymentStatus.PENDING, getCurrentStatus());
    }

    @Test
    public void testPendingWithFailedAuthorize() {
        addTransaction(PaymentTransactionType.PENDING, true);
        addTransaction(PaymentTransactionType.AUTHORIZE, false);
        assertEquals(OrderPaymentStatus.PENDING, getCurrentStatus());
    }

    // AUTHORIZED State Tests
    @Test
    public void testAuthorizedState() {
        addTransaction(PaymentTransactionType.AUTHORIZE, true);
        assertEquals(OrderPaymentStatus.AUTHORIZED, getCurrentStatus());
    }

    @Test
    public void testAuthorizedWithPreviousFailedAttempts() {
        addTransaction(PaymentTransactionType.AUTHORIZE, false);
        addTransaction(PaymentTransactionType.AUTHORIZE, false);
        addTransaction(PaymentTransactionType.AUTHORIZE, true);
        assertEquals(OrderPaymentStatus.AUTHORIZED, getCurrentStatus());
    }

    @Test
    public void testAuthorizedWithMixedHistory() {
        addTransaction(PaymentTransactionType.UNCONFIRMED, true);
        addTransaction(PaymentTransactionType.PENDING, true);
        addTransaction(PaymentTransactionType.AUTHORIZE, true);
        assertEquals(OrderPaymentStatus.AUTHORIZED, getCurrentStatus());
    }

    // Complex Scenarios Tests
    @Test
    public void testInvalidTransactionSequence() {
        addTransaction(PaymentTransactionType.AUTHORIZE, true);
        addTransaction(PaymentTransactionType.UNCONFIRMED, true);
        assertEquals(OrderPaymentStatus.AUTHORIZED, getCurrentStatus());
    }

    // Amount Validation Tests
    @Test
    public void testZeroAmountTransaction() {
        addTransactionWithAmount(PaymentTransactionType.UNCONFIRMED, BigDecimal.ZERO);
        assertEquals(OrderPaymentStatus.UNDETERMINED, getCurrentStatus());
    }

    @Test
    public void testNegativeAmountTransaction() {
        addTransactionWithAmount(PaymentTransactionType.UNCONFIRMED, new BigDecimal("-10.00"));
        assertEquals(OrderPaymentStatus.UNDETERMINED, getCurrentStatus());
    }

    @Test
    public void testAmountMismatchTransaction() {
        addTransactionWithAmount(PaymentTransactionType.UNCONFIRMED, new BigDecimal("5.00"));
        assertEquals(OrderPaymentStatus.UNDETERMINED, getCurrentStatus());
    }

    //AUTHORIZED to COMPLETE
    @Test
    public void testAuthorizedToCompleteWithVoid() {
        addTransaction(PaymentTransactionType.AUTHORIZE, true);
        addTransactionWithAmount(PaymentTransactionType.VOID, new BigDecimal("10.00"));
        assertEquals(OrderPaymentStatus.COMPLETE.getType(), getCurrentStatus().getType());
    }

    //AUTHORIZED to FULLY_CAPTURE
    @Test
    public void testFullyCapturedState() {
        addTransaction(PaymentTransactionType.AUTHORIZE, true);
        addTransactionWithAmount(PaymentTransactionType.CAPTURE, new BigDecimal("10.00"));
        assertEquals(OrderPaymentStatus.FULLY_CAPTURED, getCurrentStatus());
    }

    //AUTHORIZED to PARTIALLY_COMPLETE
    @Test
    public void testPartiallyCapturedState() {
        addTransaction(PaymentTransactionType.AUTHORIZE, true);
        addTransactionWithAmount(PaymentTransactionType.CAPTURE, new BigDecimal("5.00"));
        assertEquals(OrderPaymentStatus.PARTIALLY_COMPLETE, getCurrentStatus());
    }

    //PARTIALLY_COMPLETE to FULLY_CAPTURED
    @Test
    public void testMultiplePartialCapturesToFull() {
        addTransaction(PaymentTransactionType.AUTHORIZE, true);
        addTransactionWithAmount(PaymentTransactionType.CAPTURE, new BigDecimal("5.00"));
        addTransactionWithAmount(PaymentTransactionType.CAPTURE, new BigDecimal("5.00"));
        assertEquals(OrderPaymentStatus.FULLY_CAPTURED, getCurrentStatus());
    }

    //PARTIALLY_COMPLETE then partial refund
    @Test
    public void testPartialPaymentThenPartialRefund() {
        addTransaction(PaymentTransactionType.AUTHORIZE, true);
        addTransactionWithAmount(PaymentTransactionType.CAPTURE, new BigDecimal("5.00"));
        addTransactionWithAmount(PaymentTransactionType.REFUND, new BigDecimal("2.50"));
        System.out.println("Final Status: " + getCurrentStatus().getType());
        assertEquals(OrderPaymentStatus.PARTIALLY_COMPLETE, getCurrentStatus());
    }

    //PARTIALLY_COMPLETE then full refund
    @Test
    public void testPartialPaymentThenRefund() {
        addTransaction(PaymentTransactionType.AUTHORIZE, true);
        addTransactionWithAmount(PaymentTransactionType.CAPTURE, new BigDecimal("5.00"));
        addTransaction(PaymentTransactionType.REFUND,true);
        System.out.println("Final Status: " + getCurrentStatus().getType());
        assertEquals(OrderPaymentStatus.COMPLETE, getCurrentStatus());
    }

    //FULLY_CAPTURED and partial refund
    @Test
    public void testPartialRefund() {
        addTransaction(PaymentTransactionType.AUTHORIZE, true);
        addTransactionWithAmount(PaymentTransactionType.CAPTURE, new BigDecimal("10.00"));
        addTransactionWithAmount(PaymentTransactionType.REFUND, new BigDecimal("5.00"));
        System.out.println("Final Status: " + getCurrentStatus().getType());
        //Predict: PARTIALLY_COMPLETE
        assertEquals(OrderPaymentStatus.PARTIALLY_COMPLETE, getCurrentStatus());
    }

    //FULLY_CAPTURED to COMPLETE by full refund
    @Test
    public void testRefundAfterFullCapture() {
        addTransaction(PaymentTransactionType.AUTHORIZE, true);
        addTransactionWithAmount(PaymentTransactionType.CAPTURE, new BigDecimal("10.00"));
        addTransaction(PaymentTransactionType.REFUND, true);
        assertEquals(OrderPaymentStatus.COMPLETE, getCurrentStatus());
    }

    //FULLY_CAPTURED to COMPLETE by void
    @Test
    public void testVoidAfterFullCapture() {
        addTransaction(PaymentTransactionType.AUTHORIZE, true);
        addTransactionWithAmount(PaymentTransactionType.CAPTURE, new BigDecimal("10.00"));
        addTransaction(PaymentTransactionType.VOID, true);
        assertEquals(OrderPaymentStatus.COMPLETE, getCurrentStatus());
    }

    //Invalid Transaction After COMPLETE
    @Test
    public void testInvalidTransactionAfterComplete() {
        addTransaction(PaymentTransactionType.AUTHORIZE, true);
        addTransactionWithAmount(PaymentTransactionType.CAPTURE, new BigDecimal("10.00"));
        addTransactionWithAmount(PaymentTransactionType.REFUND, new BigDecimal("10.00"));
        System.out.println("Final Status: " + getCurrentStatus().getType());
        addTransaction(PaymentTransactionType.AUTHORIZE, true);
        System.out.println("Final Status: " + getCurrentStatus().getType());
        assertEquals(OrderPaymentStatus.COMPLETE, getCurrentStatus());
    }

    // Helper Methods
    private OrderPaymentStatus getCurrentStatus() {
        return statusService.determineOrderPaymentStatus(orderPayment);
    }

    private void addTransaction(PaymentTransactionType type, boolean success) {
        PaymentTransaction tx = new PaymentTransactionImpl();
        tx.setOrderPayment(orderPayment);
        tx.setType(type);
        tx.setSuccess(success);
        tx.setAmount(new Money(BigDecimal.TEN));
        orderPayment.addTransaction(tx);
    }

    private void addTransactionWithAmount(PaymentTransactionType type, BigDecimal amount) {
        PaymentTransaction tx = new PaymentTransactionImpl();
        tx.setOrderPayment(orderPayment);
        tx.setType(type);
        tx.setSuccess(true);
        tx.setAmount(new Money(amount));
        orderPayment.addTransaction(tx);
    }
}
