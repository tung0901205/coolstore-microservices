package org.coolstore.auth.resource;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.coolstore.auth.model.AuthDTOs.KetQua;

/**
 * Bắt tất cả exception và trả về JSON có cấu trúc nhất quán.
 * Frontend sẽ luôn nhận được { thanhCong: false, thongBao: "..." }
 */
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        if (e instanceof BadRequestException) {
            return Response.status(400)
                    .entity(KetQua.loi(e.getMessage()))
                    .build();
        }
        if (e instanceof NotFoundException) {
            return Response.status(404)
                    .entity(KetQua.loi(e.getMessage()))
                    .build();
        }
        // Lỗi không mong muốn - không tiết lộ chi tiết kỹ thuật
        System.err.println("[AuthService] Lỗi hệ thống: " + e.getMessage());
        return Response.status(500)
                .entity(KetQua.loi("Lỗi hệ thống. Vui lòng thử lại sau."))
                .build();
    }
}