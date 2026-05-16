package org.coolstore.payment.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Dịch vụ tích hợp VNPay theo tài liệu chính thức:
 * https://sandbox.vnpayment.vn/apis/docs/thanh-toan-pay/pay.html
 *
 * Thuật toán chữ ký: HMAC-SHA512
 * Yêu cầu: Tất cả tham số phải được sắp xếp theo thứ tự từ điển (TreeMap)
 * trước khi tạo chữ ký.
 */
@ApplicationScoped
public class VnpayService {

    @ConfigProperty(name = "vnpay.tmn-code")
    String tmnCode;         // CMQOPO2R

    @ConfigProperty(name = "vnpay.hash-secret")
    String hashSecret;      // RSK421BZHJDRW19F6AJR0BFHH56HTF22

    @ConfigProperty(name = "vnpay.url")
    String vnpayUrl;        // https://sandbox.vnpayment.vn/paymentv2/vpcpay.html

    @ConfigProperty(name = "vnpay.return-url")
    String returnUrl;       // URL frontend nhận kết quả

    @ConfigProperty(name = "vnpay.ipn-url")
    String ipnUrl;          // URL backend nhận IPN

    @ConfigProperty(name = "vnpay.version", defaultValue = "2.1.0")
    String version;

    @ConfigProperty(name = "vnpay.timezone", defaultValue = "Asia/Ho_Chi_Minh")
    String timezone;

    /**
     * Tạo URL thanh toán VNPay để redirect người dùng.
     *
     * @param orderId    Mã đơn hàng (làm vnp_TxnRef)
     * @param soTienVnd  Số tiền VNĐ (ví dụ: 150000)
     * @param moTaDonHang Mô tả ngắn hiển thị trên trang VNPay
     * @param ipNguoiDung IP của người dùng (lấy từ request)
     * @return URL đầy đủ để redirect
     */
    public String taoUrlThanhToan(String orderId, long soTienVnd,
                                  String moTaDonHang, String ipNguoiDung) {
        // VNPay yêu cầu số tiền x100 (đơn vị: đồng -> xu)
        long soTienVnpay = soTienVnd * 100;

        // Thời gian tạo và hết hạn giao dịch (múi giờ VN)
        TimeZone tz = TimeZone.getTimeZone(timezone);
        Calendar cal = Calendar.getInstance(tz);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(tz);

        String thoiGianTao = sdf.format(cal.getTime());
        cal.add(Calendar.MINUTE, 15); // Giao dịch hết hạn sau 15 phút
        String thoiGianHetHan = sdf.format(cal.getTime());

        // Dùng TreeMap để TỰ ĐỘNG sắp xếp theo thứ tự từ điển (bắt buộc của VNPay)
        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version",    version);
        params.put("vnp_Command",    "pay");
        params.put("vnp_TmnCode",    tmnCode);
        params.put("vnp_Amount",     String.valueOf(soTienVnpay));
        params.put("vnp_CurrCode",   "VND");
        params.put("vnp_TxnRef",     orderId);         // Mã đơn hàng của mình
//        params.put("vnp_OrderInfo",  moTaDonHang);
        params.put("vnp_OrderInfo", "Thanh toan don hang " + orderId);
        params.put("vnp_OrderType",  "other");          // Loại hàng hóa (xem docs VNPay)
        params.put("vnp_Locale",     "vn");             // Ngôn ngữ trang VNPay
        params.put("vnp_ReturnUrl",  returnUrl);        // VNPay redirect sau thanh toán
        params.put("vnp_IpAddr",     ipNguoiDung != null ? ipNguoiDung : "127.0.0.1");
        params.put("vnp_CreateDate", thoiGianTao);
//        params.put("vnp_ExpireDate", thoiGianHetHan);

        // Tạo chuỗi hash (không URL encode value để hash)
//        String chuoiHashData = xayDungChuoiQuery(params, false);
        // Chuỗi để ký
//        String chuoiHashData = buildQueryRaw(params);
        String chuoiHashData = buildQueryEncoded(params);
        // Log chuỗi hash để kiểm tra
        Logger log = Logger.getLogger(VnpayService.class.getName());
        log.info("VNPay chuoiHashData: " + chuoiHashData);

        // Tạo chuỗi query (URL encode value để ghép URL)
//        String chuoiQuery = xayDungChuoiQuery(params, true);
        String chuoiQuery = buildQueryEncoded(params);

        // Ký HMAC-SHA512
        String secureHash = hmacSha512(hashSecret, chuoiHashData);

        log.info("VNPay secureHash: " + secureHash);
        // Ghép URL hoàn chỉnh
        return vnpayUrl + "?" + chuoiQuery + "&vnp_SecureHash=" + secureHash;
    }

