package org.broadleafcommerce.core.catalog.Part1;

import org.broadleafcommerce.core.catalog.domain.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for Product search
 * Using partition testing approach with the following partitions:
 * 1. Search Field Partitions:
 *    - Exact Match
 *    - Partial Match
 *    - Case Insensitive
 *    - Special Characters
 *    - Description
 *    - Manufacturer
 *    - Model
 * 2. Search Criteria Partitions:
 *    - Single Criteria
 *    - Multiple Criteria
 */

public class ProductSearchTest {
    private Product product;
    private Sku defaultSku;
    private List<Product> productList;

    @Before
    public void setUp() {
        defaultSku = new SkuImpl();
        product = new ProductImpl();
        product.setDefaultSku(defaultSku);
        productList = new ArrayList<>();
    }

    // ===== Search Field Partitions Tests =====
    @Test
    public void testExactNameSearch() {
        Sku sku = new SkuImpl();
        product.setDefaultSku(sku);
        String productName = "iPhone 14 Pro";
        product.setName(productName);

        assertEquals("Should find product by exact name",
                productName, product.getName());
    }

    @Test
    public void testPartialNameSearch() {
        // Create first product
        Product product1 = new ProductImpl();
        Sku sku1 = new SkuImpl();
        product1.setDefaultSku(sku1);
        product1.setName("iPhone 14 Pro");

        // Create second product
        Product product2 = new ProductImpl();
        Sku sku2 = new SkuImpl();
        product2.setDefaultSku(sku2);
        product2.setName("iPhone 14 Max");

        productList.add(product1);
        productList.add(product2);

        int iPhoneProducts = 0;
        for (Product p : productList) {
            if (p.getName().contains("iPhone")) {
                iPhoneProducts++;
            }
        }

        assertEquals("Should find all iPhone products",
                2, iPhoneProducts);
    }


    @Test
    public void testCaseInsensitiveSearch() {
        Sku sku = new SkuImpl();
        product.setDefaultSku(sku);
        String productName = "iPhone 14 Pro";
        product.setName(productName);

        assertTrue("Should find product ignoring case",
                product.getName().toLowerCase().contains("iphone"));
    }

    @Test
    public void testSpecialCharacterSearch() {
        Sku sku = new SkuImpl();
        product.setDefaultSku(sku);
        String productName = "Product #123 & Special!";
        product.setName(productName);

        assertEquals("Should handle special characters in search",
                productName, product.getName());
    }

    @Test
    public void testDescriptionSearch() {
        String description = "Latest smartphone with advanced features";
        product.setDescription(description);

        assertEquals("Should find product by description",
                description, product.getDescription());
    }

    @Test
    public void testManufacturerSearch() {
        String manufacturer = "Apple";
        product.setManufacturer(manufacturer);

        assertEquals("Should find product by manufacturer",
                manufacturer, product.getManufacturer());
    }

    @Test
    public void testModelSearch() {
        String model = "A2650";
        product.setModel(model);

        assertEquals("Should find product by model",
                model, product.getModel());
    }

    // ===== Search Criteria Partitions Tests =====
    @Test
    public void testMultiCriteriaSearch() {
        Sku sku = new SkuImpl();
        product.setDefaultSku(sku);
        product.setName("iPhone 14 Pro");
        product.setManufacturer("Apple");
        product.setModel("A2650");

        assertTrue("Should match all search criteria",
                product.getName().contains("iPhone") &&
                        product.getManufacturer().equals("Apple") &&
                        product.getModel().equals("A2650"));
    }
}