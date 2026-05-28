package org.coolstore;

import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.Route.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.WebClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProxyFilter {

    @Inject
    WebClient client;

    @Route(path = "/api/auth/*", methods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE})
    void proxyAuth(RoutingContext rc) {
        // Forward request sang auth-service (port 8089)
        client.request(rc.request().method(), 8089, "localhost", rc.request().uri())
                .putHeaders(rc.request().headers())
                .sendBuffer(rc.body().buffer())
                .onSuccess(res -> {
                    rc.response().setStatusCode(res.statusCode());
                    rc.response().headers().setAll(res.headers());
                    Buffer body = res.body();
                    if (body != null) {
                        rc.response().end(body);
                    } else {
                        rc.response().end();
                    }
                })
                .onFailure(err -> {
                    rc.response().setStatusCode(500).end("Gateway error: " + err.getMessage());
                });
    }

    @Route(path = "/api/products/*", methods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE})
    void proxyCatalog(RoutingContext rc) {
        client.request(rc.request().method(), 8082, "localhost", rc.request().uri())
                .putHeaders(rc.request().headers())
                .sendBuffer(rc.body() != null ? rc.body().buffer() : Buffer.buffer())
                .onSuccess(res -> {
                    rc.response().setStatusCode(res.statusCode());
                    rc.response().headers().setAll(res.headers());
                    Buffer body = res.body();
                    if (body != null) {
                        rc.response().end(body);
                    } else {
                        rc.response().end();
                    }
                })
                .onFailure(err -> {
                    rc.response().setStatusCode(502).end("Proxy error: " + err.getMessage());
                });
    }

    @Route(path = "/api/cart/*", methods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE})
    void proxyCart(RoutingContext rc) {
        client.request(rc.request().method(), 8084, "localhost", rc.request().uri())
                .putHeaders(rc.request().headers())
                .sendBuffer(rc.body() != null ? rc.body().buffer() : Buffer.buffer())
                .onSuccess(res -> {
                    rc.response().setStatusCode(res.statusCode());
                    rc.response().headers().setAll(res.headers());
                    Buffer body = res.body();
                    if (body != null) {
                        rc.response().end(body);
                    } else {
                        rc.response().end();
                    }
                })
                .onFailure(err -> {
                    rc.response().setStatusCode(502).end("Proxy error: " + err.getMessage());
                });
    }

    @Route(path = "/api/inventory/*", methods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE})
    void proxyInventory(RoutingContext rc) {
        client.request(rc.request().method(), 8081, "localhost", rc.request().uri())
                .putHeaders(rc.request().headers())
                .sendBuffer(rc.body() != null ? rc.body().buffer() : Buffer.buffer())
                .onSuccess(res -> {
                    rc.response().setStatusCode(res.statusCode());
                    rc.response().headers().setAll(res.headers());
                    Buffer body = res.body();
                    if (body != null) {
                        rc.response().end(body);
                    } else {
                        rc.response().end();
                    }
                })
                .onFailure(err -> {
                    rc.response().setStatusCode(502).end("Proxy error: " + err.getMessage());
                });
    }

    @Route(path = "/api/orders/*", methods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE})
    void proxyOrders(RoutingContext rc) {
        client.request(rc.request().method(), 8086, "localhost", rc.request().uri())
                .putHeaders(rc.request().headers())
                .sendBuffer(rc.body() != null ? rc.body().buffer() : Buffer.buffer())
                .onSuccess(res -> {
                    rc.response().setStatusCode(res.statusCode());
                    rc.response().headers().setAll(res.headers());
                    Buffer body = res.body();
                    if (body != null) {
                        rc.response().end(body);
                    } else {
                        rc.response().end();
                    }
                })
                .onFailure(err -> {
                    rc.response().setStatusCode(502).end("Proxy error: " + err.getMessage());
                });
    }

}

