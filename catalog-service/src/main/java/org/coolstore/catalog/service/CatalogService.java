package org.coolstore.catalog.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.coolstore.catalog.ProductMapper;
import org.coolstore.catalog.entity.Catalog;
import org.coolstore.catalog.model.Inventory;
import org.coolstore.catalog.model.Product;
import org.coolstore.catalog.repository.CatalogRepository;
import org.coolstore.catalog.resource.client.InventoryService;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

/**
 * Service xử lý nghiệp vụ danh mục sản phẩm (NÂNG CẤP).
 * Thêm: create, update, delete, search, getByCategory
 */
@ApplicationScoped
public class CatalogService {

    private static final Logger log = Logger.getLogger(CatalogService.class);

    @Inject CatalogRepository repository;
    @RestClient InventoryService inventoryService;

    // ── Đọc dữ liệu ─────────────────────────────────────────────

    /** Lấy tất cả sản phẩm kèm số lượng tồn kho */
    public List<Product> getAllProductsWithQuantity() {
        List<Product> products = ProductMapper.INSTANCE.fromCatalog(repository.listAll());
        products.forEach(p -> {
            try {
                p.setQuantity(inventoryService.getByItemId(p.getItemId()).quantity);
            } catch (Exception e) {
                log.warnf("Không lấy được tồn kho cho sản phẩm %s: %s", p.getItemId(), e.getMessage());
                p.setQuantity(0);
            }
        });
        return products;
    }

    public Product findById(String itemId) {
        Catalog catalog = repository.findById(itemId);
        if (catalog == null) return null;

        Product product = ProductMapper.INSTANCE.fromCatalog(catalog);
        try {
            Inventory inventory = inventoryService.getByItemId(catalog.itemId);
            if (inventory != null) product.setQuantity(inventory.quantity);
        } catch (Exception e) {
            product.setQuantity(0);
        }
        return product;
    }

    /** Tìm kiếm theo tên sản phẩm (không phân biệt hoa thường) */
    public List<Product> search(String keyword) {
        String lowerKeyword = "%" + keyword.toLowerCase() + "%";
        List<Catalog> results = repository.find(
                "LOWER(title) LIKE ?1 OR LOWER(category) LIKE ?1", lowerKeyword).list();
        return ProductMapper.INSTANCE.fromCatalog(results);
    }

    /** Lấy sản phẩm theo danh mục */
    public List<Product> getByCategory(String category) {
        List<Catalog> results = repository.find("LOWER(category) = LOWER(?1)", category).list();
        return ProductMapper.INSTANCE.fromCatalog(results);
    }

    /** Lấy danh sách tất cả danh mục (distinct) */
    public List<String> getAllCategories() {
        return repository.find("SELECT DISTINCT c.category FROM Catalog c ORDER BY c.category")
                .project(String.class)
                .list();
    }

    // ── Ghi dữ liệu (chỉ Admin) ─────────────────────────────────

    /** Tạo sản phẩm mới */
    @Transactional
    public Product create(Product product) {
        Catalog catalog = new Catalog();
        // Tự tạo itemId nếu không có
        catalog.itemId  = (product.getItemId() != null && !product.getItemId().isBlank())
                ? product.getItemId()
                : UUID.randomUUID().toString().substring(0, 8);
        catalog.title    = product.getTitle();
        catalog.desc     = product.getDesc();
        catalog.price    = product.getPrice();
        catalog.category = product.getCategory();
        catalog.image    = product.getImage();

        repository.persist(catalog);
        product.setItemId(catalog.itemId);
        return product;
    }

    /** Cập nhật sản phẩm */
    @Transactional
    public Product update(String itemId, Product product) {
        Catalog catalog = repository.findById(itemId);
        if (catalog == null) return null;

        if (product.getTitle()    != null) catalog.title    = product.getTitle();
        if (product.getDesc()     != null) catalog.desc     = product.getDesc();
        if (product.getPrice()    != null) catalog.price    = product.getPrice();
        if (product.getCategory() != null) catalog.category = product.getCategory();
        if (product.getImage()    != null) catalog.image    = product.getImage();

        return ProductMapper.INSTANCE.fromCatalog(catalog);
    }

    /** Xóa sản phẩm */
    @Transactional
    public boolean delete(String itemId) {
        return repository.deleteById(itemId);
    }
}