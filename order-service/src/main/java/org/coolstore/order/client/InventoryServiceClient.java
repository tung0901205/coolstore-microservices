package org.coolstore.order.client;

import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@Path("/api/inventory")
public interface InventoryServiceClient {

    record InventorySnapshot(String itemId, String location, int quantity, String link) {}

    @GET
    @Path("/{itemId}")
    InventorySnapshot layTheoItemId(@PathParam("itemId") String itemId);

    @PATCH
    @Path("/{itemId}/giam")
    Response giamSoLuong(@PathParam("itemId") String itemId, @QueryParam("soLuong") int soLuong);
}
