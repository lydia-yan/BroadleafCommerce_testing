package org.broadleafcommerce.core.catalog.Part1;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ProductSKUTest {
    private Product product;
    private Sku defaultSku;
    private List<Sku> additionalSkus;

    @Before
    public void setUp() {
        // Initialize product
        product = new ProductImpl();

        // Initialize default SKU
        defaultSku = new SkuImpl();
        product.setDefaultSku(defaultSku);

        // Initialize additional SKUs list
        additionalSkus = new ArrayList<>();
    }

    /**
     * Test default SKU assignment and retrieval
     */
    @Test
    public void testDefaultSku() {
        Sku sku = new SkuImpl();
        sku.setName("Default SKU");
        product.setDefaultSku(sku);

        assertEquals("Default SKU should be retrievable",
                sku, product.getDefaultSku());
    }

    /**
     * Test multiple SKUs management
     */
    @Test
    public void testMultipleSkus() {
        // Create and add multiple SKUs
        Sku sku1 = new SkuImpl();
        sku1.setName("SKU 1");

        Sku sku2 = new SkuImpl();
        sku2.setName("SKU 2");

        additionalSkus.add(sku1);
        additionalSkus.add(sku2);
        product.setAdditionalSkus(additionalSkus);

        assertEquals("Product should have correct number of additional SKUs",
                2, product.getAdditionalSkus().size());
    }

    /**
     * Test null SKU handling
     */
    @Test
    public void testNullSku() {
        product.setDefaultSku(null);
        assertNull("Product should allow null default SKU",
                product.getDefaultSku());
    }

    /**
     * Test empty additional SKUs list
     */
    @Test
    public void testEmptyAdditionalSkus() {
        product.setAdditionalSkus(new ArrayList<>());
        assertTrue("Empty additional SKUs list should be allowed",
                product.getAdditionalSkus().isEmpty());
    }

    /**
     * Test SKU replacement
     */
    @Test
    public void testSkuReplacement() {
        Sku originalSku = new SkuImpl();
        originalSku.setName("Original SKU");
        product.setDefaultSku(originalSku);

        Sku replacementSku = new SkuImpl();
        replacementSku.setName("Replacement SKU");
        product.setDefaultSku(replacementSku);

        assertEquals("SKU should be replaceable",
                replacementSku, product.getDefaultSku());
    }

    /**
     * Test SKU updates
     */
    @Test
    public void testSkuUpdate() {
        Sku sku = new SkuImpl();
        product.setDefaultSku(sku);

        String newName = "Updated SKU";
        sku.setName(newName);

        assertEquals("SKU properties should be updatable",
                newName, product.getDefaultSku().getName());
    }

    /**
     * Test SKU relationships
     */
    @Test
    public void testSkuRelationships() {
        // Create default SKU
        Sku defaultSku = new SkuImpl();
        defaultSku.setName("Default SKU");
        product.setDefaultSku(defaultSku);

        // Create additional SKUs with relationships
        Sku sku1 = new SkuImpl();
        sku1.setName("SKU 1 - Red");

        Sku sku2 = new SkuImpl();
        sku2.setName("SKU 2 - Blue");

        // Add SKUs to product
        List<Sku> additionalSkus = new ArrayList<>();
        additionalSkus.add(sku1);
        additionalSkus.add(sku2);
        product.setAdditionalSkus(additionalSkus);

        // Test relationships
        assertEquals("Default SKU relationship should be maintained",
                defaultSku, product.getDefaultSku());

        assertTrue("Additional SKUs should contain all added SKUs",
                product.getAdditionalSkus().containsAll(additionalSkus));

        assertEquals("Product should have correct number of additional SKUs",
                2, product.getAdditionalSkus().size());
    }

    /**
     * Test SKU cross-relationships
     */
    @Test
    public void testSkuCrossRelationships() {
        // Create SKUs for different variants
        Sku redSmall = new SkuImpl();
        redSmall.setName("Red Small");

        Sku redMedium = new SkuImpl();
        redMedium.setName("Red Medium");

        Sku blueSmall = new SkuImpl();
        blueSmall.setName("Blue Small");

        Sku blueMedium = new SkuImpl();
        blueMedium.setName("Blue Medium");

        // Add SKUs to product
        List<Sku> additionalSkus = new ArrayList<>();
        additionalSkus.add(redSmall);
        additionalSkus.add(redMedium);
        additionalSkus.add(blueSmall);
        additionalSkus.add(blueMedium);

        product.setDefaultSku(redSmall);
        product.setAdditionalSkus(additionalSkus);

        // Verify relationships
        assertEquals("Default SKU should be set correctly",
                redSmall, product.getDefaultSku());

        assertEquals("All variant SKUs should be present",
                4, product.getAdditionalSkus().size());
    }

}