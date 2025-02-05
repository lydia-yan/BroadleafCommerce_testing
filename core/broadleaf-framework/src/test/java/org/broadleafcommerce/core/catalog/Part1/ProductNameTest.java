package org.broadleafcommerce.core.catalog.Part1;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProductNameTest {
    private Product product;
    private Sku defaultSku;

    @Before
    public void setUp() {
        defaultSku = new SkuImpl();

        product = new ProductImpl();
        product.setDefaultSku(defaultSku);
    }

    /**
     * Test Partition: Normal product names
     * Testing regular product names with alphanumeric characters
     */
    @Test
    public void testNormalProductName() {
        String normalName = "iPhone 14 Pro";
        product.setName(normalName);
        assertEquals("Regular product name should be stored correctly",
                normalName, product.getName());
    }

    /**
     * Test Partition: Empty values
     * Testing empty string as product name
     */
    @Test
    public void testEmptyProductName() {
        product.setName("");
        assertEquals("Empty string should be accepted as product name",
                "", product.getName());
    }

    /**
     * Test Partition: Null values
     * Testing null as product name
     */
    @Test
    public void testNullProductName() {
        product.setName(null);
        assertNull("Null should be accepted as product name",
                product.getName());
    }

    /**
     * Test Partition: Boundary values
     * Testing maximum length product name (assuming max length is 255)
     */
    @Test
    public void testLongProductName() {
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 255; i++) {
            longName.append("a");
        }
        product.setName(longName.toString());
        assertEquals("Long product name should be stored correctly",
                longName.toString(), product.getName());
    }

    /**
     * Test Partition: Special characters
     * Testing product name with special characters
     */
    @Test
    public void testSpecialCharactersInName() {
        String specialName = "Product!@#$%^&*()_+-=[]{}|;:,.<>?";
        product.setName(specialName);
        assertEquals("Special characters should be stored correctly",
                specialName, product.getName());
    }

    /**
     * Test Partition 5: Multi-language support
     * Testing product name with mixed languages
     */
    @Test
    public void testMultiLanguageName() {
        String mixedName = "iPhone手机 Pro Max";
        product.setName(mixedName);
        assertEquals("Multi-language name should be stored correctly",
                mixedName, product.getName());
    }

    /**
     * Test Partition: Alphanumeric only
     * Testing product name with only letters and numbers, no spaces or special characters
     */
    @Test
    public void testAlphanumericProductName() {
        String alphanumericName = "iPhone14Pro";
        product.setName(alphanumericName);
        assertEquals("Pure alphanumeric name should be stored correctly",
                alphanumericName, product.getName());
    }
}