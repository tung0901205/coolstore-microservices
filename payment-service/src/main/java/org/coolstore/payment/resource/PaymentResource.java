package org.coolstore.payment.resource;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.coolstore.payment.client.OrderServiceClient;
import org.coolstore.payment.service.VnpayService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST API xử lý thanh toán VNPay.
 *
 * Luồng thanh toán:
 * 1. Frontend POST /api/payment/tao-url-thanh-toan (có JWT)
 *    → Backend tạo URL VNPay → Trả về URL cho frontend
 * 2. Frontend redirect user đến URL VNPay
 * 3. User thanh toán trên cổng VNPay
 * 4. VNPay GET /api/payment/vnpay-return → redirect người dùng về frontend
 * 5. VNPay POST /api/payment/vnpay-ipn → cập nhật order trong DB (quan trọng hơn)
 *
 * QUAN TRỌNG: IPN (Instant Payment Notification) là nguồn dữ liệu CHÍNH THỨC.
 * Return URL chỉ để UX, không nên dùng làm nguồn xác nhận thanh toán.
 */
@Path("/api/payment")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Thanh Toán VNPay", description = "Tích hợp cổng thanh toán VNPay")
public class PaymentResource {

    private static final Logger log = Logger.getLogger(PaymentResource.class);

    @Inject VnpayService vnpayService;
    @Inject JsonWebToken jwt;
    @RestClient OrderServiceClient orderClient;

    // ── DTO nội bộ ───────────────────────────────────────────────

    public record YeuCauThanhToan(
            String orderId,
            long soTienVnd,
            String moTaDonHang
    ) {}

    public record KetQuaThanhToan(
            boolean thanhCong,
            String thongBao,
            Object duLieu
    ) {
        static KetQuaThanhToan ok(String msg, Object d) { return new KetQuaThanhToan(true, msg, d); }
        static KetQuaThanhToan loi(String msg) { return new KetQuaThanhToan(false, msg, null); }
    }

    // ── Step 1: Tạo URL thanh toán ───────────────────────────────

    /**
     * Frontend gọi endpoint này để lấy URL VNPay.
     * Sau đó frontend tự redirect người dùng đến URL đó.
     *
     * Yêu cầu: JWT hợp lệ trong header Authorization: Bearer <token>
     */
    @POST
    @Path("/tao-url-thanh-toan")
    @Consumes(MediaType.APPLICATION_JSON)
    @Authenticated
    @Operation(summary = "Tạo URL thanh toán VNPay")
    public Response taoUrlThanhToan(YeuCauThanhToan request,
                                    @Context HttpHeaders headers) {
        // Lấy IP người dùng (quan trọng cho VNPay)
        String ip = layIpNguoiDung(headers);

        log.infof("Tạo URL VNPay: orderId=%s, soTien=%d, ip=%s",
                request.orderId(), request.soTienVnd(), ip);

        String url = vnpayService.taoUrlThanhToan(
                request.orderId(),
                request.soTienVnd(),
                request.moTaDonHang(),
                ip
        );

        // Log URL đầy đủ để kiểm tra
        log.info("VNPay redirect URL: " + url);

        Map<String, String> ket = new HashMap<>();
        ket.put("paymentUrl", url);
        ket.put("orderId", request.orderId());

        return Response.ok(KetQuaThanhToan.ok("URL thanh toán đã được tạo.", ket)).build();
    }

    // ── Step 4: Return URL (redirect từ VNPay về) ────────────────

    /**
     * VNPay redirect người dùng về URL này sau khi thanh toán.
     * Endpoint này chỉ dùng để UX - redirect về trang kết quả của frontend.
     *
     * KHÔNG xử lý nghiệp vụ quan trọng ở đây - dùng IPN cho việc đó.
     * Lý do: người dùng có thể đóng trình duyệt trước khi được redirect về.
     */
    @GET
    @Path("/vnpay-return")
    @Operation(summary = "VNPay Return URL - redirect sau thanh toán")
    public Response vnpayReturn(@Context UriInfo uriInfo) {
        Map<String, String> params = layRawQueryParams(uriInfo);

        String orderId    = params.get("vnp_TxnRef");
        String resCode    = params.get("vnp_ResponseCode");
        boolean hopLe     = vnpayService.xacThucChuKy(params);
        boolean thanhCong = hopLe && vnpayService.laGiaoDichThanhCong(params);

        log.infof("VNPay Return: orderId=%s, responseCode=%s, hopLe=%b, thanhCong=%b",
                orderId, resCode, hopLe, thanhCong);

        try {
            if (thanhCong) {
                orderClient.xacNhanThanhToan(
                        orderId,
                        params.getOrDefault("vnp_TransactionNo", "")
                );
                log.infof("Đơn hàng %s đã thanh toán thành công.", orderId);

            } else {
                orderClient.huyThanhToan(orderId);
                log.warnf("Thanh toán thất bại cho order %s", orderId);
            }

        } catch (Exception e) {
            log.errorf("Lỗi cập nhật order %s: %s",
                    orderId,
                    e.getMessage());
        }

        // Redirect về Angular frontend với query params để hiển thị kết quả
        String frontendUrl;
        if (thanhCong) {
            frontendUrl = "http://localhost:4200/thanh-toan/ket-qua"
                    + "?trangThai=thanh-cong"
                    + "&orderId=" + orderId
                    + "&maGiaoDich=" + params.getOrDefault("vnp_TransactionNo", "");
        } else {
            frontendUrl = "http://localhost:4200/thanh-toan/ket-qua"
                    + "?trangThai=that-bai"
                    + "&orderId=" + orderId
                    + "&maLoi=" + resCode;
        }

        return Response.temporaryRedirect(URI.create(frontendUrl)).build();
    }

