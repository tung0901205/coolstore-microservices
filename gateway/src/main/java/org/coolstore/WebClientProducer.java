package org.coolstore;

import io.vertx.ext.web.client.WebClient;
import io.vertx.mutiny.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class WebClientProducer {

    @Inject
    Vertx vertx;

    @Produces
    @ApplicationScoped
    public WebClient webClient() {
        return WebClient.create(vertx.getDelegate());
    }
}

