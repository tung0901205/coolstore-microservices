package org.coolstore.payment.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * REST client để payment-service gọi order-service
 * sau khi nhận callback từ VNPay.
 */
@RegisterRestClient
@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
public interface OrderServiceClient {

    /**
     * Xác nhận đơn hàng đã thanh toán thành công.
     * Gọi sau khi VNPay callback với ResponseCode = 00.
     */
    @PUT
    @Path("/{orderId}/xac-nhan-thanh-toan")
    Response xacNhanThanhToan(
            @PathParam("orderId") String orderId,
            @QueryParam("maGiaoDich") String maGiaoDich
    );

    /**
     * Hủy đơn hàng khi thanh toán thất bại.
     * Gọi sau khi VNPay callback với ResponseCode != 00.
     */
    @PUT
    @Path("/{orderId}/huy-thanh-toan")
    Response huyThanhToan(@PathParam("orderId") String orderId);
}