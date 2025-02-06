package org.broadleafcommerce.core.catalog.Part1;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for Product pricing
 * Using partition testing approach with following partitions:
 * 1. Retail Price Partitions:
 *    - Normal price (> 0)
 *    - Zero price (= 0)
 *    - Negative price (< 0)
 *    - Null price (not set)
 * 2. Sale Price Partitions:
 *    - No sale price (null)
 *    - Sale price < Retail price (normal case)
 *    - Sale price = Retail price
 *    - Sale price > Retail price
 *    - Sale price becomes null
 * 3. Boundary Value Analysis:
 *    - Minimum legal price (0.01)
 *    - Maximum legal price (system maximum)
 *    - Exceeding maximum price
 *    - Normal price range boundaries
 */
public class ProductPriceTest {

    private Product product;
    private Sku defaultSku;

    @Before
    public void setUp() {
        product = new ProductImpl();
        defaultSku = new SkuImpl();
        ((ProductImpl) product).setDefaultSku(defaultSku);
    }

    // ===== Retail Price Partition Tests =====
    @Test
    public void testNormalRetailPrice() {
        Money retailPrice = new Money("99.99");
        defaultSku.setRetailPrice(retailPrice);
        assertEquals(retailPrice, product.getRetailPrice());
    }

    @Test
    public void testZeroRetailPrice() {
        Money zeroPrice = new Money("0.00");
        defaultSku.setRetailPrice(zeroPrice);
        assertEquals(zeroPrice, product.getRetailPrice());
    }

    @Test
    public void testNegativeRetailPrice() {
        Money negativePrice = new Money("-10.00");
        defaultSku.setRetailPrice(negativePrice);
        assertEquals(negativePrice, product.getRetailPrice());
        // Note: In real system, negative prices might need to be prevented
    }

    @Test
    public void testNullRetailPrice() {
        defaultSku.setRetailPrice(null);
        assertNull(product.getRetailPrice());
    }

    // ===== Sale Price Partition Tests =====
    @Test
    public void testNoSalePrice() {
        Money retailPrice = new Money("100.00");
        defaultSku.setRetailPrice(retailPrice);
        defaultSku.setSalePrice(null);
        assertFalse(product.isOnSale());
        assertEquals(retailPrice, product.getPrice()); // getPrice should return retail price
    }

    @Test
    public void testNormalSalePrice() {
        Money retailPrice = new Money("100.00");
        Money salePrice = new Money("80.00");
        defaultSku.setRetailPrice(retailPrice);
        defaultSku.setSalePrice(salePrice);
        assertTrue(product.isOnSale());
        assertEquals(salePrice, product.getPrice()); // getPrice should return sale price
    }

    @Test
    public void testEqualSalePrice() {
        Money price = new Money("100.00");
        defaultSku.setRetailPrice(price);
        defaultSku.setSalePrice(price);
        assertFalse(product.isOnSale());
        assertEquals(price, product.getPrice());
    }

    @Test
    public void testHigherSalePrice() {
        Money retailPrice = new Money("100.00");
        Money salePrice = new Money("120.00");
        defaultSku.setRetailPrice(retailPrice);
        defaultSku.setSalePrice(salePrice);
        assertFalse(product.isOnSale());
        assertEquals(retailPrice, product.getPrice());
    }

    @Test
    public void testSalePriceBecomesNull() {
        // Test when sale price is set, then removed
        Money retailPrice = new Money("100.00");
        Money salePrice = new Money("80.00");

        defaultSku.setRetailPrice(retailPrice);
        defaultSku.setSalePrice(salePrice);
        assertTrue("Product should be on sale", product.isOnSale());
        assertEquals("Sale price should be applied", salePrice, product.getPrice());

        // Remove sale price
        defaultSku.setSalePrice(null);
        assertFalse("Product should no longer be on sale", product.isOnSale());
        assertEquals("Retail price should be restored", retailPrice, product.getPrice());
    }

    // ===== Boundary Value Tests =====
    @Test
    public void testMinimumLegalPrice() {
        Money minPrice = new Money("0.01");  // Minimum legal price
        defaultSku.setRetailPrice(minPrice);
        assertEquals(minPrice, product.getRetailPrice());
    }

    @Test
    public void testMaximumLegalPrice() {
        // Assuming system maximum price is 9999999.99
        Money maxPrice = new Money("9999999.99");  // Maximum legal price
        defaultSku.setRetailPrice(maxPrice);
        assertEquals(maxPrice, product.getRetailPrice());
    }

    @Test
    public void testExceedingMaximumPrice() {
        // Assuming system max price is 9999999.99, test exceeding it
        Money tooHighPrice = new Money("10000000.00"); // Exceeding limit
        defaultSku.setRetailPrice(tooHighPrice);
        assertEquals("System allows exceeding maximum price storage?",
                tooHighPrice, product.getRetailPrice());
    }

    @Test
    public void testNormalPriceRangeLowerBound() {
        // Testing lower bound of normal price range
        Money lowerNormalPrice = new Money("1.00");
        defaultSku.setRetailPrice(lowerNormalPrice);
        assertEquals(lowerNormalPrice, product.getRetailPrice());
    }

    @Test
    public void testNormalPriceRangeMiddle() {
        // Testing middle value of normal price range
        Money normalPrice = new Money("99.99");
        defaultSku.setRetailPrice(normalPrice);
        assertEquals(normalPrice, product.getRetailPrice());
    }

    @Test
    public void testNormalPriceRangeUpperBound() {
        // Testing upper bound of normal price range
        Money upperNormalPrice = new Money("999.99");
        defaultSku.setRetailPrice(upperNormalPrice);
        assertEquals(upperNormalPrice, product.getRetailPrice());
    }
}