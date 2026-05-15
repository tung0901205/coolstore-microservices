package org.coolstore.auth.resource;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.coolstore.auth.model.AuthDTOs.KetQua;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        if (e instanceof BadRequestException) {
            return Response.status(400).entity(KetQua.loi(e.getMessage())).build();
        }
        if (e instanceof NotFoundException) {
            return Response.status(404).entity(KetQua.loi(e.getMessage())).build();
        }
        e.printStackTrace();
        return Response.status(500)
                .entity(KetQua.loi(e.getClass().getSimpleName() + ": " + e.getMessage()))
                .build();
    }
}