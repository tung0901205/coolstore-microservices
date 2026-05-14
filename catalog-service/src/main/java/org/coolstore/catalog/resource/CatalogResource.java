package org.coolstore.catalog.resource;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.coolstore.catalog.model.Product;
import org.coolstore.catalog.service.CatalogService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * REST API danh mục sản phẩm (NÂNG CẤP với JWT).
 *
 * Endpoints CÔNG KHAI (không cần đăng nhập):
 *   GET /api/products        - Lấy tất cả sản phẩm
 *   GET /api/products/{id}   - Xem chi tiết sản phẩm
 *   GET /api/products/search - Tìm kiếm
 *
 * Endpoints CHỈ ADMIN:
 *   POST   /api/products           - Thêm sản phẩm mới
 *   PUT    /api/products/{id}      - Sửa sản phẩm
 *   DELETE /api/products/{id}      - Xóa sản phẩm
 */
@Path("/api/products")
@Tag(name = "Sản Phẩm", description = "Quản lý danh mục sản phẩm CoolStore")
@Produces(APPLICATION_JSON)
public class CatalogResource {

    @Inject CatalogService service;
    private static final Logger log = Logger.getLogger(CatalogResource.class);

    // ── Endpoints công khai ──────────────────────────────────────

    @GET
    @Operation(summary = "Lấy tất cả sản phẩm (có số lượng tồn kho)")
    public Response getAll(@QueryParam("category") String category,
                           @QueryParam("search") String search) {
        if (category != null && !category.isBlank()) {
            return Response.ok(service.getByCategory(category)).build();
        }
        if (search != null && !search.isBlank()) {
            return Response.ok(service.search(search)).build();
        }
        return Response.ok(service.getAllProductsWithQuantity()).build();
    }

    @GET
    @Path("/{itemId}")
    @Operation(summary = "Xem chi tiết một sản phẩm")
    public Response getOne(@PathParam("itemId") String itemId) {
        Product product = service.findById(itemId);
        if (product == null) {
            log.debugf("Sản phẩm không tồn tại: %s", itemId);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Không tìm thấy sản phẩm: " + itemId))
                    .build();
        }
        return Response.ok(product).build();
    }

    @GET
    @Path("/categories")
    @Operation(summary = "Lấy danh sách tất cả danh mục")
    public Response getCategories() {
        return Response.ok(service.getAllCategories()).build();
    }

    // ── Endpoints chỉ ADMIN ──────────────────────────────────────

    @POST
    @Consumes(APPLICATION_JSON)
    @Transactional
    @RolesAllowed("ADMIN")
    @Operation(summary = "[Admin] Thêm sản phẩm mới")
    public Response create(@Valid Product product) {
        try {
            Product created = service.create(product);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (PersistenceException pe) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Lỗi tạo sản phẩm: " + pe.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{itemId}")
    @Consumes(APPLICATION_JSON)
    @Transactional
    @RolesAllowed("ADMIN")
    @Operation(summary = "[Admin] Cập nhật thông tin sản phẩm")
    public Response update(@Valid Product product, @PathParam("itemId") String itemId) {
        Product updated = service.update(itemId, product);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Sản phẩm không tồn tại: " + itemId))
                    .build();
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{itemId}")
    @Transactional
    @RolesAllowed("ADMIN")
    @Operation(summary = "[Admin] Xóa sản phẩm")
    public Response delete(@PathParam("itemId") String itemId) {
        boolean deleted = service.delete(itemId);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Sản phẩm không tồn tại: " + itemId))
                    .build();
        }
        return Response.noContent().build();
    }

    // ── DTO lỗi ─────────────────────────────────────────────────
    public record ErrorResponse(String thongBao) {}
}