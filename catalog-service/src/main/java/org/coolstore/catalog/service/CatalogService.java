package org.coolstore.catalog.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
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

@ApplicationScoped
public class CatalogService {

    private static final Logger log = Logger.getLogger(CatalogService.class);

    @Inject
    @RestClient
    InventoryService inventoryClient;
    @Inject CatalogRepository repository;
//    @RestClient InventoryService inventoryService;

    public List<Product> getAllProductsWithQuantity() {
        return enrichQuantity(ProductMapper.INSTANCE.fromCatalog(repository.listAll()));
    }

    public Product findById(String itemId) {
        Catalog catalog = repository.findById(itemId);
        if (catalog == null) {
            return null;
        }

        List<Product> products = enrichQuantity(List.of(ProductMapper.INSTANCE.fromCatalog(catalog)));
        return products.get(0);
    }

    public List<Product> search(String keyword) {
        String lowerKeyword = "%" + keyword.toLowerCase() + "%";
        List<Catalog> results = repository.find(
                "LOWER(title) LIKE ?1 OR LOWER(category) LIKE ?1", lowerKeyword).list();
        return enrichQuantity(ProductMapper.INSTANCE.fromCatalog(results));
    }

    public List<Product> getByCategory(String category) {
        List<Catalog> results = repository.find("LOWER(category) = LOWER(?1)", category).list();
        return enrichQuantity(ProductMapper.INSTANCE.fromCatalog(results));
    }

    public List<String> getAllCategories() {
        return repository.find("SELECT DISTINCT c.category FROM Catalog c ORDER BY c.category")
                .project(String.class)
                .list();
    }

    @Transactional
    public Product create(Product product) {
        Catalog catalog = new Catalog();
        catalog.itemId = (product.getItemId() != null && !product.getItemId().isBlank())
                ? product.getItemId()
                : UUID.randomUUID().toString().substring(0, 8);
        catalog.title = product.getTitle();
        catalog.desc = product.getDesc();
        catalog.price = product.getPrice();
        catalog.category = product.getCategory();
        catalog.image = product.getImage();

        repository.persist(catalog);
        product.setItemId(catalog.itemId);
        return product;
    }

    @Transactional
    public Product update(String itemId, Product product) {
        Catalog catalog = repository.findById(itemId);
        if (catalog == null) {
            return null;
        }

        if (product.getTitle() != null) catalog.title = product.getTitle();
        if (product.getDesc() != null) catalog.desc = product.getDesc();
        if (product.getPrice() != null) catalog.price = product.getPrice();
        if (product.getCategory() != null) catalog.category = product.getCategory();
        if (product.getImage() != null) catalog.image = product.getImage();

        return enrichQuantity(List.of(ProductMapper.INSTANCE.fromCatalog(catalog))).get(0);
    }

    @Transactional
    public boolean delete(String itemId) {
        return repository.deleteById(itemId);
    }

//    private List<Product> enrichQuantity(List<Product> products) {
//        products.forEach(p -> {
//            try {
//                Inventory inventory = inventoryService.getByItemId(p.getItemId());
//                p.setQuantity(inventory != null ? inventory.quantity : 0);
//            } catch (Exception e) {
//                log.warnf("Khong lay duoc ton kho cho san pham %s: %s", p.getItemId(), e.getMessage());
//                p.setQuantity(0);
//            }
//        });
//        return products;
//    }



    private List<Product> enrichQuantity(List<Product> products) {
        for (Product p : products) {
            try {
                Inventory inv = inventoryClient.getByItemId(p.getItemId());
                if (inv != null) {
                    p.setQuantity(inv.quantity);
                    // nếu muốn lưu location thì thêm field location vào Product
                } else {
                    p.setQuantity(0);
                }
            } catch (WebApplicationException e) {
                if (e.getResponse().getStatus() == 404) {
                    log.debugf("Không có tồn kho cho sản phẩm %s, fallback = 0", p.getItemId());
                    p.setQuantity(0);
                } else {
                    throw e;
                }
            } catch (Exception ex) {
                log.warnf("Lỗi khi lấy tồn kho cho sản phẩm %s: %s", p.getItemId(), ex.getMessage());
                p.setQuantity(0);
            }
        }
        return products;
    }

}