    // ── Step 5: IPN Handler (VNPay gọi server của mình) ─────────

    /**
     * IPN = Instant Payment Notification.
     * VNPay gọi endpoint này BẤT KỂ người dùng có redirect về hay không.
     * Đây là nơi XỬ LÝ CHÍNH THỨC - cập nhật trạng thái đơn hàng trong DB.
     *
     * VNPay yêu cầu response phải là JSON {"RspCode":"00","Message":"Confirm Success"}
     * trong vòng 5 giây, nếu không VNPay sẽ gọi lại.
     *
     * LƯU Ý: URL IPN phải là URL public (không phải localhost).
     * Trong dev, dùng ngrok: ngrok http 8088
     */
    @GET
    @Path("/vnpay-ipn")
    @Operation(summary = "VNPay IPN Handler - xử lý xác nhận thanh toán")
    public Response vnpayIpn(@Context UriInfo uriInfo) {
        Map<String, String> params = layRawQueryParams(uriInfo);

        String orderId   = params.get("vnp_TxnRef");
        String resCode   = params.get("vnp_ResponseCode");
        String maGiaoDich = params.get("vnp_TransactionNo");
        String soTienStr = params.get("vnp_Amount");

        log.infof("VNPay IPN nhận được: orderId=%s, txnNo=%s, amount=%s, rspCode=%s",
                orderId, maGiaoDich, soTienStr, resCode);

        // Bước 1: Kiểm tra chữ ký - QUAN TRỌNG NHẤT
        if (!vnpayService.xacThucChuKy(params)) {
            log.errorf("VNPay IPN: Chữ ký KHÔNG HỢP LỆ cho orderId=%s", orderId);
            return Response.ok(Map.of(
                    "RspCode", "97",
                    "Message", "Invalid Signature"
            )).build();
        }

        // Bước 2: Kiểm tra kết quả giao dịch
        boolean thanhCong = vnpayService.laGiaoDichThanhCong(params);

        // Bước 3: Cập nhật đơn hàng trong order-service
        try {
            if (thanhCong) {
                // Thanh toán thành công → xác nhận đơn hàng
                orderClient.xacNhanThanhToan(orderId, maGiaoDich);
                log.infof("Đơn hàng %s đã được xác nhận thanh toán thành công.", orderId);
            } else {
                // Thanh toán thất bại → hủy đơn hàng
                orderClient.huyThanhToan(orderId);
                log.warnf("Đơn hàng %s bị hủy do thanh toán thất bại. ResponseCode: %s",
                        orderId, resCode);
            }
        } catch (Exception e) {
            // Không tìm thấy đơn hàng
            log.errorf("Không thể cập nhật đơn hàng %s: %s", orderId, e.getMessage());
            return Response.ok(Map.of(
                    "RspCode", "01",
                    "Message", "Order Not Found"
            )).build();
        }

        // Bước 4: Trả về "Confirm Success" cho VNPay
        return Response.ok(Map.of(
                "RspCode", "00",
                "Message", "Confirm Success"
        )).build();
    }

    // ── Helper ──────────────────────────────────────────────────

    /** Thu thập tất cả query params thành Map<String, String> */
//    private Map<String, String> layTatCaQueryParams(UriInfo uriInfo) {
//        return uriInfo.getQueryParameters()
//                .entrySet()
//                .stream()
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        e -> e.getValue().isEmpty() ? "" : e.getValue().get(0)
//                ));
//    }

    private Map<String, String> layRawQueryParams(UriInfo uriInfo) {

        String rawQuery = uriInfo.getRequestUri().getRawQuery();

        Map<String, String> map = new HashMap<>();

        if (rawQuery == null || rawQuery.isBlank()) {
            return map;
        }

        for (String pair : rawQuery.split("&")) {

            int idx = pair.indexOf("=");

            if (idx > 0) {

                String key = pair.substring(0, idx);
                String value = pair.substring(idx + 1);

                map.put(key, value);
            }
        }

        return map;
    }

    /** Lấy IP thực của người dùng (xử lý proxy/load balancer) */
    private String layIpNguoiDung(HttpHeaders headers) {
        // Thử X-Forwarded-For trước (khi có proxy)
        String xForwardedFor = headers.getHeaderString("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        // Fallback
        return "127.0.0.1";
    }
}