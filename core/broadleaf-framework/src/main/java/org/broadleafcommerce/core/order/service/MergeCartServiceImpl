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

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.core.order.domain.*;
import org.broadleafcommerce.core.order.service.call.MergeCartResponse;
import org.broadleafcommerce.core.order.service.call.ReconstructCartResponse;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

@Service("blMergeCartService")
public class MergeCartServiceImpl implements MergeCartService {

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;

    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;

    @Resource(name = "blMergeCartServiceExtensionManager")
    protected MergeCartServiceExtensionManager extensionManager;

    private final DateTimeFormatter dateFormatter;

    @Autowired
    // Inject date format through the constructor to avoid hard coding SimpleDateFormat
    public MergeCartServiceImpl(@Value("${cart.date.format:MMM dd, ''yy}") String dateFormat) {
        this.dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
    }

    @Override
    public MergeCartResponse mergeCart(Customer customer, Order anonymousCart)
            throws PricingException, RemoveFromCartException {
        return mergeCart(customer, anonymousCart, true);
    }

    @Override
    public ReconstructCartResponse reconstructCart(Customer customer) throws PricingException, RemoveFromCartException {
        return reconstructCart(customer, true);
    }

    @Override
    public MergeCartResponse mergeCart(Customer customer, Order anonymousCart, boolean priceOrder)
            throws PricingException, RemoveFromCartException {
        MergeCartResponse mergeCartResponse = new MergeCartResponse();
        mergeCartResponse.setMerged(false);

        ReconstructCartResponse reconstructCartResponse = reconstructCart(customer, false);
        mergeCartResponse.setRemovedItems(reconstructCartResponse.getRemovedItems());
        Order customerCart = reconstructCartResponse.getOrder();

        if (mergeCartResponse.getOrder() != null) {
            Order savedCart = orderService.save(mergeCartResponse.getOrder(), priceOrder, priceOrder);
            mergeCartResponse.setOrder(savedCart);
        }

        return mergeCartResponse;
    }

    @Override
    public ReconstructCartResponse reconstructCart(Customer customer, boolean priceOrder) throws PricingException, RemoveFromCartException {
        ReconstructCartResponse reconstructCartResponse = new ReconstructCartResponse();
        Order customerCart = orderService.findCartForCustomerWithEnhancements(customer);
        if (customerCart != null) {
            List<OrderItem> itemsToRemove = getItemsToRemove(customerCart);

            for (OrderItem item : itemsToRemove) {
                orderService.removeItem(customerCart.getId(), item.getId(), false);
            }

            reconstructCartResponse.setRemovedItems(itemsToRemove);
            customerCart = orderService.save(customerCart, priceOrder);
        }

        reconstructCartResponse.setOrder(customerCart);
        return reconstructCartResponse;
    }

    // Add public method updateCartOwnership to test setNewCartOwnership method
    public void updateCartOwnership(Order cart, Customer customer) {
        setNewCartOwnership(cart, customer);
    }

    // Allow calling through updateCartOwnership method for easy testing
    protected void setNewCartOwnership(Order cart, Customer customer) {
        cart.setCustomer(customer);

        if (cart != null && StringUtils.isNotBlank(customer.getEmailAddress())) {
            cart.setEmailAddress(customer.getEmailAddress());
        }

        extensionManager.getProxy().setNewCartOwnership(cart, customer);
    }

    // Split the original complex logic and handle whether the OrderItem needs to be deleted separately for easy testing
    public List<OrderItem> getItemsToRemove(Order customerCart) {
        List<OrderItem> itemsToRemove = new ArrayList<>();
        for (OrderItem orderItem : customerCart.getOrderItems()) {
            if (!isItemValid(orderItem)) {
                itemsToRemove.add(orderItem);
            }
        }
        return itemsToRemove;
    }

    // Split the OrderItem validation logic into independent methods for easy individual testing
    public boolean isItemValid(OrderItem orderItem) {
        if (orderItem instanceof DiscreteOrderItem) {
            DiscreteOrderItem doi = (DiscreteOrderItem) orderItem;
            return checkActive(doi) && checkInventory(doi) && checkOtherValidity(orderItem);
        } else if (orderItem instanceof BundleOrderItem) {
            BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItem;
            for (DiscreteOrderItem doi : bundleOrderItem.getDiscreteOrderItems()) {
                if (!checkActive(doi) || !checkInventory(doi) || !checkOtherValidity(orderItem)) {
                    return false;
                }
            }
        }
        return true;
    }

    // Replace SimpleDateFormat with DateTimeFormatter to avoid thread safety issues
    protected void setSavedCartAttributes(Order cart) {
        LocalDateTime cartLastUpdated = LocalDateTime.ofInstant(cart.getAuditable().getDateUpdated().toInstant(), ZoneId.systemDefault());
        cart.setName("Previously saved cart - " + dateFormatter.format(cartLastUpdated));
        cart.setStatus(OrderStatus.NAMED);
    }

    protected boolean checkActive(DiscreteOrderItem orderItem) {
        return orderItem.getSku().isActive(orderItem.getProduct(), orderItem.getCategory());
    }

    protected boolean checkInventory(DiscreteOrderItem orderItem) {
        return true;
    }

    protected boolean checkOtherValidity(OrderItem orderItem) {
        return true;
    }
}
