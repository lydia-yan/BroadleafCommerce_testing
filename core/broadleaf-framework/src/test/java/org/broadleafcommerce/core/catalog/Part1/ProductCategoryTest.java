package org.broadleafcommerce.core.catalog.Part1;

import org.broadleafcommerce.core.catalog.domain.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for Product category
 * Using partition testing approach with the following partitions:
 * 1. Category Assignment:
 *    - Default Category: Single category.
 *    - Multiple Categories: List of categories.
 *    - Null Category: Null value.
 *    - Empty Categories: Empty list.
 * 2. Category Hierarchy:
 *    - Single Level: Direct assignment.
 *    - Multi-Level: Parent-child relationship.
 */

public class ProductCategoryTest {
    private Product product;
    private Category defaultCategory;
    private List<CategoryProductXref> categoryXrefs;

    @Before
    public void setUp() {
        product = new ProductImpl();
        defaultCategory = new CategoryImpl();
        categoryXrefs = new ArrayList<>();
    }

    // ===== category assignment Tests =====
    @Test
    public void testDefaultCategory() {
        defaultCategory.setName("Default Category");
        product.setDefaultCategory(defaultCategory);

        assertEquals("Default category should be correctly assigned",
                defaultCategory, product.getDefaultCategory());
    }

    @Test
    public void testMultipleCategories() {
        // Create test categories
        Category category1 = new CategoryImpl();
        category1.setName("Electronics");

        Category category2 = new CategoryImpl();
        category2.setName("Phones");

        // Create CategoryProductXrefs
        CategoryProductXref xref1 = new CategoryProductXrefImpl();
        xref1.setCategory(category1);
        xref1.setProduct(product);

        CategoryProductXref xref2 = new CategoryProductXrefImpl();
        xref2.setCategory(category2);
        xref2.setProduct(product);

        // Add xrefs to list
        categoryXrefs.add(xref1);
        categoryXrefs.add(xref2);

        // Set xrefs to product
        product.setAllParentCategoryXrefs(categoryXrefs);

        assertEquals("Product should have correct number of category xrefs",
                2, product.getAllParentCategoryXrefs().size());
    }

    @Test
    public void testNullCategory() {
        product.setDefaultCategory(null);
        assertNull("Product should allow null default category",
                product.getDefaultCategory());
    }

    @Test
    public void testEmptyCategories() {
        product.setAllParentCategoryXrefs(new ArrayList<>());
        assertTrue("Product should allow empty category xrefs list",
                product.getAllParentCategoryXrefs().isEmpty());
    }

    // ===== category hierarchy Partition Tests =====
    @Test
    public void testCategoryHierarchy() {
        // Create parent category
        Category parentCategory = new CategoryImpl();
        parentCategory.setName("Electronics");

        // Create child category
        Category childCategory = new CategoryImpl();
        childCategory.setName("Phones");
        childCategory.setDefaultParentCategory(parentCategory);

        // Create CategoryProductXref
        CategoryProductXref xref = new CategoryProductXrefImpl();
        xref.setCategory(childCategory);
        xref.setProduct(product);

        List<CategoryProductXref> xrefs = new ArrayList<>();
        xrefs.add(xref);

        // Set as product category
        product.setDefaultCategory(childCategory);
        product.setAllParentCategoryXrefs(xrefs);

        assertEquals("Product should be in child category",
                childCategory, product.getDefaultCategory());
        assertEquals("Child category should have correct parent",
                parentCategory, childCategory.getDefaultParentCategory());
    }
}