    /**
     * Xác thực chữ ký từ callback của VNPay.
     *
     * QUAN TRỌNG: Phải gọi hàm này TRƯỚC khi tin tưởng bất kỳ dữ liệu nào từ VNPay.
     * Nếu chữ ký không hợp lệ → có thể bị giả mạo callback.
     *
     * @param params Tất cả query params từ VNPay gửi về
     * @return true nếu chữ ký hợp lệ
     */
    public boolean xacThucChuKy(Map<String, String> params) {
        String chukySanBay = params.get("vnp_SecureHash");
        if (chukySanBay == null || chukySanBay.isBlank()) {
            return false;
        }

        // Loại bỏ các tham số chữ ký trước khi tính lại
        Map<String, String> paramsDeHash = new TreeMap<>(params);
        paramsDeHash.remove("vnp_SecureHash");
        paramsDeHash.remove("vnp_SecureHashType");

        // Tính lại chữ ký từ params còn lại
//        String chuoiDeKy = xayDungChuoiQuery(paramsDeHash, false);
        String chuoiDeKy = buildQueryRaw(paramsDeHash);
        String chukyTinhToan = hmacSha512(hashSecret, chuoiDeKy);

        // So sánh case-insensitive
        return chukyTinhToan.equalsIgnoreCase(chukySanBay);
    }

    /**
     * Kiểm tra kết quả giao dịch từ VNPay.
     * vnp_ResponseCode = "00" nghĩa là THÀNH CÔNG.
     */
    public boolean laGiaoDichThanhCong(Map<String, String> params) {
        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");
        // Cả hai phải là "00"
        return "00".equals(responseCode) && "00".equals(transactionStatus);
    }

    // ── Helper methods ───────────────────────────────────────────

    /**
     * Xây dựng chuỗi query từ map param đã được sắp xếp.
     * @param encode true → URL encode value (dùng để ghép URL)
     *               false → không encode (dùng để tạo hash)
     */
//    private String xayDungChuoiQuery(Map<String, String> params, boolean encode) {
//        StringBuilder sb = new StringBuilder();
//        for (Map.Entry<String, String> entry : params.entrySet()) {
//            if (entry.getValue() == null || entry.getValue().isBlank()) continue;
//            if (!sb.isEmpty()) sb.append("&");
//            sb.append(entry.getKey()).append("=");
//            if (encode) {
//                sb.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
//            } else {
//                sb.append(entry.getValue());
//            }
//        }
//        return sb.toString();
//    }

    private String xayDungChuoiQuery(Map<String, String> params, boolean encode) {
        return params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // sắp xếp key
                .map(e -> e.getKey() + "=" + (
                        encode
                                ? URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8)
                                : e.getValue()
                ))
                .collect(Collectors.joining("&"));
    }

    // Chuỗi để ký (raw, không encode)
    private String buildQueryRaw(Map<String, String> params) {
        return params.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().isBlank()) // bỏ null/blank
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue()) // KHÔNG encode
                .collect(Collectors.joining("&"));
    }

    // Chuỗi query để gửi đi (encode)
//    private String buildQueryEncoded(Map<String, String> params) {
//        return params.entrySet().stream()
//                .filter(e -> e.getValue() != null && !e.getValue().isBlank())
//                .sorted(Map.Entry.comparingByKey())
//                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
//                .collect(Collectors.joining("&"));
//    }

    private String buildQueryEncoded(Map<String, String> params) {
        return params.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().isBlank())
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + vnpEncode(e.getValue()))
                .collect(Collectors.joining("&"));
    }

    private String vnpEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }


    /**
     * Tạo chữ ký HMAC-SHA512 theo yêu cầu VNPay.
     * Key là chuỗi bí mật (vnp_HashSecret).
     * Data là chuỗi query chưa encode.
     */
    private String hmacSha512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKey);
            byte[] hashBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Chuyển byte array thành hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo chữ ký HMAC-SHA512: " + e.getMessage(), e);
        }
    }
}