package org.coolstore.inventory.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.coolstore.inventory.entity.Inventory;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * REST API quản lý tồn kho (NÂNG CẤP).
 *
 * GET /api/inventory          - Lấy tất cả (public - catalog-service dùng)
 * GET /api/inventory/{itemId} - Lấy theo itemId (public)
 * PUT /api/inventory/{itemId} - Cập nhật số lượng (chỉ Admin)
 * POST /api/inventory         - Thêm mới (chỉ Admin)
 */
@Path("/api/inventory")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Tồn Kho", description = "Quản lý số lượng tồn kho sản phẩm")
public class InventoryResource {

    // ── Public endpoints (catalog-service gọi nội bộ) ───────────

    @GET
    @Path("/{itemId}")
    @Operation(summary = "Lấy tồn kho theo itemId")
    public Inventory getByItemId(@PathParam("itemId") String itemId) {
        Inventory inv = Inventory.find("itemId", itemId).firstResult();
        if (inv == null) {
            // Trả về 0 thay vì 404 để không làm hỏng catalog response
            Inventory empty = new Inventory();
            empty.itemId   = itemId;
            empty.quantity = 0;
            empty.location = "Không xác định";
            return empty;
        }
        return inv;
    }

    @GET
    @Operation(summary = "Lấy toàn bộ tồn kho")
    public Response getAll() {
        return Response.ok(Inventory.listAll()).build();
    }

    // ── Admin endpoints ──────────────────────────────────────────

    @POST
    @Transactional
    @RolesAllowed("ADMIN")
    @Operation(summary = "[Admin] Thêm bản ghi tồn kho mới")
    public Response create(Inventory inventory) {
        // Kiểm tra đã tồn tại chưa
        long existing = Inventory.count("itemId", inventory.itemId);
        if (existing > 0) {
            return Response.status(409)
                    .entity(new ErrorMsg("Tồn kho cho sản phẩm " + inventory.itemId + " đã tồn tại."))
                    .build();
        }
        inventory.persist();
        return Response.status(201).entity(inventory).build();
    }

    @PUT
    @Path("/{itemId}")
    @Transactional
    @RolesAllowed("ADMIN")
    @Operation(summary = "[Admin] Cập nhật số lượng tồn kho")
    public Response update(@PathParam("itemId") String itemId, CapNhatTonKho request) {
        Inventory inv = Inventory.find("itemId", itemId).firstResult();
        if (inv == null) {
            return Response.status(404)
                    .entity(new ErrorMsg("Không tìm thấy tồn kho cho sản phẩm: " + itemId))
                    .build();
        }
        if (request.soLuong() < 0) {
            return Response.status(400)
                    .entity(new ErrorMsg("Số lượng không được âm."))
                    .build();
        }
        inv.quantity = request.soLuong();
        if (request.location() != null && !request.location().isBlank()) {
            inv.location = request.location();
        }
        return Response.ok(inv).build();
    }

    @PATCH
    @Path("/{itemId}/giam")
    @Transactional
    @Operation(summary = "Giảm số lượng tồn kho (dùng khi đặt hàng)")
    public Response giamSoLuong(@PathParam("itemId") String itemId,
                                @QueryParam("soLuong") int soLuong) {
        Inventory inv = Inventory.find("itemId", itemId).firstResult();
        if (inv == null) {
            return Response.status(404)
                    .entity(new ErrorMsg("Không tìm thấy tồn kho cho sản phẩm: " + itemId))
                    .build();
        }
        if (inv.quantity < soLuong) {
            return Response.status(400)
                    .entity(new ErrorMsg("Số lượng tồn kho không đủ. Còn lại: " + inv.quantity))
                    .build();
        }
        inv.quantity -= soLuong;
        return Response.ok(inv).build();
    }

    // ── DTOs ────────────────────────────────────────────────────
    public record CapNhatTonKho(int soLuong, String location) {}
    public record ErrorMsg(String thongBao) {}
}